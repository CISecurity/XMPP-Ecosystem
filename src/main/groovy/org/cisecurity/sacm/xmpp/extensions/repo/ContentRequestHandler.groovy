package org.cisecurity.sacm.xmpp.extensions.repo

import org.cisecurity.sacm.xmpp.extensions.repo.model.SacmRepository
import org.cisecurity.sacm.xmpp.repo.DatabaseConnection
import org.slf4j.LoggerFactory
import rocks.xmpp.addr.Jid
import rocks.xmpp.core.session.XmppSession
import rocks.xmpp.core.stanza.AbstractIQHandler
import rocks.xmpp.core.stanza.model.IQ
import rocks.xmpp.core.stanza.model.StanzaError
import rocks.xmpp.core.stanza.model.errors.Condition
import rocks.xmpp.extensions.filetransfer.FileTransfer
import rocks.xmpp.extensions.filetransfer.FileTransferManager

import java.time.Duration

class ContentRequestHandler extends AbstractIQHandler {

	def log = LoggerFactory.getLogger(ContentRequestHandler.class)

	XmppSession         xmppSession
	DatabaseConnection  databaseConnection = new DatabaseConnection()
	FileTransferManager fileTransferManager

	private final String CONTENT_QUERY = """
SELECT
    C.assessment_content_id, 
    CONCAT(F.content_path, "\\\\", F.content_filename) AS content_filepath
FROM 
    `sacm-xmpp`.assessmentcontent as C, 
    `sacm-xmpp`.assessmentcontentfilepath as F
WHERE
    C.content_filepath_id = F.content_filepath_id AND
    C.assessment_content_id = ?
ORDER BY C.content_filepath_id, F.content_filepath_seq;
"""

	ContentRequestHandler() { super(IQ.Type.GET) }

	ContentRequestHandler(XmppSession xmppSession, DatabaseConnection databaseConnection) {
		super(IQ.Type.GET)

		this.xmppSession        = xmppSession
		this.databaseConnection = databaseConnection

		fileTransferManager = xmppSession.getManager(FileTransferManager.class)
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
		log.info "Processing Request --> {http://cisecurity.org/sacm/repository}/content_request"

		SacmRepository.SacmRepositoryContentRequestType contentRequest =
			iq.getExtension(SacmRepository.SacmRepositoryContentRequestType.class)

		String assessmentContentId = contentRequest.getAssessmentContentId()
		String toJid               = contentRequest.getToJid()

		def contentRows = databaseConnection.query(CONTENT_QUERY, [assessmentContentId])
		contentRows.each { r ->
			initiateTransfer(Jid.of(toJid), r["content_filepath"])
		}

		def rez = new SacmRepository.SacmRepositoryContentRequestType(success: true)
		return iq.createResult(rez)
	}

	def initiateTransfer(Jid toJid, String filepath) {
		File source = new File(filepath)

		FileTransfer fileTransfer =
			fileTransferManager.offerFile(
				source,
				source.name,
				toJid,
				Duration.ofSeconds(60, 0)).result
		return fileTransfer.transfer()
	}
}
