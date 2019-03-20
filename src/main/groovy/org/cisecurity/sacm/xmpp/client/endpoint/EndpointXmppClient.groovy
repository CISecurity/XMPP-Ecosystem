package org.cisecurity.sacm.xmpp.client.endpoint

import org.cisecurity.sacm.xmpp.client.XmppClientBase
import org.cisecurity.sacm.xmpp.extensions.assessment.SacmAssessmentManager
import org.cisecurity.sacm.xmpp.extensions.assessment.model.Assessment
import org.cisecurity.sacm.xmpp.extensions.repo.SacmRepositoryManager
import org.cisecurity.sacm.xmpp.extensions.repo.model.SacmRepository
import rocks.xmpp.core.session.Extension
import rocks.xmpp.core.session.XmppSessionConfiguration
import rocks.xmpp.core.session.debug.ConsoleDebugger
import rocks.xmpp.extensions.filetransfer.FileTransfer
import rocks.xmpp.extensions.filetransfer.FileTransferManager

import java.nio.file.Paths
import java.util.concurrent.TimeUnit

class EndpointXmppClient extends XmppClientBase {

	def assessmentCapabilities = ["SCAP", "OVAL"]

	/**
	 * The endpoint needs to enable the repository extension in order to receive files.  The
	 * orchestrator will send a message to the repository telling it to "send content X to the
	 * endpoint JID"
	 * @return the XMPP Session Configuration, potentially with extensions/managers enabled.
	 */
	@Override
	XmppSessionConfiguration createXmppSessionConfiguration() {
		return XmppSessionConfiguration.builder()
			.extensions(
				// Talk to a repository...
				Extension.of(SacmRepository.NAMESPACE, SacmRepositoryManager.class, true, SacmRepository.class),
				Extension.of(Assessment.NAMESPACE, SacmAssessmentManager.class, true, Assessment.class)
			)
			.debugger(ConsoleDebugger.class)
			.build()
	}

	/**
	 * Configure any XMPP extension-based listeners, such as file transfer offer listeners
	 * or assessment request listeners, etc.
	 */
	@Override
	def configureExtensionBasedListeners() {
		//
		// FILE TRANSFER OFFER LISTENER.
		//
		xmppClient.getManager(FileTransferManager.class).addFileTransferOfferListener { e ->

			log.info "ACCEPTING FILE TRANSFER FROM JID: ${e.initiator}"
			FileTransfer ft = e.accept(Paths.get("endpoint_content", e.getName())).result

			log.info "ADDING FILE TRANSFER STATUS LISTENER"
			ft.addFileTransferStatusListener { l ->
				if (l.status == FileTransfer.Status.COMPLETED) { println "${l.status}; ${l.bytesTransferred}" }
			}

			log.info "TRANSFERRING"
			ft.transfer().get(15, TimeUnit.SECONDS)

			log.info "EXCEPTION?"
			if (ft.exception) { log.error "File Transfer Exception", ft.exception }

			log.info "File Transfer --> Status: ${ft.status}; Bytes: ${ft.bytesTransferred}"
		}
	}

	// Want to be able to load pre-configured assessors
	// Want to be able to register new assessors

	@Override
	def postConnect() {

	}
}
