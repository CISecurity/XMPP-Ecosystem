package org.cisecurity.sacm.xmpp.extensions.repo

import org.cisecurity.sacm.xmpp.extensions.repo.model.SacmRepository
import org.cisecurity.sacm.xmpp.extensions.repo.model.SacmRepositoryItemType
import org.cisecurity.sacm.xmpp.extensions.repo.model.SacmRepositoryItemTypeType
import org.slf4j.LoggerFactory
import rocks.xmpp.addr.Jid
import rocks.xmpp.core.session.Manager
import rocks.xmpp.core.session.XmppSession
import rocks.xmpp.core.stanza.model.IQ
import rocks.xmpp.util.concurrent.AsyncResult

class SacmRepositoryManager extends Manager {

	def log = LoggerFactory.getLogger(SacmRepositoryManager.class)



	private SacmRepositoryManager(final XmppSession xmppSession) {
		super(xmppSession)

//		iqHandler = new AbstractIQHandler(IQ.Type.GET) {
//			/**
//			 * If someone asks me to list available assessment content, reply.
//			 * @param iq
//			 * @return
//			 */
//			@Override
//			protected IQ processRequest(IQ iq) {
//				synchronized (this) {
//					SacmRepository sr = iq.getExtension(SacmRepository.class)
//					if (sr.content.item?.size() > 0) {
//						// The calling client is requesting a specific item.
//						SacmRepositoryItemType requestedItem = sr.content.item[0]
//						def repositoryItem = {
//							def b = benchmarksMap.find { k, v -> k == requestedItem.name && v["item"].type == requestedItem.type }
//							if (b) {
//								return b
//							} else {
//								def d = definitionsMap.find { k, v -> k == requestedItem.name && v["item"].type == requestedItem.type }
//								if (d) {
//									return d
//								} else {
//									return null
//								}
//							}
//						}.call()
//
//						if (repositoryItem) {
//							log.info "Query for Repository Item: ${requestedItem.name} --> ${repositoryItem.value["filenames"]}"
//
//							SacmRepository sacmRepository = new SacmRepository()
//							SacmRepositoryContentType content = new SacmRepositoryContentType()
//							content.item << repositoryItem.value["item"]
//							sacmRepository.content = content
//
//							return iq.createResult(sacmRepository)
//						} else {
//							return iq.createError(
//								new StanzaError(
//									Condition.ITEM_NOT_FOUND,
//									"SACM Content not found for request: ${requestedItem.name}/${requestedItem.type}"))
//						}
//
//					} else {
//						SacmRepositoryContentType content = new SacmRepositoryContentType()
//						content.item.addAll(benchmarksMap.collect { k, v -> v["item"] })
//						content.item.addAll(definitionsMap.collect { k, v -> v["item"] })
//						SacmRepository sacmRepository = new SacmRepository()
//						sacmRepository.content = content
//
//						return iq.createResult(sacmRepository)
//					}
//				}
//			}
//		}
	}

	/**
	 * Called when the manager is enabled.
	 */
	@Override
	protected void onEnable() {
		super.onEnable()

		xmppSession.addIQHandler(SacmRepository.SacmRepositoryContentTypeType.class, new ContentTypeHandler())
		xmppSession.addIQHandler(SacmRepository.SacmRepositoryContentType.class, new ContentHandler())
		xmppSession.addIQHandler(SacmRepository.SacmRepositoryContentRequestType.class, new ContentRequestHandler())
	}

	/**
	 * Called when the manager is disabled.
	 */
	@Override
	protected void onDisable() {
		super.onDisable()

		xmppSession.removeIQHandler(SacmRepository.SacmRepositoryContentTypeType.class)
		xmppSession.removeIQHandler(SacmRepository.SacmRepositoryContentType.class)
		xmppSession.removeIQHandler(SacmRepository.SacmRepositoryContentRequestType.class)
	}

	//
	// "Outbound" IQs.  These methods are the request interfaces from this manager to another entity
	//

	/**
	 * Request from the repository JID, all available content types
	 * @param jid - the JID representing a SACM content repository interface
	 * @return the async result with the listing of available content types at that repository JID
	 */
	AsyncResult<SacmRepository.SacmRepositoryContentTypeType> listContentTypes(Jid jid) {
		return xmppSession.query(
			IQ.get(jid, new SacmRepository.SacmRepositoryContentTypeType()),
			SacmRepository.SacmRepositoryContentTypeType.class)
	}

	/**
	 * Lists all SACM content in the repository owned by the specified JID
	 * @param jid - the JID representing a SACM content repository interface
	 * @return the async result with the listing of available content at that repository JID
	 */
	AsyncResult<SacmRepository.SacmRepositoryContentType> listRepositoryItems(Jid jid) {
		return listRepositoryItems(jid, null, null)
	}
	/**
	 * Lists all SACM content in the repository owned by the specified JID
	 * @param jid - the JID representing a SACM content repository interface
	 * @param requestedType - filter the results of the query by content type
	 * @return the async result with the listing of available content at that repository JID
	 */
	AsyncResult<SacmRepository.SacmRepositoryContentType> listRepositoryItems(Jid jid, String requestedType) {
		return listRepositoryItems(jid, requestedType, null)
	}
	AsyncResult<SacmRepository.SacmRepositoryContentType> listRepositoryItems(Jid jid, String requestedType, String requestedItem) {
		def requestedContentType = requestedType ? SacmRepositoryItemTypeType.fromValue(requestedType.toUpperCase()) : null

		SacmRepository.SacmRepositoryContentType query =
			new SacmRepository.SacmRepositoryContentType(
				requestedType: requestedContentType,
				requestedItem: requestedItem)

		return xmppSession.query(IQ.get(jid, query), SacmRepository.SacmRepositoryContentType.class)
	}

	/**
	 * Initiate a file transfer request to the repository JID, for files associated with a named item and content type.
	 * The optional recipient JID allows the requestor to tell the repository to forward the files to another entity.
	 * When the recipient JID is null, the file transfer offering goes to the requestor
	 * @param repositoryJid - the JID representing a SACM content repository interface
	 * @param recipientJid - an optional full JID representing the entity who should actually receive the files.  When
	 * null, the recipient is just the entity making this call.
	 * @param itemName - the name of the SACM content being requested
	 * @param itemType - the type of the content being requested (SCAP, OVAL, etc)
	 * @return An async result basically indicating either a successful request or failure.  If the request is successful,
	 * the repository JID will initiate the file transfer separately from this request.
	 */
	AsyncResult<SacmRepository.SacmRepositoryContentRequestType> requestRepositoryContent(Jid repositoryJid, Jid recipientJid, String requestedType, String requestedItem) {
		def requestedContentType = SacmRepositoryItemTypeType.fromValue(requestedType.toUpperCase())

		SacmRepository.SacmRepositoryContentRequestType contentRequest =
			new SacmRepository.SacmRepositoryContentRequestType(
				requestedType: requestedContentType,
				requestedItem: requestedItem)

		// If the intended recipient JID is specified, add it.  Otherwise, the IQ handler will take care of routing
		// back to the calling JID.
		if (recipientJid) {
			log.info "Recipient JID --> ${recipientJid.toString()}"
			contentRequest.toJid = recipientJid.toString()
		}

		return xmppSession.query(IQ.get(repositoryJid, requestedContentType), SacmRepository.SacmRepositoryContentRequestType.class)
	}
}
