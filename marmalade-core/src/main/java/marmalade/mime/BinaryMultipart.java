package marmalade.mime;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BinaryMultipart
{

   String mime() default "application/octet-stream";

   String fileName() default "";
   
   String name() default "";

}
