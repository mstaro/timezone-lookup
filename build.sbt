name := "timezone-lookup"

version := "1.0"

scalaVersion := "2.10.6"

resolvers += "Open Source Geospatial Foundation Repository" at "http://download.osgeo.org/webdav/geotools/"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
//scala 2.12
//libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
//libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2"
//libraryDependencies += "org.geotools" % "gt-shapefile" % "17.2"

//scala 2.10
libraryDependencies += "org.geotools" % "gt-shapefile" % "14.5"
