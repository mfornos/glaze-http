# Glaze HTTP [![Build Status](https://travis-ci.org/mfornos/glaze-http.png?branch=master)](https://travis-ci.org/mfornos/glaze-http)

Fluent API for comfortable HTTP interactions. Glaze HTTP offers a simple yet complete toolkit to ease the building of powerful REST clients.

Provides:

* Automatic serialization/deserialization by content type
* Easy form handling, including multipart requests
* Easy asynchronous HTTP interaction over NIO
* Easy Error handling
* Convenient facilities for local multi-threaded and remote distributed executors
* Pluggable mappers and services

## Basic Usage

```java
import static glaze.Glaze.*;
```

### Mapping

```java

 // Simple map
 
 Map<String, Object> result = Get(uri).map();
 
 // Map a bean
 
 MyBean out = Get(uri).map(MyBean.class);
 
 // Map with error handling
 
 MyBean out = Get(uri).withErrorHandler(new ErrorHandler(){...}).map(MyBean.class);
 
 // Post a bean as url-encoded content
 
 Post(uri).bean(in).send();
 
 // Post a bean as json
 
 Post(uri).bean(in).as(APPLICATION_JSON).send();
 
 // or
 
 Post(uri, APPLICATION_JSON).bean(in).send();
 
 // Post a bean as json and get the response mapped back to a bean according to
 // the response content-type
 
 MyBean out = Post(uri).bean(in).as(APPLICATION_JSON).map(MyBean.class);
 
 // force to be mapped back from xml ignoring the response content-type. i.e.
 // broken headers
 
 Post(uri).bean(in).as(APPLICATION_JSON).map(MyBean.class, APPLICATION_XML);
 
```

### Asynchronous interaction

```java

 // Basic send async
 
 Future<HttpResponse> out = Get(uri).sendAsync();
 out.get();
 
 // Basic map async
 
 Future<MyBean> out = Get(uri).mapAsync(MyBean.class);
 out.get();
 
 // With consumer
 
 Future<MyResult> out = Get(uri).withConsumer(myAsyncConsumer).executeAsync();
 out.get();
 
```

### Multipart

```java

 // Post a file
 
 Post(uri).bean(new File("myfile.png")).as(MULTIPART_FORM_DATA).send();
 
 // or
 
 Post(uri, MULTIPART_FORM_DATA).bean(new FileInputStream(file)).send();
 
 // or bytes
 
 byte[] bytes = new byte[] { 0x1, 0x1, 0x1, 0x0, 0xB, 0xA, 0xB, 0xB, 0xE };
 
 Post(uri, MULTIPART_FORM_DATA).bean(bytes).send();
 
 // Post a bean annotated with specific multipart annotations
 
 class MultipartBean
 {
    @BinaryMultipart
    private File attachment;
 
    @BinaryMultipart(fileName = "tangerine.jpg", mime = "image/jpeg", name = "photo")
    private File pht;
 
    @TextMultipart
    private String hello = "world!";
 
    @TextMultipart(name = "ho", mime = "application/json")
    private String hi = "{\"num\":1}";
 }
 
 Post(uri).bean(multipartBean).as(MULTIPART_FORM_DATA).send();

```

## Writing a Twitter Client

You will need _glaze-oauth_ to access Twitter 1.1 APIs.

For a full working example with OAuth flow included look at: __examples/src/main/java/glaze/examples/twitter__

In the example we will create an asynchronous client, if you want a synchronous one just extend DefaultSyncClient and call non-async methods.

_App.java_

```java
public class App
{

   public static void main(String[] args) throws IOException, OAuthException
   {
      TwitterApi api = new TwitterApi(new PreemptiveTwitterClient());

      Tweet[] tweets = api.homeTimeline();

      for (Tweet tweet : tweets) {
         System.out.println(tweet);
      }

      System.exit(0);
   }
}
```

_TwitterApi.java_

