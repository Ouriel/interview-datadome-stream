import java.util.concurrent.ForkJoinPool

import cats.effect.IO
import lol.http._
import org.slf4j.LoggerFactory
import utilities.LoggerMagnet
import utilities.Config.config

import scala.concurrent.{ExecutionContext, Future}

object DataDomeApiClient {
  implicit val clickHouseClientExecutionContext: ExecutionContext = ExecutionContext.fromExecutor(new ForkJoinPool(config.concurrence/2))
  private val client = Client("api.datadome.co", maxConnections = config.concurrence/2)
  private val url = "validate-request/"
private val headers: Map[HttpString, HttpString] = Map(h"ContentType" -> h"application/x-www-form-urlencoded",h"User-Agent" -> h"DataDome")

  val log: LoggerMagnet = LoggerFactory.getLogger(this.getClass)

  def send(formData: DataDomeFormData): IO[String] = {
    val req = Post(url, formData.toString).addHeaders(headers)
    log.debug(s"Request: $req")
    client.run(req) { response =>
      response.drain.map(_ => s"status:${response.status} -> header:${response.headers}")
    }
  }
}
