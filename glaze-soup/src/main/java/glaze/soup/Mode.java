package glaze.soup;

import org.jsoup.parser.Parser;

public enum Mode {
   HTML {
      @Override
      public Parser getParser()
      {
         return Parser.htmlParser();
      }
   },
   XML {
      @Override
      public Parser getParser()
      {
         return Parser.xmlParser();
      }
   };
   public abstract Parser getParser();
}