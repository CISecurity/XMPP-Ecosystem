package org.cisecurity.sacm.xmpp

import rocks.xmpp.core.session.XmppSession
import spock.lang.Specification

class XmppClientBaseTest extends Specification {

	def "Ensure that, when the initialize method is complete, the XMPP client has as status of CONNECTED"() {
		given: "An XMPP client and user"
			def u = new XmppUser(username: "ubuntu", credentials: "Pt3ttcs2h!")
			def x = new XmppClientBase(xmppUser: u)
		when: "The XMPP client is initialized"
			x.initialize()
		then: "The status of the XMPP client is CONNECTED"
			assert x.xmppClient.getStatus() == XmppSession.Status.CONNECTED
	}

	def "Ensure that, when the connect method is complete, the XMPP client has a status of AUTHENTICATED"() {
		given: "An XMPP client and user"
			def u = new XmppUser(username: "ubuntu", credentials: "Pt3ttcs2h!")
			def x = new XmppClientBase(xmppUser: u)
		when: "The XMPP client is connected"
			x.connect()
		then: "The status of the XMPP client is CONNECTED"
			assert x.xmppClient.getStatus() == XmppSession.Status.AUTHENTICATED
	}

	def "Ensure that, when the close method is complete, the XMPP client has a status of CLOSED"() {
		given: "An XMPP client and user"
			def u = new XmppUser(username: "ubuntu", credentials: "Pt3ttcs2h!")
			def x = new XmppClientBase(xmppUser: u)
			x.connect()
		when: "The XMPP client is closed"
			x.close()
		then: "The status of the XMPP client is CONNECTED"
			assert x.xmppClient.getStatus() == XmppSession.Status.CLOSED
	}

	//TODO
}
