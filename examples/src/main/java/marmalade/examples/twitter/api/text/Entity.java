package marmalade.examples.twitter.api.text;

import lombok.Data;
import marmalade.examples.twitter.api.stream.Entities.Hashtag;
import marmalade.examples.twitter.api.stream.Entities.Mention;
import marmalade.examples.twitter.api.stream.Entities.Url;


@Data
public class Entity implements Comparable<Entity>
{
   public enum Type {
      URL, HASHTAG, MENTION, CASHTAG
   }

   protected int start;
   protected int end;
   protected final String value;
   // listSlug is used to store the list portion of @mention/list.
   protected final String listSlug;
   protected final Type type;

   protected String displayURL = null;
   protected String expandedURL = null;

   public Entity(Hashtag h)
   {
      fromIndices(h.getIndices());
      this.value = h.getText();
      this.type = Type.HASHTAG;
      this.listSlug = null;
   }

   public Entity(int start, int end, String value, String listSlug, Type type)
   {
      this.start = start;
      this.end = end;
      this.value = value;
      this.listSlug = listSlug;
      this.type = type;
   }

   public Entity(int start, int end, String value, Type type)
   {
      this(start, end, value, null, type);
   }

   public Entity(Mention m)
   {
      fromIndices(m.getIndices());
      this.value = m.getScreenName();
      this.type = Type.MENTION;
      this.listSlug = null;
   }

   public Entity(Url u)
   {
      fromIndices(u.getIndices());
      this.value = u.getUrl();
      this.displayURL = u.getDisplayUrl();
      this.expandedURL = u.getExpandedUrl();
      this.type = Type.URL;
      this.listSlug = null;
   }

   @Override
   public int compareTo(Entity entity)
   {
      return start - entity.start;
   }

   protected void fromIndices(Integer[] idx)
   {
      this.start = idx[0];
      this.end = idx[1];
   }

}
