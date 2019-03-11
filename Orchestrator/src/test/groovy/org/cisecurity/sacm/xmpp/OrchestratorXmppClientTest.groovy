package org.cisecurity.sacm.xmpp

import spock.lang.Specification

class OrchestratorXmppClientTest extends Specification {
	def "Retrieve supported SACM content types from the repository"() {
		given: "A base client and a repository client"
			def b = new XmppUser(username: "orchestrator", credentials: "Pt3ttcs2h!")
			def bx = new OrchestratorXmppClient(xmppUser: b)
			bx.connect()

			def r = new XmppUser(username: "ubuntu", credentials: "Pt3ttcs2h!")
			def rx = new RepositoryXmppClient(xmppUser: r)
			rx.connect()
		when: "SACM content types retrieved"
			def repo = bx.listRepositoryContentTypes(rx.xmppClient.connectedResource)
		then: "Good things happen"
			assert repo.value.collect { it.contentType.value() } == ["SCAP", "OVAL", "SACM", "YANG"]
	}

	def "Retrieve SACM content repository for ALL types"() {
		given: "A base client and a repository client"
			def b = new XmppUser(username: "orchestrator", credentials: "Pt3ttcs2h!")
			def bx = new OrchestratorXmppClient(xmppUser: b)
			bx.connect()

			def r = new XmppUser(username: "ubuntu", credentials: "Pt3ttcs2h!")
			def rx = new RepositoryXmppClient(xmppUser: r)
			rx.connect()
		when: "SACM content is retrieved"
			def repo = bx.listRepositoryContent(rx.xmppClient.connectedResource)
		then: "Good things happen"
			assert repo.item.size() == 4
	}

	def "Retrieve SACM content repository for OVAL type"() {
		given: "A base client and a repository client"
			def b = new XmppUser(username: "orchestrator", credentials: "Pt3ttcs2h!")
			def bx = new OrchestratorXmppClient(xmppUser: b)
			bx.connect()

			def r = new XmppUser(username: "ubuntu", credentials: "Pt3ttcs2h!")
			def rx = new RepositoryXmppClient(xmppUser: r)
			rx.connect()
		when: "SACM content is retrieved"
			def repo = bx.listRepositoryContent(rx.xmppClient.connectedResource, "OVAL")
		then: "Good things happen"
			assert repo.item.size() == 0
	}

	def "Retrieve SACM content repository for SCAP type"() {
		given: "A base client and a repository client"
			def b = new XmppUser(username: "orchestrator", credentials: "Pt3ttcs2h!")
			def bx = new OrchestratorXmppClient(xmppUser: b)
			bx.connect()

			def r = new XmppUser(username: "ubuntu", credentials: "Pt3ttcs2h!")
			def rx = new RepositoryXmppClient(xmppUser: r)
			rx.connect()
		when: "SACM content is retrieved"
			def repo = bx.listRepositoryContent(rx.xmppClient.connectedResource, "SCAP")
		then: "Good things happen"
			assert repo.item.size() == 4
	}

	def "Retrieve SACM content repository for SCAP type and item name"() {
		given: "A base client and a repository client"
			def b = new XmppUser(username: "orchestrator", credentials: "Pt3ttcs2h!")
			def bx = new OrchestratorXmppClient(xmppUser: b)
			bx.connect()

			def r = new XmppUser(username: "ubuntu", credentials: "Pt3ttcs2h!")
			def rx = new RepositoryXmppClient(xmppUser: r)
			rx.connect()
		when: "SACM content is retrieved"
			def repo = bx.listRepositoryContent(rx.xmppClient.connectedResource, "SCAP", "Microsoft Windows 2016")
		then: "Good things happen"
			assert repo.item.size() == 1
	}

	def "Retrieve SACM content by name"() {
		given: "A base client and a repository client"
			def b = new XmppUser(username: "orchestrator", credentials: "Pt3ttcs2h!")
			def bx = new OrchestratorXmppClient(xmppUser: b)
			bx.connect()

			def r = new XmppUser(username: "ubuntu", credentials: "Pt3ttcs2h!")
			def rx = new RepositoryXmppClient(xmppUser: r)
			rx.connect()
		when: "SACM content is retrieved"
			def repo = bx.listRepositoryContent(rx.xmppClient.connectedResource, "scap", "Microsoft Windows 2016")
		then: "Good things happen"
			assert repo.item.size() == 1
	}
}
