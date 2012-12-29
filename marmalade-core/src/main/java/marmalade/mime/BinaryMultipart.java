package marmalade.mime;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BinaryMultipart
{

   String fileName() default "";

   String mime() default "application/octet-stream";

   String name() default "";

}
