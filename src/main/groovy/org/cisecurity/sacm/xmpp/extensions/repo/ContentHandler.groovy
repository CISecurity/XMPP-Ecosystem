package org.cisecurity.sacm.xmpp.extensions.repo

import org.cisecurity.sacm.xmpp.extensions.repo.model.SacmRepository
import org.cisecurity.sacm.xmpp.extensions.repo.model.SacmRepositoryContentTypeCodeType
import org.cisecurity.sacm.xmpp.extensions.repo.model.SacmRepositoryItemType

import org.cisecurity.sacm.xmpp.repo.DatabaseConnection
import org.slf4j.LoggerFactory
import rocks.xmpp.core.stanza.AbstractIQHandler
import rocks.xmpp.core.stanza.model.IQ

class ContentHandler extends AbstractIQHandler {

	def log = LoggerFactory.getLogger(ContentHandler.class)

	DatabaseConnection databaseConnection = new DatabaseConnection()

	private final String CONTENT_QUERY_BASE = """
SELECT assessment_content_id, content_type_code, content_filepath_id, content_name
  FROM `sacm-xmpp`.assessmentcontent
 WHERE 1=1
"""

	ContentHandler() { super(IQ.Type.GET) }

	ContentHandler(DatabaseConnection databaseConnection) {
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
		log.info "Processing Request --> {http://cisecurity.org/sacm/repository}/content"

		SacmRepository.SacmRepositoryContentType content = iq.getExtension(SacmRepository.SacmRepositoryContentType.class)
		def rt = content.requestedType
		def ri = content.requestedItem

		def binds = []
		def query = CONTENT_QUERY_BASE
		if (rt) {
			query += " AND content_type_code = ?"
			binds << rt.value()
		}

		if (ri) {
			query += " AND content_name = ?"
			binds << ri
		}

		def rez = new SacmRepository.SacmRepositoryContentType()

		def contentRows = databaseConnection.query(query, binds)
		contentRows.each { r ->
			rez.item <<
				new SacmRepositoryItemType(
					assessmentContentId: Integer.valueOf(r["assessment_content_id"]),
					contentName: r["content_name"],
					contentTypeCode: SacmRepositoryContentTypeCodeType.fromValue(r["content_type_code"]))
		}
		return iq.createResult(rez)
	}
}
