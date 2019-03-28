name := "datadome-stream"

version := "0.1"

scalaVersion := "2.12.7"


scalacOptions ++= Seq(
  "-feature", "-deprecation",
  "-language:postfixOps", "-language:reflectiveCalls", "-language:implicitConversions",
  "-Ywarn-dead-code", "-Ywarn-value-discard", "-Ywarn-unused"
)

libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.+"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.8.+"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.+"

libraryDependencies += "com.criteo.lolhttp" %% "lolhttp" % "0.10.+"
libraryDependencies += "com.criteo.lolhttp" %% "loljson" % "0.10.+"

libraryDependencies += "com.github.pureconfig" %% "pureconfig" % "0.10.+"

mainClass in (Compile, run) := Some("Main")
mainClass in assembly := Some("Main")

assemblyMergeStrategy in assembly := {
  case  n if n.startsWith("META-INF") => MergeStrategy.discard
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}