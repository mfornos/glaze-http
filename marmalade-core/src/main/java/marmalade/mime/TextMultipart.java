package marmalade.mime;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TextMultipart
{

   String mime() default "text/plain";

   String name() default "";

   String charset() default "UTF-8";

}
