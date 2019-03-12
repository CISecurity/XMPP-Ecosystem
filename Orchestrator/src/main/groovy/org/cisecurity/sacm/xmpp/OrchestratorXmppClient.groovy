package org.cisecurity.sacm.xmpp

import org.cisecurity.sacm.xmpp.extensions.repo.SacmRepositoryManager
import org.cisecurity.sacm.xmpp.extensions.repo.model.SacmRepository
import rocks.xmpp.core.session.Extension
import rocks.xmpp.core.session.XmppSessionConfiguration
import rocks.xmpp.core.session.debug.ConsoleDebugger

class OrchestratorXmppClient extends XmppClientBase {

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
				Extension.of(SacmRepository.NAMESPACE, SacmRepositoryManager.class, false, SacmRepository.class)
				// Talk to endpoint(s)...
				// TODO
			)
			.debugger(ConsoleDebugger.class)
			.build()
	}
}
