package org.cisecurity.sacm.xmpp.client.orchestrator

import org.cisecurity.sacm.xmpp.client.XmppClientBase
import org.cisecurity.sacm.xmpp.extensions.assessment.SacmAssessmentManager
import org.cisecurity.sacm.xmpp.extensions.assessment.model.Assessment
import org.cisecurity.sacm.xmpp.extensions.assessment.model.Xccdf
import org.cisecurity.sacm.xmpp.extensions.repo.SacmRepositoryManager
import org.cisecurity.sacm.xmpp.extensions.repo.model.SacmRepository
import rocks.xmpp.addr.Jid
import rocks.xmpp.core.session.Extension
import rocks.xmpp.core.session.XmppSessionConfiguration
import rocks.xmpp.core.session.debug.ConsoleDebugger
import rocks.xmpp.util.concurrent.AsyncResult

class OrchestratorXmppClient extends XmppClientBase {

	//
	// STRUCTURAL
	//

	/**
	 * The orchestrator does just that, orchestrates collection and evaluation and query/storage of
	 * information from/to a repository.  The orchestrator, much like the conductor of an orchestra, controls
	 * the flow of information between components, and cues those components when necessary, to perform
	 * their work.
	 * @return the XMPP Session Configuration, potentially with extensions/managers enabled.
	 */
	@Override
	XmppSessionConfiguration createXmppSessionConfiguration() {
		return XmppSessionConfiguration.builder()
			.extensions(
				// Talk to a repository...
				Extension.of(SacmRepository.NAMESPACE, SacmRepositoryManager.class, false, SacmRepository.class),
			Extension.of(Assessment.NAMESPACE, SacmAssessmentManager.class, true, Assessment.class)
			)
			.debugger(ConsoleDebugger.class)
			.build()
	}

	//
	// OPERATIONAL
	//

	/**
	 * Ask a repository entity for a listing of unique content types.
	 * @param repositoryJid
	 * @return
	 */
	def listRepositoryContentTypes(Jid repositoryJid) {
		SacmRepositoryManager srm = xmppClient.getManager(SacmRepositoryManager.class)
		SacmRepository.SacmRepositoryContentTypeType repo = srm.listContentTypes(repositoryJid).getResult()
		repo.value.each { i ->
			log.info "SACM Content Type: ${i.contentType}"
		}
		return repo
	}

	def listRepositoryContent(Jid repositoryJid) {
		return listRepositoryContent(repositoryJid, null, null)
	}

	def listRepositoryContent(Jid repositoryJid, String itemType) {
		return listRepositoryContent(repositoryJid, itemType, null)
	}

	def listRepositoryContent(Jid repositoryJid, String itemType, String itemName) {
		SacmRepositoryManager srm = xmppClient.getManager(SacmRepositoryManager.class)
		SacmRepository.SacmRepositoryContentType repo = srm.listRepositoryItems(repositoryJid, itemType, itemName).getResult()
		repo.item.each { i ->
			log.info "SACM Content Item: ${i.assessmentContentId}; Name: ${i.contentName}; Type: ${i.contentTypeCode.toString()}"
		}
		return repo
	}

	def tellAnEndpointToPerformAnAssessment(Jid endpointJid, def parameters = [:]) {
		def mgr = xmppClient.getManager(SacmAssessmentManager.class)
		Assessment rez = mgr.performAssessment(endpointJid, parameters).result
		return rez
	}
}
