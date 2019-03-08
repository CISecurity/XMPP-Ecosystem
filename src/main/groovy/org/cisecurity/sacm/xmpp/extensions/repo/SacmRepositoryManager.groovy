package org.cisecurity.sacm.xmpp.extensions.repo

import org.cisecurity.sacm.xmpp.extensions.repo.model.SACMRepositoryContentType
import org.cisecurity.sacm.xmpp.extensions.repo.model.SACMRepositoryItemType
import org.cisecurity.sacm.xmpp.extensions.repo.model.SACMRepositoryItemTypeType
import org.cisecurity.sacm.xmpp.extensions.repo.model.SacmRepository
import org.slf4j.LoggerFactory
import rocks.xmpp.addr.Jid
import rocks.xmpp.core.session.Manager
import rocks.xmpp.core.session.XmppSession
import rocks.xmpp.core.stanza.AbstractIQHandler
import rocks.xmpp.core.stanza.IQHandler
import rocks.xmpp.core.stanza.model.IQ
import rocks.xmpp.core.stanza.model.StanzaError
import rocks.xmpp.core.stanza.model.errors.Condition
import rocks.xmpp.util.concurrent.AsyncResult

class SacmRepositoryManager extends Manager {

	def log = LoggerFactory.getLogger(SacmRepositoryManager.class)

	// For now, I think I just need to hard-code this
	// TODO Figure out how to configure and initialize a filesystem-based repository
	def baseDir     = "C:\\_Development\\Projects\\Standards\\XMPP-Ecosystem\\content"
	def benchmarks  = new File("${baseDir}\\benchmarks")
	def definitions = new File("${baseDir}\\definitions")

	def benchmarksMap  = [:]
	def definitionsMap = [:]

	private final IQHandler iqHandler

	private SacmRepositoryManager(final XmppSession xmppSession) {
		super(xmppSession)

		iqHandler = new AbstractIQHandler(IQ.Type.GET) {
			/**
			 * If someone asks me to list available assessment content, reply.
			 * @param iq
			 * @return
			 */
			@Override
			protected IQ processRequest(IQ iq) {
				synchronized (this) {
					SacmRepository sr = iq.getExtension(SacmRepository.class)
					if (sr.content.item?.size() > 0) {
						// The calling client is requesting a specific item.
						SACMRepositoryItemType requestedItem = sr.content.item[0]
						def repositoryItem = {
							def b = benchmarksMap.find { k, v -> k == requestedItem.name && v["item"].type == requestedItem.type }
							if (b) {
								return b
							} else {
								def d = definitionsMap.find { k, v -> k == requestedItem.name && v["item"].type == requestedItem.type }
								if (d) {
									return d
								} else {
									return null
								}
							}
						}.call()

						if (repositoryItem) {
							log.info "Query for Repository Item: ${requestedItem.name} --> ${repositoryItem.value["filenames"]}"

							SacmRepository sacmRepository = new SacmRepository()
							SACMRepositoryContentType content = new SACMRepositoryContentType()
							content.item << repositoryItem.value["item"]
							sacmRepository.content = content

							return iq.createResult(sacmRepository)
						} else {
							return iq.createError(
								new StanzaError(
									Condition.ITEM_NOT_FOUND,
									"SACM Content not found for request: ${requestedItem.name}/${requestedItem.type}"))
						}

					} else {
						SACMRepositoryContentType content = new SACMRepositoryContentType()
						content.item.addAll(benchmarksMap.collect { k, v -> v["item"] })
						content.item.addAll(definitionsMap.collect { k, v -> v["item"] })
						SacmRepository sacmRepository = new SacmRepository()
						sacmRepository.content = content

						return iq.createResult(sacmRepository)
					}
				}
			}
		}
	}

	/**
	 * Called when the manager is enabled.
	 */
	@Override
	protected void onEnable() {
		super.onEnable()

		xmppSession.addIQHandler(SacmRepository.class, iqHandler)
	}

	/**
	 * Called when the manager is disabled.
	 */
	@Override
	protected void onDisable() {
		super.onDisable()

		xmppSession.removeIQHandler(SacmRepository.class)
	}

	/**
	 * Initializes the manager. Logic which shouldn't be in the constructor can go here.
	 * This allows thread-safe construction of objects, e.g. when you need to publish the "this" reference.
	 *
	 * @see <a href="http://www.ibm.com/developerworks/library/j-jtp0618/">Java theory and practice: Safe construction techniques</a>
	 */
	@Override
	protected void initialize() {
		super.initialize()

		benchmarks.eachFile { f ->
			benchmarksMap[f.name] = [
				"item": new SACMRepositoryItemType(
					name: f.name,
					id: f.name.replace(" ", "_"),
					type: SACMRepositoryItemTypeType.SCAP),
				"filenames": f.isDirectory() ? f.listFiles().collect { fi -> fi.name } : [f.name]
			]
		}

		log.info "Benchmarks Map --> ${benchmarksMap}"

		definitions.eachFile { f ->
			definitionsMap[f.name] = [
				"item": new SACMRepositoryItemType(
					name: f.name,
					id: f.name.replace(" ", "_"),
					type: SACMRepositoryItemTypeType.OVAL),
				"filenames": f.isDirectory() ? f.listFiles().collect { fi -> fi.name } : [f.name]
			]
		}

		log.info "Definitions Map --> ${definitionsMap}"
	}

	/**
	 * Lists all SACM content in the repository owned by the specified user
	 * @param jid - the JID representing a SACM content repository interface
	 * @return the async result with the listing of available content at that repository JID
	 */
	AsyncResult<SacmRepository> listRepositoryContent(Jid jid) {
		def sr = new SacmRepository()
		sr.content = new SACMRepositoryContentType()
		return xmppSession.query(IQ.get(jid, sr), SacmRepository.class)
	}

	/**
	 * Get named SACM content
	 * @param jid - the JID representing a SACM content repository interface
	 * @param itemName - the name of the SACM content being requested
	 * @return the async result with the named content at that repository JID
	 */
	AsyncResult<SacmRepository> getRepositoryContent(Jid jid, String itemName, String itemType) {
		def sr = new SacmRepository()
		sr.content = new SACMRepositoryContentType()
		sr.content.item << new SACMRepositoryItemType(
			name: itemName,
			type: SACMRepositoryItemTypeType.fromValue(itemType.toUpperCase()))
		return xmppSession.query(IQ.get(jid, sr), SacmRepository.class)
	}
}
