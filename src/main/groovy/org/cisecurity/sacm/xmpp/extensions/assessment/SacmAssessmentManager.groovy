package org.cisecurity.sacm.xmpp.extensions.assessment

import org.cisecurity.sacm.xmpp.extensions.assessment.model.Assessment
import org.cisecurity.sacm.xmpp.extensions.assessment.model.OvalDefinitions
import org.cisecurity.sacm.xmpp.extensions.assessment.model.Xccdf
import rocks.xmpp.addr.Jid
import rocks.xmpp.core.session.Manager
import rocks.xmpp.core.session.XmppSession
import rocks.xmpp.core.stanza.model.IQ
import rocks.xmpp.util.concurrent.AsyncResult

import java.util.concurrent.Executors

class SacmAssessmentManager extends Manager {

	private SacmAssessmentManager(final XmppSession xmppSession) {
		super(xmppSession)
	}

	//
	// TODO: Register incoming IQ handlers to process SACM Assessment-related requests
	//

	/**
	 * Called when the manager is enabled.
	 */
	@Override
	protected void onEnable() {
		super.onEnable()

		// register assessment capabilities

		// register IQ handlers
		xmppSession.addIQHandler(Assessment.class, new AssessmentHandler(xmppSession))
	}

	/**
	 * Called when the manager is disabled.
	 */
	@Override
	protected void onDisable() {
		super.onDisable()

		xmppSession.removeIQHandler(Assessment.class)
	}

	//
	// "Outbound" IQs.
	//

	/**
	 * Request that the recipient JID perform an assessment using the unique content
	 * identifier (presumably retrieved from the repository)
	 * @param jid
	 * @param assessmentContentId
	 * @return
	 */
	AsyncResult<Assessment> performAssessment(Jid jid, def parameters = [:]) {
		def assessment = new Assessment()

		if (parameters["xccdf"]) {
			assessment.xccdf = new Xccdf(contentPath: "endpoint_content",  benchmark: parameters["xccdf"], profile: parameters["profile"])
		}

		if (parameters["oval_definitions"]) {
			assessment.ovalDefinitions =
				new OvalDefinitions(
					contentPath: "endpoint_content",
					definitions: parameters["oval_definitions"],
					variables: parameters["oval_variables"])
		}

		return xmppSession.query(IQ.get(jid, assessment), Assessment.class)
	}
}
