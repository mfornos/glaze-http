package marmalade.scala

import marmalade.scala.test._
import marmalade.test.http.Condition
import marmalade.Marmalade._
import marmalade.client.Response
import org.apache.http.HttpResponse
import org.apache.http.util.EntityUtils
import org.apache.http.entity.ContentType
import marmalade.spi.Registry
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonCreator

class SpecMarmaladeHelpers extends HttpSpec {

  "Response" should "be 200 OK" in withHttpServer { server =>
    server.expect(Condition.when("GET").path("/").respond("OK"));

    expect(200) {
      Get(baseUri).send[Response].status
    }
  }

  "Response content" should "be 'OK'" in withHttpServer { server =>
    server.expect(Condition.when("GET").path("/").respond("OK"));

    Get(baseUri).withHandler((r: HttpResponse) => {
      assert("OK" === EntityUtils.toString(r.getEntity))
    }).send[Any]
  }

  "A map that" should "return a Card with id 'ABCDEFG'" in withHttpServer { server =>
    server.expect(Condition.when("GET").path("/").respond("{\"id\":\"ABCDEFG\"}", ContentType.APPLICATION_JSON));

    val card: Card = Get(baseUri).map(classOf[Card])
    assert(card.id === "ABCDEFG")

  }

}

// Nice trick
class Card @JsonCreator() (@JsonProperty("id") val id: String) {

}
