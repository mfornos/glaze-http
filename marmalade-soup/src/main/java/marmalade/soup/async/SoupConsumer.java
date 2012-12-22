package marmalade.soup.async;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import marmalade.soup.Mode;
import marmalade.util.ResponseUtil;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.client.methods.AsyncByteConsumer;
import org.apache.http.protocol.HttpContext;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

public abstract class SoupConsumer<T> extends AsyncByteConsumer<T>
{

   public static SoupConsumer<Document> instance()
   {
      return instance(Mode.HTML);
   }

   public static SoupConsumer<Document> instance(Mode mode)
   {
      return new SoupConsumer<Document>(mode)
      {
         @Override
         protected Document onDocumentReceived(Document document)
         {
            return document;
         }
      };
   }

   private volatile StringBuilder stringBuffer;

   private Mode mode;

   private String charsetName;

   private String baseUri;

   public SoupConsumer()
   {
      this(Mode.HTML);
   }

   public SoupConsumer(Mode mode)
   {
      this("", ResponseUtil.DEFAULT_ENCODING, mode);
   }

   public SoupConsumer(String baseUri, String charsetName, Mode mode)
   {
      this.mode = mode;
      this.charsetName = charsetName;
      this.baseUri = baseUri;
   }

   void appendByteData(ByteBuffer byteData, StringBuilder docData, String charsetName)
   {
      if (charsetName == null) {
         docData.append(Charset.forName(ResponseUtil.DEFAULT_ENCODING).decode(byteData).toString());
      } else {
         Validate.notEmpty(charsetName, "Must set charset arg to character set of file to parse. Set to null to attempt to detect from HTML");
         docData.append(Charset.forName(charsetName).decode(byteData).toString());
      }
   }

   String getCharsetFromContentType(String contentType)
   {
      Charset charset = ContentType.parse(contentType).getCharset();
      return charset == null ? ResponseUtil.DEFAULT_ENCODING : charset.toString();
   }

   Document parseDocData(String docData, String charsetName, String baseUri, Parser parser)
   {
      Document doc = null;
      if (charsetName == null) {
         doc = parser.parseInput(docData, baseUri);
         Element meta = doc.select("meta[http-equiv=content-type], meta[charset]").first();
         if (meta != null) {
            String foundCharset = (meta.hasAttr("http-equiv")) ? getCharsetFromContentType(meta.attr("content"))
                  : meta.attr("charset");
            if ((foundCharset != null) && (foundCharset.length() != 0)
                  && (!(foundCharset.equals(ResponseUtil.DEFAULT_ENCODING)))) {
               // XXX we cannot rewind :_
               // charsetName = foundCharset;
               // byteData.rewind();
               // docData =
               // Charset.forName(foundCharset).decode(byteData).toString();
               // doc = null;
               throw new IllegalStateException(String.format("Invalid enconding %s found %s", charsetName, foundCharset));
            }

         }
      }

      if (doc == null) {
         if (docData.charAt(0) == 65279) {
            docData = docData.substring(1);
         }
         doc = parser.parseInput(docData, baseUri);
         doc.outputSettings().charset(charsetName);
      }
      return doc;
   }

   @Override
   protected T buildResult(HttpContext ctx) throws Exception
   {
      Document document = parseDocData(stringBuffer.toString(), charsetName, baseUri, mode.getParser());
      return onDocumentReceived(document);
   }

   @Override
   protected void onByteReceived(ByteBuffer byteBuffer, IOControl paramIOControl) throws IOException
   {
      appendByteData(byteBuffer, stringBuffer, charsetName);
   }

   abstract protected T onDocumentReceived(Document document);

   @Override
   protected void onResponseReceived(HttpResponse response) throws HttpException, IOException
   {
      this.stringBuffer = new StringBuilder();
      this.charsetName = charsetName == null ? ResponseUtil.resolveEncoding(response) : charsetName;
   }

}
