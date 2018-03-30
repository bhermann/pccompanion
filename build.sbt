name := "pccompanion"

version := "0.4"

scalaVersion := "2.12.4"

libraryDependencies += "com.github.scopt" %% "scopt" % "3.7.0"
libraryDependencies += "com.nrinaudo" %% "kantan.csv" % "0.4.0"
libraryDependencies += "com.nrinaudo" %% "kantan.csv-generic" % "0.4.0"

lazy val root = (project in file(".")).
  enablePlugins(JavaAppPackaging).
  enablePlugins(BuildInfoPlugin).
  settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "de.thewhitespace.pccompanion"
  )

