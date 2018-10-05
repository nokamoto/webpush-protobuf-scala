lazy val scalaSettings = Seq(
  scalaVersion := "2.12.6",
  organization := "com.github.nokamoto",
  version := "0.0.0",
  licenses := Seq(
    "MIT License" -> url("http://www.opensource.org/licenses/mit-license")),
)

def pbSettings(grpc: Boolean) = Seq(
  PB.protoSources in Compile := (file("webpush-protobuf/webpush/protobuf").getCanonicalFile * AllPassFilter).get,
  PB.includePaths in Compile := Seq(file("webpush-protobuf").getCanonicalFile,
                                    target.value / "protobuf_external"),
  PB.targets in Compile := Seq(scalapb
    .gen(grpc = grpc, flatPackage = true) -> (sourceManaged in Compile).value),
  libraryDependencies += "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf"
)

def sonatypeSettings(name: String) = Seq(
  useGpg := false,
  pgpPassphrase := sys.env.get("PGP_PASSPHRASE").map(_.toCharArray),
  publishTo := sonatypePublishTo.value,
  credentials += Credentials("Sonatype Nexus Repository Manager",
                             "oss.sonatype.org",
                             sys.env.getOrElse("SONATYPE_USER", ""),
                             sys.env.getOrElse("SONATYPE_PASS", "")),
  sonatypeProfileName := "com.github.nokamoto",
  publishMavenStyle := true,
  sonatypeProjectHosting := Some(
    xerial.sbt.Sonatype
      .GitHubHosting("nokamoto", name, "nokamoto.engr@gmail.com")),
  scmInfo := Some(
    ScmInfo(url("https://github.com/nokamoto/webpush-protobuf-scala"),
            "scm:git@github.com:nokamoto/webpush-protobuf-scala.git")),
  publishConfiguration := publishConfiguration.value.withOverwrite(true)
)

def create(id: String, grpc: Boolean) =
  Project(id = id, base = file(s".$id"))
    .settings(name := id, scalaSettings, pbSettings(grpc), sonatypeSettings(id))

lazy val protobuf = create(id = "webpush-protobuf", grpc = false)

lazy val grpc = create(id = "webpush-protobuf-grpc", grpc = true).settings(
  libraryDependencies ++= Seq(
    "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
    "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion
  ))
