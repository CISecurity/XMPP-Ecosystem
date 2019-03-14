package org.cisecurity.sacm.xmpp.client.endpoint

import org.cisecurity.sacm.xmpp.client.XmppClientBase
import rocks.xmpp.extensions.filetransfer.FileTransfer
import rocks.xmpp.extensions.filetransfer.FileTransferManager

import java.nio.file.Paths
import java.util.concurrent.TimeUnit

class EndpointXmppClient extends XmppClientBase {

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
			FileTransfer ft = e.accept(Paths.get(e.getName())).result

			log.info "ADDING FILE TRANSFER STATUS LISTENER"
			ft.addFileTransferStatusListener { l ->
				println "${l.status}; ${l.bytesTransferred}"
			}

			log.info "TRANSFERRING"
			ft.transfer().get(15, TimeUnit.SECONDS)

			log.info "EXCEPTION?"
			if (ft.exception) { log.error "File Transfer Exception", ft.exception }

			log.info "File Transfer --> Status: ${ft.status}; Bytes: ${ft.bytesTransferred}"
		}
	}
}
