resolvers ++= Seq("Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/", "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/")

scalacOptions ++= Seq("-language:postfixOps", "-language:implicitConversions", "-language:higherKinds", "-deprecation", "-feature")
 
libraryDependencies ++= Seq(
  "com.typesafe"        %  "config"                    % "1.2.0",
  "com.typesafe"        %% "scalalogging-slf4j"        % "1.0.1",
  "org.scalaz"          %% "scalaz-core"               % "7.0.5",
  "org.scalaz"          %% "scalaz-scalacheck-binding" % "7.0.5"  % "test",
  "org.typelevel"       %% "scalaz-contrib-210"        % "0.1.5",
  "com.github.scopt"    %% "scopt"                     % "3.2.0",
  "org.specs2"          %% "specs2"                    % "2.3.7"  % "test",
  "org.scalacheck"      %% "scalacheck"                % "1.11.1" % "test",
  // DB dependencies - Slick with MySQL
  "com.typesafe.slick"  %% "slick"                     % "2.0.0",
  "mysql"               %  "mysql-connector-java"      % "5.1.24",
  "com.h2database"      %  "h2"                        % "1.3.174",
  "com.jolbox"          %  "bonecp"                    % "0.8.0.RELEASE",
  "com.zaxxer"          %  "HikariCP"                  % "1.2.8"
)

initialCommands in console := "import scalaz._, Scalaz._"
