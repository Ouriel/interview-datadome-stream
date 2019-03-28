package utilities

import pureconfig.loadConfigOrThrow

import scala.concurrent.duration.FiniteDuration

case class Config(
  url: String,
                  concurrence: Int,
                  shutdownTimeout: FiniteDuration
                 )


object Config {
  import pureconfig.generic.auto._
  implicit lazy val config: Config = loadConfigOrThrow[Config]("datadome-stream")
}
