name := "web-service-sample"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.8"

resolvers += "ossrh repository" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
    "com.typesafe.akka"         %%  "akka-slf4j"                % "2.5.12",
    "com.typesafe.akka"         %%  "akka-stream"               % "2.5.12",
    "com.typesafe.akka"         %%  "akka-http-core"            % "10.1.1",
    "com.typesafe.akka"         %%  "akka-http-experimental"    % "2.4.11",
    "ch.qos.logback"            %   "logback-classic"           % "1.1.2",
    "org.apache.spark"          %%  "spark-core"                % "2.2.0" withSources(),
    "org.apache.spark"          %%  "spark-mllib"               % "2.2.0" withSources(),
    "org.apache.spark"          %%  "spark-sql"                 % "2.2.0" withSources(),
    "com.intel.analytics.zoo"   %   "analytics-zoo-SPARK_2.2"   % "0.1.0-SNAPSHOT" withSources() 
    )

mainClass := Some("com.intel.analytics.zoo.webservicesample.Main")
    
assemblyMergeStrategy in assembly := {
	case PathList("org", "slf4j", xs @ _*)         => MergeStrategy.first
	case s => {
		val oldStrategy = (assemblyMergeStrategy in assembly).value
    	oldStrategy(s)
	}
}