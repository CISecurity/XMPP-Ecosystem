package org.cisecurity.sacm.xmpp

import org.cisecurity.sacm.xmpp.extensions.repo.SacmRepositoryManager
import org.cisecurity.sacm.xmpp.extensions.repo.model.SacmRepository
import rocks.xmpp.core.session.Extension
import rocks.xmpp.core.session.XmppSessionConfiguration
import rocks.xmpp.core.session.debug.ConsoleDebugger

class RepositoryXmppClient extends XmppClientBase {



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
				Extension.of(SacmRepository.NAMESPACE, SacmRepositoryManager.class, true, SacmRepository.class))
			.debugger(ConsoleDebugger.class)
			.build()
	}
}
