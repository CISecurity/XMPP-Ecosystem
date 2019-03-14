package org.cisecurity.sacm.xmpp.repo

import groovy.sql.Sql
import org.slf4j.LoggerFactory

import java.sql.ResultSetMetaData
import java.sql.SQLException

class DatabaseConnection {
	def log = LoggerFactory.getLogger(DatabaseConnection.class)

	protected Sql sql

	private final String CONTENT_QUERY = """
SELECT
	C.content_name, 
    C.content_type_code, 
    CONCAT(F.content_path, "\\\\", F.content_filename) AS content_filepath, 
    C.assessment_content_id, 
    F.content_filepath_id, 
    F.content_filepath_seq
FROM 
    `sacm-xmpp`.assessmentcontent as C, 
    `sacm-xmpp`.assessmentcontenttype as T,  
    `sacm-xmpp`.assessmentcontentfilepath as F
WHERE
    C.content_type_code = T.content_type_code AND
    C.content_filepath_id = F.content_filepath_id
ORDER BY C.content_filepath_id, F.content_filepath_seq;
"""

	// Each entry in this list is a map of column name/value pairs
	def repository = []

	def openConnection() throws SQLException {
		sql = Sql.newInstance("jdbc:mysql://localhost:3306/ccpd?user=root&password=Pt3ttcs2h!")
	}

	boolean isOpen() {
		return (sql != null)
	}

	def closeConnection() throws SQLException {
		sql?.close()
	}

	def cache() throws SQLException {
		if (!sql) { openConnection() }

		repository.clear()
		repository.addAll(query(CONTENT_QUERY))
	}

	def query(def stmt, binds = []) {
		def rows = []

		sql.query(stmt, binds) { resultSet ->
			// get column metadata...
			ResultSetMetaData resultSetMetaData = resultSet.getMetaData()
			int numberOfColumns = resultSetMetaData.getColumnCount()

			while (resultSet.next()) {
				// create a database row object...
				def currRow = [:]

				// NOTE: Columns are numbered from 1.
				(1..numberOfColumns).each { column ->
					// add the column to the row...
					currRow[resultSetMetaData.getColumnName(column).toLowerCase()] = resultSet.getString(column)
				}

				log.info "Current Row --> ${currRow.toString()}"

				// add the row to the results...
				rows << currRow
			}
		}
		return rows
	}
}
