package org.cisecurity.sacm.xmpp.client.orchestrator

import org.cisecurity.sacm.xmpp.client.XmppClientBase
import org.cisecurity.sacm.xmpp.extensions.assessment.SacmAssessmentManager
import org.cisecurity.sacm.xmpp.extensions.assessment.model.Assessment
import org.cisecurity.sacm.xmpp.extensions.assessment.model.Xccdf
import org.cisecurity.sacm.xmpp.extensions.polcoll.PolicyCollectionManager
import org.cisecurity.sacm.xmpp.extensions.policy.model.ObjectFactory
import org.cisecurity.sacm.xmpp.extensions.policy.model.PolicyCollection
import org.cisecurity.sacm.xmpp.extensions.repo.SacmRepositoryManager
import org.cisecurity.sacm.xmpp.extensions.repo.model.SacmRepository
import rocks.xmpp.addr.Jid
import rocks.xmpp.core.session.Extension
import rocks.xmpp.core.session.XmppSessionConfiguration
import rocks.xmpp.core.session.debug.ConsoleDebugger
import rocks.xmpp.extensions.pubsub.PubSubManager
import rocks.xmpp.extensions.pubsub.PubSubNode
import rocks.xmpp.extensions.pubsub.PubSubService
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
				Extension.of(Assessment.NAMESPACE, SacmAssessmentManager.class, true, Assessment.class),
				Extension.of(PolicyCollection.NAMESPACE, PolicyCollectionManager.class, true, PolicyCollection.class)
			)
			.debugger(ConsoleDebugger.class)
			.build()
	}

	@Override
	def postConnect() {
		// subscribe to the map server
	}

	//
	// OPERATIONAL
	//

	//def callback()

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

	def publishPolicy() {
		log.info "[START] PUBLISH POLICY"
		ObjectFactory policyCollectionFactory = new ObjectFactory()
		def pc = policyCollectionFactory.createPolicyCollection()
		pc.publisher = policyCollectionFactory.createPublisher()
		pc.publisher.jid = xmppClient.connectedResource.toString()

		pc.policy = policyCollectionFactory.createPolicy()
		pc.policy.name = "minimum_password_length"

		pc.expected = policyCollectionFactory.createExpected()
		pc.expected.datatype = "int"
		pc.expected.content = "22"

		def pubSubManager = xmppClient.getManager(PubSubManager.class)
		PubSubService pubSubService =
			pubSubManager.createPubSubService(
				Jid.of("pubsub.${xmppClient.connectedResource.domain}"))
		PubSubNode pubSubNode = pubSubService.node("policy")
		log.info "[START] PUBLISHING"
		pubSubNode.publish(pc)
		log.info "[ END ] PUBLISH POLICY"
	}
}
