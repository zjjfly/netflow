name := "netflow"

organization := "io.wasted"

version := scala.io.Source.fromFile("version").mkString.trim

scalaVersion := "2.12.4"

scalacOptions ++= Seq("-unchecked",
                      "-deprecation",
                      "-Xcheckinit",
                      "-encoding",
                      "utf8",
                      "-feature")

scalacOptions ++= Seq("-language:higherKinds",
                      "-language:postfixOps",
                      "-language:implicitConversions",
                      "-language:reflectiveCalls",
                      "-language:existentials")

javacOptions ++= Seq("-target", "1.8", "-source", "1.8", "-Xlint:deprecation")

mainClass in assembly := Some("io.netflow.Node")

libraryDependencies ++= {
  val wastedVersion = "0.12.4"
  Seq(
    "io.wasted" %% "wasted-util" % wastedVersion,
    ("net.liftweb" %% "lift-json" % "3.3.0")
      .excludeAll(ExclusionRule().withOrganization("io.netty")),
    ("com.twitter" %% "finagle-redis" % "18.8.0")
      .excludeAll(ExclusionRule().withOrganization("io.netty")),
    "org.xerial.snappy" % "snappy-java" % "1.1.1.3",
    "joda-time" % "joda-time" % "2.7",
    ("org.codehaus.janino" % "janino" % "2.7.8")
      .excludeAll(ExclusionRule().withOrganization("io.netty")),
    ("com.datastax.cassandra" % "cassandra-driver-core" % "3.5.1")
      .excludeAll(ExclusionRule().withOrganization("io.netty"))

  )
}

enablePlugins(BuildInfoPlugin)

buildInfoKeys ++= Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion)

buildInfoPackage := "io.netflow.lib"

assemblyJarName in assembly := "netflow.jar"

import scalariform.formatter.preferences._

scalariformPreferences := scalariformPreferences.value
  .setPreference(AlignParameters, true)
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(DoubleIndentConstructorArguments, true)
  .setPreference(DanglingCloseParenthesis, Preserve)