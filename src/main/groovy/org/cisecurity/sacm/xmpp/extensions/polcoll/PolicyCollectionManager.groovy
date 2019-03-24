package org.cisecurity.sacm.xmpp.extensions.polcoll

import org.cisecurity.sacm.xmpp.extensions.policy.model.ObjectFactory
import rocks.xmpp.core.session.Manager
import rocks.xmpp.core.session.XmppSession
import rocks.xmpp.core.stanza.AbstractIQHandler
import rocks.xmpp.core.stanza.IQHandler
import rocks.xmpp.core.stanza.model.IQ

class PolicyCollectionManager extends Manager {
	private final IQHandler iqHandler

	private PolicyCollectionManager(final XmppSession xmppSession) {
		super(xmppSession)

		this.iqHandler = new AbstractIQHandler(IQ.Type.GET) {
			@Override
			protected IQ processRequest(IQ iq) {
				synchronized (this) {
					ObjectFactory policyCollectionFactory = new ObjectFactory()
					def pc = policyCollectionFactory.createPolicyCollection()
					pc.publisher = policyCollectionFactory.createPublisher()
					pc.publisher.jid = xmppSession.connectedResource.toString()

					pc.policy = policyCollectionFactory.createPolicy()
					pc.policy.name = "minimum_password_length"

					pc.actual = policyCollectionFactory.createActual()
					pc.actual.datatype = "int"
					pc.actual.content = "15"

					return iq.createResult(pc)
				}
			}
		}
	}


	/**
	 * Called when the manager is enabled.
	 */
	@Override
	protected void onEnable() {
		super.onEnable()

	}

	/**
	 * Called when the manager is disabled.
	 */
	@Override
	protected void onDisable() {
		super.onDisable()
	}
}
