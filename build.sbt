name := "kivi"

organization := "com.eivlis"

version := "0.1"

scalaVersion := "2.13.3"

// set the main class for packaging the main jar
mainClass in (Compile, packageBin) := Some("com.eivlis.kivi.Main")

libraryDependencies += "net.sourceforge.argparse4j" % "argparse4j" % "0.7.0"
libraryDependencies += "com.amazonaws" % "aws-java-sdk-core" % "1.11.859"
libraryDependencies += "com.amazonaws" % "aws-java-sdk-sts" % "1.11.859"
libraryDependencies += "com.amazonaws" % "amazon-kinesis-client" % "1.9.0"




