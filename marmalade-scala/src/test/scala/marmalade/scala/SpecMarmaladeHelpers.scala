package marmalade.scala

import marmalade.scala.test._
import marmalade.test.http.Condition._
import marmalade.Marmalade._
import marmalade.client.Response
import org.apache.http.HttpResponse
import org.apache.http.util.EntityUtils
import org.apache.http.entity.ContentType._
import marmalade.spi.Registry
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonCreator
import org.apache.http.nio.client.methods.AsyncByteConsumer
import java.nio.ByteBuffer
import org.apache.http.nio.IOControl
import org.apache.http.protocol.HttpContext
import marmalade.func.Closures.Closure
import org.apache.http.client.methods.HttpRequestBase
import marmalade.util.RequestUtil
import org.apache.http.HttpEntity
import scala.reflect.BeanProperty

class SpecMarmaladeHelpers extends HttpSpec {

  "Get request" should "get 200 OK" in withHttpServer { server =>
    server.expect(when("GET").path("/").respond("OK"))

    expect(200) {
      Get(baseUri).send.status
    }
  }

  "Get request with implicit response handler" should "get 'OK'" in withHttpServer { server =>
    server.expect(when("GET").path("/").respond("OK"))

    Get(baseUri).withHandler((r: HttpResponse) => {
      assert("OK" === EntityUtils.toString(r.getEntity))
    }).execute[Any]
  }

  "Map get request" should "return a Card with id 'ABCDEFG'" in withHttpServer { server =>
    server.expect(when("GET").path("/").respond("{\"id\":\"ABCDEFG\"}", APPLICATION_JSON))

    val card = Get(baseUri).map(classOf[Card])
    assert(card.id === "ABCDEFG")
  }

  "Map post request" should "post a Foo and return a Card with id 'ABCDEFG'" in withHttpServer { server =>
    server.expect(when("POST").path("/").respond("{\"id\":\"ABCDEFG\"}", APPLICATION_JSON))

    val foo = new Foo();
    foo.name = "TEST"

    val card = Post(baseUri).bean(foo).as(APPLICATION_JSON).decorate(new Closure[HttpRequestBase] {
      // Test the content that will be sent
      override def on(req: HttpRequestBase) = {
        val bean = EntityUtils.toString(RequestUtil.getEntity(req))
        RequestUtil.setEntity(req, null);
        assert(bean === "{\"name\":\"TEST\"}")
      }
    }).map(classOf[Card])

    assert(card.id === "ABCDEFG")
  }

  "Stream request" should "receive 'Michael bytes'" in withHttpServer { server =>
    server.expect(when("GET").path("/").respond("Michael bytes", TEXT_PLAIN))

    val response = Get(baseUri).withConsumer(new AsyncByteConsumer[String] {
      var ok = ""

      override def onByteReceived(bytes: ByteBuffer, control: IOControl) = {
        for (b <- bytes.array()) {
          if (b > 0) ok += b.toChar
        }
      }

      override def buildResult(ctx: HttpContext): String = ok

      override def onResponseReceived(response: HttpResponse) = {}
    }).executeAsync[String]

    assert(response.get === "Michael bytes")

  }

}

// Nice trick, but does not work for serialization
class Card @JsonCreator() (@JsonProperty("id") val id: String)

class Foo {
  @BeanProperty var name: String = _
}
