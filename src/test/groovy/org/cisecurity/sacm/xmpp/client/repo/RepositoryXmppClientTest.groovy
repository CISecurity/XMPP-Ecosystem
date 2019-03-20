package org.cisecurity.sacm.xmpp.client.repo

import org.cisecurity.sacm.xmpp.client.XmppUser
import org.cisecurity.sacm.xmpp.client.endpoint.EndpointXmppClient
import spock.lang.Specification

class RepositoryXmppClientTest extends Specification {
	def "TransferRepositoryContent"() {
		given: "A base client and a repository client"
			def b = new XmppUser(username: "orchestrator", credentials: "Pt3ttcs2h!")
			def bx = new EndpointXmppClient(xmppUser: b)
			bx.connect()

			def r = new XmppUser(username: "ubuntu", credentials: "Pt3ttcs2h!")
			def rx = new RepositoryXmppClient(xmppUser: r)
			rx.connect()
		when: "SACM content is transferred"
			def repo = rx.transferRepositoryContent(bx.xmppClient.connectedResource, "2")
			Thread.sleep(15000)
		then: "Good things happen"
			assert repo.success
	}
}
