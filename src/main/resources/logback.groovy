import ch.qos.logback.classic.encoder.PatternLayoutEncoder

// See http://logback.qos.ch/manual/groovy.html for details on configuration
appender('STDOUT', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%level %logger - %msg%n"
    }
}

def logDirectory = "."
appender("ROLLING", RollingFileAppender) {
    encoder(PatternLayoutEncoder) {
        Pattern = "%d{MM/dd/yyyy HH:mm:ss.SSS} %level %logger - %msg%n"
    }
    file = "${logDirectory}/logs/xmpp.log"
    rollingPolicy(FixedWindowRollingPolicy) {
        fileNamePattern = "${logDirectory}/logs/xmpp.%i.log"
        minIndex = 1
        maxIndex = 21
    }
    triggeringPolicy(SizeBasedTriggeringPolicy) {
        maxFileSize = "25MB"
    }
}

//root(INFO, ["STDOUT"])
root(INFO, ["ROLLING"])
