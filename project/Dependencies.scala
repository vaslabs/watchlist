import sbt._
object Dependencies {

  object Version {
    val tapir = "0.12.16"
    val akka = "2.6.1"
    val akkaHttp = "10.1.11"
    val scalatest = "3.1.0"
  }

  object Library {

    object Akka {
      val actors = "com.typesafe.akka" %% "akka-actor-typed" % Version.akka
      val testKit =
        "com.typesafe.akka" %% "akka-actor-testkit-typed" % Version.akka % Test
      val http = Seq(
        "com.typesafe.akka" %% "akka-http"   % Version.akkaHttp,
        "com.typesafe.akka" %% "akka-stream" % Version.akka
      )

    }
    object Tapir {
      val core = "com.softwaremill.sttp.tapir" %% "tapir-core" % Version.tapir
      val circe = "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % Version.tapir

      val akkaHttp = "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server" % Version.tapir
      val openApi = "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % Version.tapir
      val openApiCirce = "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % Version.tapir
      val swaggerAkkaHttp = "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-akka-http" % Version.tapir

    }
    object Testing {
      val scalatest = "org.scalatest" %% "scalatest" % Version.scalatest % Test
      val scalacheck = "org.scalacheck" %% "scalacheck" % "1.14.1" % Test
    }
  }

  object Module {
    import Library._

    val protocol = Seq(Akka.actors, Testing.scalatest, Akka.testKit, Testing.scalacheck)
    val endpoints = Seq(Tapir.core, Tapir.circe)
    val service = Seq(
      Tapir.akkaHttp,
      Tapir.akkaHttp,
      Tapir.openApi,
      Tapir.openApiCirce,
      Tapir.swaggerAkkaHttp
    ) ++
      Akka.http
  }
}
