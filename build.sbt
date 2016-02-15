name := "Scala Monadic Persistent Data Layer Handler."

organization := "org.sokawa"

version := "0.0.1"

scalaVersion := "2.11.7"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

val awsSdkS3Version       = "1.10.0"
val junitVersion          = "4.11"
val junitinterfaceVersion = "0.11-RC1"
val scalareflectVersion   = "2.11.7"
val scalatestVersion      = "2.2.4"
val typesafeVersion       = "1.2.1"

description := "Provides Functionality Assets for Fully Monadic Persistent Data Layer Handler."

startYear := Some(2016)

resolvers ++= Seq(
  "Snapshots Repository" at "http://repo.maven.apache.org/maven2/",
  "Typesafe Repository"  at "http://repo.typesafe.com/typesafe/releases/",
  "Clojars Repository"   at "https://clojars.org/repo/")

libraryDependencies ++= Seq(
  "com.amazonaws"     %  "aws-java-sdk-s3"  % awsSdkS3Version,
  "com.novocode"      %  "junit-interface"  % junitinterfaceVersion % "test",
  "com.typesafe"      %  "config"           % typesafeVersion,
  "junit"             %  "junit"            % junitVersion          % "test",
  "org.scala-lang"    %  "scala-reflect"    % scalareflectVersion,
  "org.scalatest"     %% "scalatest"        % scalatestVersion      % "test")
