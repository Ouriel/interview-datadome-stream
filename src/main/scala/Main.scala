import akka.actor.ActorSystem
import akka.stream.scaladsl.{Keep, Sink}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, KillSwitches, Supervision}
import cats.instances.future
import org.slf4j.LoggerFactory
import utilities.LoggerMagnet

import utilities.Config.config
import scala.concurrent.duration.Duration
import scala.concurrent.Await
import scala.io.Source

object Main {

  def main(args: Array[String]): Unit = {

    val log: LoggerMagnet = LoggerFactory.getLogger(this.getClass)

    implicit val system = ActorSystem.create("datadome-stream")
    implicit val mat = ActorMaterializer(ActorMaterializerSettings(system).withSupervisionStrategy { e =>
      log.error(s"Exception Received", e)
      Supervision.Resume
    })
    val iteratorFiles: Iterator[String] = Source.fromURL(config.url).getLines()

    val (killSwitch, future) =
      akka.stream.scaladsl.Source(iteratorFiles.toStream)
        .collect {
          case l if l.nonEmpty =>
            DataDomeFormData(l)
        }
      .filter(d => d.Key!="ERROR")
        .mapAsync(config.concurrence)(DataDomeApiClient.send(_).unsafeToFuture())
        .viaMat(KillSwitches.single)(Keep.right)
        .toMat(Sink.ignore)(Keep.both)
        .run()

    sys.addShutdownHook({
      killSwitch.shutdown()
    })

    log.info("akka stream started, to shutdown send ctrl+c or call /shutdown")
    val result = Await.result(future, Duration.Inf)
    log.info(s"Finishing with value : $result")
    mat.shutdown()
    Await.result(system.terminate(), config.shutdownTimeout)
    log.info("Terminated... Bye")
    sys.exit()
  }
}