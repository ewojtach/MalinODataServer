package server.malin

import java.util

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer
import org.apache.olingo.server.api.OData
import org.apache.olingo.server.api.edmx.EdmxReference
import server.core.ODataAkkaHttpHandler
import server.malin.device.{DeviceEdmProvider, DeviceEntityCollectionProcessor}

import scala.concurrent._
import ExecutionContext.Implicits.global

/**
  * Created by ewa on 14.08.2017.
  */
object MalinApp extends App {
  implicit val system = ActorSystem("akka-http-olingo-integration")
  implicit val fm = ActorMaterializer()

  val serverBinding = Http().bindAndHandleAsync(asyncHandler, interface = "localhost", port = 8080)

  def asyncHandler(request: HttpRequest): Future[HttpResponse] = {
    request match {
      case HttpRequest(GET, _, _, _, _) => {
        Future[HttpResponse] {
          val oData = OData.newInstance()

          val edm = oData.createServiceMetadata(DeviceEdmProvider(), new util.ArrayList[EdmxReference]())
          val handler = new ODataAkkaHttpHandler(oData, edm, fm)

          handler.register(new DeviceEntityCollectionProcessor())
          handler.process(request)
        }
      }
    }
  }
}