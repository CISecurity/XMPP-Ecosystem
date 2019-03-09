package org.cisecurity.sacm.xmpp.extensions.repo

import org.slf4j.LoggerFactory
import rocks.xmpp.core.stanza.AbstractIQHandler
import rocks.xmpp.core.stanza.model.IQ
import rocks.xmpp.core.stanza.model.StanzaError
import rocks.xmpp.core.stanza.model.errors.Condition

class ContentRequestHandler extends AbstractIQHandler {

	def log = LoggerFactory.getLogger(ContentRequestHandler.class)

	ContentRequestHandler() { super(IQ.Type.GET) }

	/**
	 * Processes the IQ, after checking if the extension is enabled and after checking if the IQ has correct type,
	 * which is specified for the extension.
	 *
	 * @param iq The IQ request.
	 * @return The IQ response.
	 */
	@Override
	protected IQ processRequest(IQ iq) {
		log.info "Processing Request --> {http://cisecurity.org/sacm/repository}/content_request"
		return iq.createError(
			new StanzaError(
				Condition.SERVICE_UNAVAILABLE))
	}
}
