package org.cisecurity.sacm.xmpp

import org.cisecurity.sacm.xmpp.extensions.repo.SacmRepositoryManager
import org.cisecurity.sacm.xmpp.extensions.repo.model.SacmRepository
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
}
