package org.cisecurity.sacm.xmpp

import org.cisecurity.sacm.xmpp.extensions.repo.SacmRepositoryManager
import org.cisecurity.sacm.xmpp.extensions.repo.model.SacmRepository
import rocks.xmpp.core.session.Extension
import rocks.xmpp.core.session.XmppSessionConfiguration
import rocks.xmpp.core.session.debug.ConsoleDebugger

class OrchestratorXmppClient extends XmppClientBase {



	/**
	 * Extending classes should override this method to create their own configuration,
	 * including any extensions specific to that client.  By default, no custom extensions
	 * are included, and the console debugger is turned on.
	 * @return the XMPP Session Configuration, potentially with extensions/managers enabled.
	 */
	@Override
	XmppSessionConfiguration createXmppSessionConfiguration() {
		return XmppSessionConfiguration.builder()
			.extensions(
				// Talk to a repository...
				Extension.of(SacmRepository.NAMESPACE, SacmRepositoryManager.class, false, SacmRepository.class)
				// Talk to endpoint(s)...
				// TODO
			)
			.debugger(ConsoleDebugger.class)
			.build()
	}
}
