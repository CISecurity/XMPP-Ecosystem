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

	def "Retrieve SACM content repository"() {
		given: "A base client and a repository client"
			def b = new XmppUser(username: "orchestrator", credentials: "Pt3ttcs2h!")
			def bx = new XmppClientBase(xmppUser: b)
			bx.connect()

			def r = new XmppUser(username: "ubuntu", credentials: "Pt3ttcs2h!")
			def rx = new RepositoryXmppClient(xmppUser: r)
			rx.connect()
		when: "SACM content is retrieved"
			def repo = bx.listRepositoryContent(rx.xmppClient.connectedResource)
		then: "Good things happen"
			assert repo.content.item.size() == 4
	}

	def "Retrieve SACM content by name"() {
		given: "A base client and a repository client"
			def b = new XmppUser(username: "orchestrator", credentials: "Pt3ttcs2h!")
			def bx = new XmppClientBase(xmppUser: b)
			bx.connect()

			def r = new XmppUser(username: "ubuntu", credentials: "Pt3ttcs2h!")
			def rx = new RepositoryXmppClient(xmppUser: r)
			rx.connect()
		when: "SACM content is retrieved"
			def repo = bx.getRepositoryContent(rx.xmppClient.connectedResource, "Microsoft Windows 2016", "scap")
		then: "Good things happen"
			assert repo.content.item.size() == 1
	}
}
