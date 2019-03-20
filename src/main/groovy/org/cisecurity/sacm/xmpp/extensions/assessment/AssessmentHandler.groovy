package org.cisecurity.sacm.xmpp.extensions.assessment

import groovy.xml.XmlUtil
import org.cisecurity.assessor.impl.XccdfCollectionEngine
import org.cisecurity.assessor.impl.status.ConsoleStatusWriter
import org.cisecurity.assessor.intf.IChecklistEngine
import org.cisecurity.assessor.intf.IDatastreamCollectionEngine
import org.cisecurity.assessor.intf.IDatastreamEngine
import org.cisecurity.assessor.parser.IDatastreamCollectionParser
import org.cisecurity.assessor.parser.file.XccdfCollectionParser
import org.cisecurity.assessor.util.AssessorUtilities
import org.cisecurity.sacm.xmpp.extensions.assessment.model.Assessment
import org.cisecurity.sacm.xmpp.extensions.assessment.model.DatastreamCollection
import org.cisecurity.sacm.xmpp.extensions.assessment.model.OvalDefinitions
import org.cisecurity.sacm.xmpp.extensions.assessment.model.Xccdf
import org.cisecurity.session.fact.ISessionFactory
import org.cisecurity.session.fact.SessionConfig
import org.cisecurity.session.fact.SessionFactoryFactory
import org.cisecurity.session.intf.ISession
import org.cisecurity.wrapper.AssessmentConfiguration
import org.cisecurity.wrapper.StandardConsole
import org.cisecurity.wrapper.console.AssessmentBase
import org.cisecurity.wrapper.console.AssessmentFactory
import org.cisecurity.wrapper.console.AssessmentWrapper
import org.slf4j.LoggerFactory
import rocks.xmpp.core.session.XmppSession
import rocks.xmpp.core.stanza.AbstractIQHandler
import rocks.xmpp.core.stanza.model.IQ
import rocks.xmpp.core.stanza.model.StanzaError
import rocks.xmpp.core.stanza.model.errors.Condition

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class AssessmentHandler extends AbstractIQHandler {
	def log = LoggerFactory.getLogger(AssessmentHandler.class)

	def initialAssessmentConfiguration
	def assessmentWrapper

	XmppSession xmppSession

	ExecutorService executorService

	AssessmentHandler() {
		super(IQ.Type.GET)

		initialize()
	}

	AssessmentHandler(XmppSession xmppSession) {
		super(IQ.Type.GET)

		this.xmppSession = xmppSession
		executorService =
			Executors.newSingleThreadExecutor(
				xmppSession.getConfiguration().getThreadFactory("Assessment Thread"))

		initialize()
	}

	def initialize() {
		// Configure the initial assessment "wrapper" configuration
		initialAssessmentConfiguration =
			new AssessmentConfiguration(
				jd: new File(System.getProperty("user.dir")),
				//d: f.parentFile,
				userProperties: AssessorUtilities.instance.userProperties,
				scripts: new File("C:\\_Development\\Projects\\Assessor-Shared\\scripts"))

		assessmentWrapper =
			new AssessmentWrapper(
				assessmentConfiguration: initialAssessmentConfiguration,
				console: new StandardConsole())
	}

	/**
	 * Processes the IQ, after checking if the extension is enabled and after checking if the IQ has correct type,
	 * which is specified for the extension.
	 *
	 * @param iq The IQ request.
	 * @return The IQ response.
	 */
	@Override
	protected IQ processRequest(IQ iq) {
		Assessment assessment = iq.getExtension(Assessment.class)

		if (assessment.xccdf) {
			assessment.scheduled = withXccdf(assessment.xccdf)
			return iq.createResult(assessment)
		} else if (assessment.datastreamCollection) {
			assessment.scheduled = withDSC(assessment.datastreamCollection)
			return iq.createResult(assessment)
		} else if (assessment.ovalDefinitions) {
			assessment.scheduled = withOD(assessment.ovalDefinitions)
			return iq.createResult(assessment)
		} else {
			return iq.createError(new StanzaError(Condition.BAD_REQUEST, "Unsupported Assessment Request"))
		}
	}

	def withXccdf(Xccdf xccdf) {
		return withSCAP(new File("${xccdf.contentPath}\\${xccdf.benchmark}"))
	}

	def withDSC(DatastreamCollection dsc) {
		return withSCAP(new File("${dsc.contentPath}\\${dsc.collection}"))
	}

	def withSCAP(File f) {
		assessmentWrapper.assessmentConfiguration.d = f.parentFile

		AssessmentBase assessmentBase = assessmentWrapper.collectBenchmark(f)

		assessmentBase.assessmentConfig = assessmentWrapper.assessmentConfiguration.clone()
		assessmentBase.console          = assessmentWrapper.console
		assessmentBase.initialize()

		return assess(assessmentBase.engine, assessmentBase.parser)
	}

	def withOD(OvalDefinitions od) {
		def odf = new File("${od.contentPath}\\${od.definitions}")
		def ovf = {
			if (od.variables) {
				return new File("${od.contentPath}\\${od.variables}")
			} else {
				return null
			}
		}.call()

		initialAssessmentConfiguration.d = odf.parentFile

		def assessmentFactory =
			new AssessmentFactory(
				applicationDirectory: initialAssessmentConfiguration.jd,
				workingDirectory: initialAssessmentConfiguration.d)

		AssessmentBase assessmentBase =
			assessmentFactory.getAssessment([
				"contentPath": odf.path,
				"variables"  : ovf?.path])

		assessmentBase.assessmentConfig = assessmentWrapper.assessmentConfiguration.clone()
		assessmentBase.console          = assessmentWrapper.console
		assessmentBase.preInitialize()
		assessmentBase.initialize()

		return assess(assessmentBase.engine, assessmentBase.parser)
	}

	def assess(def engine, def parser) {
		Future<?> assessmentFuture = executorService.submit {
			SessionConfig cfg =
				new SessionConfig(
					type: SessionConfig.Type.LOCAL,
					localScriptsDirPathname: "C:\\_Development\\Projects\\Assessor-Shared\\scripts",
					tmpPath: "C:\\_Development\\Projects\\Assessor-Shared\\Temp")

			ISessionFactory factory = new SessionFactoryFactory().getSessionFactory()
			ISession session = factory.getSession(cfg)

			if (session.isWindows()) {
				log.info "Connection type is Windows:"
				log.info "--> Unzipping [START]"
				def unzipCmd = "${session.connectionTmpPathname}unzip.exe"
				def zipfile = "${session.connectionTmpPathname}scripts.zip"
				def fullCmd = "${unzipCmd} -o -qq ${zipfile} -d ${session.connectionTmpPathname}"

				def rc = session.execute(fullCmd)
				log.info "--> Unzipping [ END ]   (rc = ${rc})"
			}

			AssessorUtilities.instance.loadProperties("C:\\_Development\\Projects\\Assessor-CLI\\config\\assessor-cli.properties")

			def statusWriter = new ConsoleStatusWriter()
			statusWriter.initialize()

			engine.parameters = parser.parseForParameters()
			engine.initializeCollection()

			IDatastreamEngine datastreamEngine  = engine.selectDatastream(0)
			IChecklistEngine checklistEngine    = datastreamEngine.selectChecklist(0)
			datastreamEngine.session            = session
			checklistEngine.session             = session
			checklistEngine.statusWriter        = statusWriter
			checklistEngine.checklistProperties = AssessorUtilities.instance.userProperties
			checklistEngine.selectProfile(0)
			checklistEngine.transform()
			checklistEngine.evaluate()

			new File("endpoint_results\\ASSESSMENT-RESULTS.xml").withWriter { w ->
				w.write(XmlUtil.serialize(engine.getOutputReport()))
			}
			session.disconnect()
		}
		return (assessmentFuture != null)
	}
}