```java
public class TwitterApi
{
   private static final String BASE_URI = "https://api.twitter.com/1.1";

   private final TwitterClient client;

   public TwitterApi()
   {
      this(Registry.lookup(TwitterClient.class));
   }

   public TwitterApi(TwitterClient client)
   {
      Preconditions.checkNotNull(client);

      this.client = client;
   }

   public Tweet[] homeTimeline()
   {
      try {
         return promiseHomeTimeline().get();
      } catch (Exception e) {
         throw new TwitterApiException(e);
      }
   }

   public Future<Tweet[]> promiseHomeTimeline()
   {
      return Get(svcPath("/statuses/home_timeline.json").build()).mapAsync(client, Tweet[].class);
   }

   public void shutdown()
   {
      client.shutdown();
   }

   private UriBuilder svcPath(Object... paths)
   {
      return uriBuilderFrom(BASE_URI).appendPaths(paths);
   }

}
```

_TwitterClient.java_

```java
public class PreemptiveTwitterClient extends DefaultAsyncClient
{
   private String key;
   private String secret;

   public PreemptiveTwitterClient()
   {
      this(new ConfigCredentialsProvider(new TwitterConfig()));
   }

   public PreemptiveTwitterClient(OAuthCredentialsProvider provider)
   {
      enableAuth(provider);
   }

   @Override
   public String getKey()
   {
      return key;
   }

   @Override
   public String getSecret()
   {
      return secret;
   }

   @Override
   public void setTokens(OAuthConsumer consumer)
   {
      ConsumerCredentialsProvider provider = new ConsumerCredentialsProvider(consumer);
      enableAuth(provider);
   }

   private void enableAuth(OAuthCredentialsProvider provider)
   {
      this.key = provider.getKey();
      this.secret = provider.getSecret();
      OAuthClientHelper.enablePreemptiveAuth(this, provider);
   }
}
```

_TwitterConfig.java_

```java
public class TwitterConfig extends DefaultPropertyConfig implements OAuthConfig
{
   
   public TwitterConfig()
   {
      super("twitter.config", "twitter.properties");
   }

   public String getKey()
   {
      return get("key");
   }

   public String getSecret()
   {
      return get("secret");
   }

   public String getTokenKey()
   {
      return get("token.key");
   }

   public String getTokenSecret()
   {
      return get("token.secret");
   }

}
```

## JVM languages

### Scala

```scala
object MashapeApp extends GlazeHelpers {

  class Forecast @JsonCreator() (
    @JsonProperty("day_of_week") val dayOfWeek: String,
    @JsonProperty("low") val low: Integer, // Fahrenheit
    @JsonProperty("high") val high: Integer,
    @JsonProperty("condition") val condition: String) {
    override def toString: String = "%s L%s° H%s°F - %s\n".format(dayOfWeek, low, high, condition)
  }

  def main(args: Array[String]): Unit = {

    val mcli = new MashapeClient
    val uri = uriBuilderFrom("https://george-vustrey-weather.p.mashape.com/api.php")
                            .addParameter("_method", "getForecasts")
                            .addParameter("location", "Barcelona")
                            .build

    try {
      val forecasts = Get(uri).withErrorHandler { r: Response =>
        println("Error: %s".format(r))
      }.mapAsync(mcli, classOf[Array[Forecast]])

      println("%s @%s\n".format(where, new Date))

      forecasts.get.foreach(forecast => println(forecast))

    } finally { mcli.shutdown }

  }

}
``` 

### Clojure

```clojure
(defn map-request [uri]
  "Maps a Get request with an ErrorHandler"
  (.. (Get uri)
        (withErrorHandler 
          (reify ErrorHandler
            (onError[this response] 
              (println (str response " error went through handler")))))
        map))

(defn send-request-rh [uri]
  "Sends a Get request with a ResponseHandler"
  (.. (Get uri)
        (withHandler 
          (reify ResponseHandler
            (handleResponse[this response] 
              (println (str response " went through handler"))
              (EntityUtils/toString (.getEntity response)))))
        send))

(defn send-request-eh [uri]
  "Sends a Get request with an ErrorHandler"
  (.. (Get uri)
        (withErrorHandler 
          (reify ErrorHandler
            (onError[this response] 
              (println (str response " error went through handler")))))
        send))

(defn send-request [uri]
  "Sends a Get request"
  (.. (Get uri) send))
```

Enjoy!

                     ____
       ________     /   /   
      / ____/ /___ /_  / ___  
     / / __/ / __ `// / / _ \
    / /_/ / / /_/ // /_/  __/ 
    \____/_/\__,_//   /\___/  HTTP delight
                 /___/


be cool and play nice.

