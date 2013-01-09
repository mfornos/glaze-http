package glaze.examples.twitter.api.stream;

import lombok.Data;

@Data
public class BoundingBox
{

   /**
    * A series of longitude and latitude points, defining a box which will
    * contain the Place entity this bounding box is related to. Each point is an
    * array in the form of [longitude, latitude]. Points are grouped into an
    * array per bounding box. Bounding box arrays are wrapped in one additional
    * array to be compatible with the polygon notation.
    */
   private Float[][][] coordinates;
   private String type;
}
