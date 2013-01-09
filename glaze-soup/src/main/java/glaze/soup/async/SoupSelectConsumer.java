package glaze.soup.async;

import glaze.soup.Mode;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class SoupSelectConsumer extends SoupConsumer<Elements>
{

   public static SoupSelectConsumer Select(String cssQuery)
   {
      return new SoupSelectConsumer(cssQuery);
   }

   private final String cssQuery;

   public SoupSelectConsumer(String cssQuery)
   {
      this(cssQuery, Mode.HTML);
   }

   public SoupSelectConsumer(String cssQuery, Mode mode)
   {
      super(mode);
      this.cssQuery = cssQuery;
   }

   @Override
   protected Elements onDocumentReceived(Document document)
   {
      return document.select(cssQuery);
   }

}
