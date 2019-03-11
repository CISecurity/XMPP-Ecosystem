package org.cisecurity.sacm.xmpp.extensions.repo

import org.cisecurity.sacm.xmpp.extensions.repo.model.SacmRepository
import org.cisecurity.sacm.xmpp.extensions.repo.model.SacmRepositoryItemType
import org.cisecurity.sacm.xmpp.extensions.repo.model.SacmRepositoryItemTypeType
import org.slf4j.LoggerFactory
import rocks.xmpp.core.stanza.AbstractIQHandler
import rocks.xmpp.core.stanza.model.IQ

class ContentHandler extends AbstractIQHandler {

	def log = LoggerFactory.getLogger(ContentHandler.class)

	// For now, I think I just need to hard-code this
	// TODO Figure out how to configure and initialize a filesystem-based repository
	def baseDir     = "C:\\_Development\\Projects\\Standards\\XMPP-Ecosystem\\content"
	def benchmarks  = new File("${baseDir}\\benchmarks")
	def definitions = new File("${baseDir}\\definitions")

	def contentMap = [:]

	ContentHandler() { super(IQ.Type.GET) }

	/**
	 * Processes the IQ, after checking if the extension is enabled and after checking if the IQ has correct type,
	 * which is specified for the extension.
	 *
	 * @param iq The IQ request.
	 * @return The IQ response.
	 */
	@Override
	protected IQ processRequest(IQ iq) {
		log.info "Processing Request --> {http://cisecurity.org/sacm/repository}/content"

		if (contentMap.isEmpty()) {
			initialize()
		}

		SacmRepository.SacmRepositoryContentType content = iq.getExtension(SacmRepository.SacmRepositoryContentType.class)
		def found = {
			def rt = content.requestedType
			def ri = content.requestedItem

			if (rt && ri) {
				// Only content matching the type and item-name
				return contentMap.findAll { k, v -> k == ri && v["item"].type == rt }?.values()
			} else if (rt) {
				// Only content matching the type
				return contentMap.findAll { k, v -> v["item"].type == rt }?.values()
			} else if (ri) {
				// Only content matching the item-name
				return contentMap.findAll { k, v -> k == ri }?.values()
			} else {
				// All content
				return contentMap.values()
			}
		}.call()

		def rez = new SacmRepository.SacmRepositoryContentType()

		// This will be an array of maps; Construct the result
		if (found) {
			found.each { i ->
				rez.item << new SacmRepositoryItemType(name: i["item"].name, type: i["item"].type)
			}
		}
		return iq.createResult(rez)
	}

	/**
	 * Read the files from the filesystem and categorize them by type
	 * @return
	 */
	def initialize() {
		benchmarks.eachFile { f ->
			contentMap[f.name] = [
				"item": new SacmRepositoryItemType(
					name: f.name,
					type: SacmRepositoryItemTypeType.SCAP),
				"filenames": f.isDirectory() ? f.listFiles().collect { fi -> fi.name } : [f.name]
			]
		}

		definitions.eachFile { f ->
			contentMap[f.name] = [
				"item": new SacmRepositoryItemType(
					name: f.name,
					type: SacmRepositoryItemTypeType.OVAL),
				"filenames": f.isDirectory() ? f.listFiles().collect { fi -> fi.name } : [f.name]
			]
		}

		log.info "Content Map --> ${contentMap}"
	}
}
