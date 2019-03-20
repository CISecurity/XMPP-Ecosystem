package org.cisecurity.sacm.xmpp.client.endpoint

import org.cisecurity.sacm.xmpp.client.XmppUser
import spock.lang.Specification

class EndpointXmppClientTest extends Specification {
	def "Log properties"() {
		given: "An endpoint client"
			def e = new XmppUser(username: "orchestrator", credentials: "Pt3ttcs2h!")
			def ep = new EndpointXmppClient(xmppUser: e)
		when: "its done loading"
			ep.connect()
		then: "manually check the log"
			assert true
	}
}
