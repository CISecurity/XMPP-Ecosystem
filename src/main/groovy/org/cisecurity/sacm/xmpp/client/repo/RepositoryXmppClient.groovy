package org.cisecurity.sacm.xmpp.client.repo

import org.cisecurity.sacm.xmpp.repo.DatabaseConnection
import org.cisecurity.sacm.xmpp.client.XmppClientBase
import org.cisecurity.sacm.xmpp.extensions.repo.SacmRepositoryManager
import org.cisecurity.sacm.xmpp.extensions.repo.model.SacmRepository
import rocks.xmpp.addr.Jid
import rocks.xmpp.core.session.Extension
import rocks.xmpp.core.session.XmppSessionConfiguration
import rocks.xmpp.core.session.debug.ConsoleDebugger

class RepositoryXmppClient extends XmppClientBase {

	/**
	 * The repository XMPP client advertises its use of the SACM Repository extension.  Service
	 * Discovery requests of a repository client will receive a <code>feature</code> element
	 * containing a `var` attribute value of "http://cisecurity.org/sacm/repository"
	 * @return the XMPP Session Configuration, with the SACM repository extension enabled.
	 */
	@Override
	XmppSessionConfiguration createXmppSessionConfiguration() {
		return XmppSessionConfiguration.builder()
			.extensions(
				Extension.of(SacmRepository.NAMESPACE, SacmRepositoryManager.class, true, SacmRepository.class))
			.debugger(ConsoleDebugger.class)
			.build()
	}


	//
	// OPERATIONAL
	//

	/**
	 * Provide file-based content, stored in a repository, to the endpoint JID
	 * @param endpointJid
	 * @param assessmentContentId
	 * @return
	 */
	def transferRepositoryContent(Jid endpointJid, String assessmentContentId) {
		SacmRepositoryManager srm = xmppClient.getManager(SacmRepositoryManager.class)
		SacmRepository.SacmRepositoryContentRequestType request = srm.requestRepositoryContent(endpointJid, assessmentContentId).result
		log.info "SACM Content Request: Endpoint: ${endpointJid}; Content ID: ${assessmentContentId}; Success? ${request.success}"
		return request
	}
}
