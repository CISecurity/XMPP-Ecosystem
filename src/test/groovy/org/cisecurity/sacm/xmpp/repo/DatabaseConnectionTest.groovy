package org.cisecurity.sacm.xmpp.repo

import spock.lang.Shared
import spock.lang.Specification

class DatabaseConnectionTest extends Specification {
	@Shared
	def dbc

	def setup() {
		dbc = new DatabaseConnection()
	}

	def cleanup() {
		dbc.closeConnection()
	}

	def "OpenConnection"() {
		given: "The database connection class"
		when: "the connection is open"
			dbc.openConnection()
		then: "connectivity is established"
			assert dbc.isOpen()
	}

	def "Cache"() {
		given: "An Open database connection"
			dbc.openConnection()
		when: "the query is executed and repository cached"
			dbc.cache()
		then: "connectivity is established"
			assert dbc.repository.size() == 13
	}

	def "Find all assessment content ID's equal to 4"() {

		given: "An Open database connection"
			dbc.openConnection()
		when: "the query is executed and repository cached, and searched"
			dbc.cache()
			def f = {
				def rez = []
				dbc.repository.each { r ->
					def cols = r.findAll { k,v -> k == "assessment_content_id" && v == "4" }
					if (cols) { rez << r }
				}
				return rez
			}.call()
			def b
		then: "We found 4 rows"
			assert f.size() == 4
	}
}
