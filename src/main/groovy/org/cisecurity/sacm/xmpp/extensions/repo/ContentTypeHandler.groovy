package org.cisecurity.sacm.xmpp.extensions.repo

import org.cisecurity.sacm.xmpp.extensions.repo.model.SacmRepository
import org.cisecurity.sacm.xmpp.extensions.repo.model.SacmRepositoryContentTypeValueType

import org.cisecurity.sacm.xmpp.repo.DatabaseConnection
import org.slf4j.LoggerFactory
import rocks.xmpp.core.stanza.AbstractIQHandler
import rocks.xmpp.core.stanza.model.IQ

class ContentTypeHandler extends AbstractIQHandler {

	def log = LoggerFactory.getLogger(ContentTypeHandler.class)

	DatabaseConnection databaseConnection = new DatabaseConnection()

	ContentTypeHandler() { super(IQ.Type.GET) }

	ContentTypeHandler(DatabaseConnection databaseConnection) {
		super(IQ.Type.GET)

		this.databaseConnection = databaseConnection
	}

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

		def rez = []
		databaseConnection.repository.each { r ->
			rez << r["content_type_code"]
		}

		//
		// For PoC purposes, we're just adding every content type in the enumeration
		//
		def result = new SacmRepository.SacmRepositoryContentTypeType()
		rez.unique().each { v ->
			result.value << new SacmRepositoryContentTypeValueType(contentType: v)
		}

		return iq.createResult(result)
	}
}
