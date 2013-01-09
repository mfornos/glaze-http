package glaze.scala.test

import org.scalatest.FlatSpec
import glaze.scala._
import glaze.test.http.MockHttpServer
import HttpServer._

object HttpServer {
  val port = 51234
  val baseUri = "http://localhost:%s".format(port)
  val server: MockHttpServer = new MockHttpServer(port)

  def startServer(): MockHttpServer = {
    server.start;
    server
  }
  def stopServer() {
    server.stop;
  }
}

class HttpSpec extends FlatSpec with GlazeHelpers {
  
  val baseUri = HttpServer.baseUri
  
  def withHttpServer(testCode: MockHttpServer => Any) {
    val httpServer = startServer // create the fixture
    try {
      testCode(httpServer) // "loan" the fixture to the test
    } finally stopServer() // clean up the fixture
  }
  
}