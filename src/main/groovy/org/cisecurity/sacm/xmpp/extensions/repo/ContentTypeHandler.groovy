package org.cisecurity.sacm.xmpp.extensions.repo

import org.cisecurity.sacm.xmpp.extensions.repo.model.SacmRepository
import org.cisecurity.sacm.xmpp.extensions.repo.model.SacmRepositoryContentTypeValueType
import org.cisecurity.sacm.xmpp.extensions.repo.model.SacmRepositoryItemTypeType
import org.slf4j.LoggerFactory
import rocks.xmpp.core.stanza.AbstractIQHandler
import rocks.xmpp.core.stanza.model.IQ

class ContentTypeHandler extends AbstractIQHandler {

	def log = LoggerFactory.getLogger(ContentTypeHandler.class)

	ContentTypeHandler() { super(IQ.Type.GET) }

	/**
	 * Processes the IQ, after checking if the extension is enabled and after checking if the IQ has correct type,
	 * which is specified for the extension.
	 *
	 * @param iq The IQ request.
	 * @return The IQ response.
	 */
	@Override
	protected IQ processRequest(IQ iq) {
		log.info "Processing Request --> {http://cisecurity.org/sacm/repository}/content_type"

		//
		// For PoC purposes, we're just adding every content type in the enumeration
		//
		def rez = new SacmRepository.SacmRepositoryContentTypeType()
		[SacmRepositoryItemTypeType.SCAP, SacmRepositoryItemTypeType.OVAL, SacmRepositoryItemTypeType.SACM, SacmRepositoryItemTypeType.YANG].each {
			rez.value << new SacmRepositoryContentTypeValueType(contentType: it.value())
		}

		return iq.createResult(rez)
	}
}
