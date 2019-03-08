package org.cisecurity.sacm.xmpp

import org.cisecurity.sacm.xmpp.extensions.repo.SacmRepositoryManager
import org.cisecurity.sacm.xmpp.extensions.repo.model.SacmRepository
import org.cisecurity.sacm.xmpp.trust.TrustAllX509TrustManager
import org.slf4j.LoggerFactory
import rocks.xmpp.addr.Jid
import rocks.xmpp.core.XmppException
import rocks.xmpp.core.sasl.AuthenticationException
import rocks.xmpp.core.session.Extension
import rocks.xmpp.core.stream.StreamNegotiationException
import rocks.xmpp.core.stream.model.StreamErrorException
import rocks.xmpp.core.session.NoResponseException
import rocks.xmpp.core.stanza.model.StanzaErrorException
import rocks.xmpp.core.net.client.SocketConnectionConfiguration
import rocks.xmpp.core.session.XmppClient
import rocks.xmpp.core.session.XmppSessionConfiguration
import rocks.xmpp.core.session.debug.ConsoleDebugger

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import java.security.SecureRandom

class XmppClientBase {
	def log = LoggerFactory.getLogger(XmppClientBase.class)

	XmppClient xmppClient

	XmppUser xmppUser

	//
	// Client Functionality
	//

	/**
	 * Connect the user to the XMPP server.
	 *
	 * From XmppClient:
	 * @throws AuthenticationException    If the login failed, due to a SASL error reported by the server.
	 * @throws StreamErrorException       If the server returned a stream error.
	 * @throws StreamNegotiationException If any exception occurred during stream feature negotiation.
	 * @throws NoResponseException        If the server didn't return a response during stream establishment.
	 * @throws StanzaErrorException       If the server returned a stanza error during resource binding or roster retrieval.
	 * @throws XmppException              If the login failed, due to another error.
	 */
	def connect() {
		if (!xmppUser) {
			log.error "No XMPP User was identified for this client.  Connection is impossible."
		} else {
			if (!xmppClient) {
				initialize()
			}

			try {
				xmppClient.login(xmppUser.username, xmppUser.credentials, xmppUser.resource)

				log.info "User ${xmppUser.username} is connected."
			} catch (XmppException e) {
				log.error "An XMPP Exception was thrown during LOGIN", e
			}
		}
	}

	/**
	 * Disconnect the user from the XMPP server
	 */
	def close() {
		if (xmppClient) {
			log.info "Closing XMPP client..."
			xmppClient.close()
		}
	}

	//
	// Operational
	//


	def listRepositoryContent(Jid repositoryJid) {
		SacmRepositoryManager srm = xmppClient.getManager(SacmRepositoryManager.class)
		SacmRepository repo = srm.listRepositoryContent(repositoryJid).getResult()
		repo.content.item.each { i ->
			log.info "SACM Content Item: ${i.name}; ID: ${i.id}; Type: ${i.type.toString()}"
		}
		return repo
	}

	def getRepositoryContent(Jid repositoryJid, String itemName, String itemType) {
		SacmRepositoryManager srm = xmppClient.getManager(SacmRepositoryManager.class)
		SacmRepository repo = srm.getRepositoryContent(repositoryJid, itemName, itemType).getResult()
		return repo
	}


	//
	// Initialization
	//

	/**
	 * Initialize the XMPP client, allowing overriding functionality-specific
	 * clients to configure specific behaviors/listeners/handlers.
	 */
	def initialize() {
		log.info "[START] Initializing XMPP Client"

		final SSLContext sslContext = SSLContext.getInstance("TLS")
		TrustManager[] trustManagers = [new TrustAllX509TrustManager()]

		// Set the custom trust manager(s) in the SSL context
		sslContext.init(null, trustManagers, new SecureRandom())

		// Socket connection to the XMPP Server
		SocketConnectionConfiguration tcpConfiguration = SocketConnectionConfiguration.builder()
			.hostname(xmppUser.domain)
			.port(5222)
			.sslContext(sslContext)
			.hostnameVerifier(new HostnameVerifier() {
				boolean verify(String urlHostName, SSLSession session) {
					return true
				}
			}).build()

		// Create the XMPP Session Configuration.  Extending clients should override this to add
		// the extensions required for their specific levels of functionality.
		XmppSessionConfiguration xmppSessionConfiguration = createXmppSessionConfiguration()

		// Create the XMPP Client
		xmppClient = XmppClient.create(xmppUser.domain, xmppSessionConfiguration, tcpConfiguration)

		// Allow custom clients to enable message listeners
		configureMessageListeners()

		// Allow custom clients to enable IQ handlers
		configureIQHandlers()

		// Allow custom clients to enable extension-based listeners
		configureExtensionBasedListeners()

		// Finally...
		xmppClient.connect()

		log.info "[ END ] Initializing XMPP Client"
	}

	/**
	 * Extending classes should override this method to create their own configuration,
	 * including any extensions specific to that client.  By default, no custom extensions
	 * are included, and the console debugger is turned on.
	 * @return the XMPP Session Configuration, potentially with extensions/managers enabled.
	 */
	XmppSessionConfiguration createXmppSessionConfiguration() {
		return XmppSessionConfiguration.builder()
			.extensions(
				// Support the SACM Repository Extension but disable it by default.
				Extension.of(SacmRepository.NAMESPACE, SacmRepositoryManager.class, false, SacmRepository.class))
			.debugger(ConsoleDebugger.class)
			.build()
	}

	/**
	 * Configure any In/Outbound Message Listeners
	 */
	def configureMessageListeners() {}

	/**
	 * Configure any custom IQ Handlers
	 */
	def configureIQHandlers() {}

	/**
	 * Configure any XMPP extension-based listeners, such as file transfer offer listeners
	 * or assessment request listeners, etc.
	 */
	def configureExtensionBasedListeners() {}
}

/**
 * A user of this XMPP client
 */
class XmppUser {
	String username
	String credentials
	String domain   = "ip-0a1e0af4"
	String resource = "sacm"
}