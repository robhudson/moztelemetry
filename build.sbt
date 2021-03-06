name := "moztelemetry"

version := "1.0-SNAPSHOT"

organization := "com.mozilla.telemetry"

scalaVersion := "2.11.8"

sparkVersion := "2.1.0"

sparkComponents ++= Seq("core")

resolvers += Resolver.bintrayRepo("findify", "maven")

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.6" % "test",
  "org.apache.commons" % "commons-io" % "1.3.2" % "test",
  "com.github.seratch" %% "awscala" % "0.5.+",
  "com.amazonaws" % "aws-java-sdk" % "1.11.83",
  "com.google.protobuf" % "protobuf-java" % "2.5.0"
)
/*
 The HBase client requires protobuf-java 2.5.0 but scalapb uses protobuf-java 3.x
 so we have to force the dependency here. This should be fine as we are using only
 version 2 of the protobuf spec.
*/
dependencyOverrides += "com.google.protobuf" % "protobuf-java" % "2.5.0"

// Shade PB files
assemblyShadeRules in assembly := Seq(
  ShadeRule.rename("com.google.protobuf.**" -> "shadeproto.@1").inAll,
  ShadeRule.rename("com.trueaccord.scalapb.**" -> "shadescalapb.@1").inAll
) 

// Compile proto files
PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)

// Exclude generated classes from the coverage
coverageExcludedPackages := "com\\.mozilla\\.telemetry\\.heka\\.(Field|Message|Header)"

credentials += Credentials(Path.userHome / ".ivy2" / ".sbtcredentials")

publishMavenStyle := true

publishTo := {
  val localMaven = "s3://net-mozaws-data-us-west-2-ops-mavenrepo/"
  if (isSnapshot.value)
    Some("snapshots" at localMaven + "snapshots")
  else
    Some("releases"  at localMaven + "releases")
}

pomExtra := (
  <url>https://github.com/mozilla/moztelemetry</url>
    <licenses>
      <license>
        <name>MPL 2.0</name>
        <url>https://www.mozilla.org/en-US/MPL/2.0/</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com/mozilla/moztelemetry.git</url>
      <connection>scm:git:git@github.com/mozilla/moztelemetry.git</connection>
    </scm>
    <developers>
      <developer>
        <id>vitillo</id>
        <name>Roberto Agostino Vitillo</name>
        <url>https://robertovitillo.com</url>
      </developer>
    </developers>)
