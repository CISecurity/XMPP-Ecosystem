package org.cisecurity.sacm.xmpp

import org.cisecurity.sacm.xmpp.client.XmppUser
import org.cisecurity.sacm.xmpp.client.endpoint.EndpointXmppClient
import org.cisecurity.sacm.xmpp.client.orchestrator.OrchestratorXmppClient
import org.cisecurity.sacm.xmpp.client.repo.RepositoryXmppClient
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
			assert repo.value.collect { it.contentType.value() } == ["SCAP"]//, "OVAL", "SACM", "YANG"]
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
			def repo = bx.listRepositoryContent(rx.xmppClient.connectedResource, "SCAP", "windows_10.1709")
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
			def repo = bx.listRepositoryContent(rx.xmppClient.connectedResource, null, "apple_osx_10.12")
		then: "Good things happen"
			assert repo.item.size() == 1
			assert repo.item[0].assessmentContentId == 3
	}

	def "Perform an assessment"() {
		given: "A base client and a repository client"
			def b = new XmppUser(username: "orchestrator", credentials: "Pt3ttcs2h!")
			def bx = new OrchestratorXmppClient(xmppUser: b)
			bx.connect()

			def e = new XmppUser(username: "ubuntu", credentials: "Pt3ttcs2h!")
			def ep = new EndpointXmppClient(xmppUser: e)
			ep.connect()

			def parameters = ["xccdf": "CIS_Microsoft_Windows_10_Enterprise_Release_1709_Benchmark_v1.4.0-xccdf.xml"]
		when: "An assessment is requested"
			def assessment = bx.tellAnEndpointToPerformAnAssessment(ep.xmppClient.connectedResource, parameters)
		then: "Good things happen"
			assert assessment.isScheduled()
//			Thread.sleep(120000)
	}

	def "Perform an OVAL assessment"() {
		given: "A base client and a repository client"
			def b = new XmppUser(username: "orchestrator", credentials: "Pt3ttcs2h!")
			def bx = new OrchestratorXmppClient(xmppUser: b)
			bx.connect()

			def e = new XmppUser(username: "ubuntu", credentials: "Pt3ttcs2h!")
			def ep = new EndpointXmppClient(xmppUser: e)
			ep.connect()

			def parameters = ["oval_definitions": "microsoft_windows_10b.xml"]
		when: "An assessment is requested"
			def assessment = bx.tellAnEndpointToPerformAnAssessment(ep.xmppClient.connectedResource, parameters)
			Thread.sleep(15000)
		then: "Good things happen"
			assert assessment.isScheduled()
	}

	def "Publish something to the Collector"() {
		given: "The orchestrator"
			def b = new XmppUser(username: "orchestrator", credentials: "Pt3ttcs2h!")
			def bx = new OrchestratorXmppClient(xmppUser: b)
			bx.connect()

			def e = new XmppUser(username: "ubuntu", credentials: "Pt3ttcs2h!")
			def ep = new EndpointXmppClient(xmppUser: e)
			ep.connect()
		when: "A payload is published"
			bx.publishPolicy()
			Thread.sleep(15000)
		then: "The Collector receives the event and logs it"
			true
	}
}
