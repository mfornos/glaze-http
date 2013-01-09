package glaze.util;

import glaze.mime.BinaryMultipart;
import glaze.mime.TextMultipart;
import glaze.spi.Registry;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;

public class FormHelper
{

   private interface Converter<T extends AbstractContentBody>
   {
      T convert(Object bean, Field field, BinaryMultipart ma) throws IllegalArgumentException, IllegalAccessException;

      T convert(Object bean, String filename);
   }

   private static final Logger LOGGER = LoggerFactory.getLogger(FormHelper.class);

   private static final Map<Class<?>, Converter<?>> bodies = new HashMap<Class<?>, Converter<?>>();

   static {
      bodies.put(File.class, new Converter<FileBody>()
      {
         @Override
         public FileBody convert(Object bean, Field field, BinaryMultipart ma) throws IllegalArgumentException,
               IllegalAccessException
         {
            File file = (File) field.get(bean);
            return new FileBody(file, ma.mime());
         }

         @Override
         public FileBody convert(Object bean, String filename)
         {
            return new FileBody((File) bean);
         }
      });

      bodies.put(InputStream.class, new Converter<InputStreamBody>()
      {
         @Override
         public InputStreamBody convert(Object bean, Field field, BinaryMultipart ma) throws IllegalArgumentException,
               IllegalAccessException
         {
            return new InputStreamBody((InputStream) field.get(bean), Preconditions.checkNotNull(ma.fileName(), "Please, set a fileName on @Multipart annotation."), ma.mime());
         }

         @Override
         public InputStreamBody convert(Object bean, String filename)
         {
            return new InputStreamBody((InputStream) bean, filename);
         }
      });

      bodies.put(byte[].class, new Converter<ByteArrayBody>()
      {
         @Override
         public ByteArrayBody convert(Object bean, Field field, BinaryMultipart ma) throws IllegalArgumentException,
               IllegalAccessException
         {
            return new ByteArrayBody((byte[]) field.get(bean), Preconditions.checkNotNull(ma.fileName(), "Please, set a fileName on @Multipart annotation."), ma.mime());

         }

         @Override
         public ByteArrayBody convert(Object bean, String filename)
         {
            return new ByteArrayBody((byte[]) bean, filename);
         }
      });
   }

   public static MultipartEntity asMultipartEntity(Object bean)
   {

      MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

      for (Class<?> clz : bodies.keySet()) {
         if (bean.getClass().isAssignableFrom(clz)) {
            ContentBody body = bodies.get(clz).convert(bean, "file.data");
            multipartEntity.addPart("file", body);
            return multipartEntity;
         }
      }

      Field[] fields = bean.getClass().getDeclaredFields();
      for (Field field : fields) {
         addBody(bean, multipartEntity, field);
      }

      return multipartEntity;
   }

   public static UrlEncodedFormEntity asUrlEncodedFormEntity(Object bean)
   {
      return asUrlEncodedFormEntity(bean, Consts.ISO_8859_1);
   }

   public static UrlEncodedFormEntity asUrlEncodedFormEntity(Object bean, Charset charset)
   {
      ObjectMapper mapper = Registry.lookup(ObjectMapper.class);
      Map<String, String> props = mapper.convertValue(bean, TypeHelper.newMapType(String.class, String.class));
      List<NameValuePair> nvps = new ArrayList<NameValuePair>();
      for (Map.Entry<String, String> entry : props.entrySet()) {
         nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
      }

      return new UrlEncodedFormEntity(nvps, charset);
   }

   private static void addBinBody(Object bean, MultipartEntity multipartEntity, Field field, BinaryMultipart ma)
   {
      for (Class<?> clz : bodies.keySet()) {
         if (field.getType().isAssignableFrom(clz)) {
            try {
               field.setAccessible(true);
               String name = ma.name().isEmpty() ? field.getName() : ma.name();
               ContentBody body = bodies.get(clz).convert(bean, field, ma);
               multipartEntity.addPart(name, body);
            } catch (Exception e) {
               LOGGER.error(e.getMessage(), e);
            } finally {
               field.setAccessible(false);
            }
            break;
         }
      }
   }

   private static void addBody(Object bean, MultipartEntity multipartEntity, Field field)
   {
      BinaryMultipart ma = field.getAnnotation(BinaryMultipart.class);
      if (ma != null) {
         addBinBody(bean, multipartEntity, field, ma);
      } else {
         addStringBody(bean, multipartEntity, field);
      }
   }

   private static void addStringBody(Object bean, MultipartEntity multipartEntity, Field field)
   {
      TextMultipart sa = field.getAnnotation(TextMultipart.class);
      if (sa != null) {
         try {
            field.setAccessible(true);
            StringBody body = new StringBody(field.get(bean).toString(), sa.mime(), Charset.forName(sa.charset()));
            multipartEntity.addPart(sa.name().isEmpty() ? field.getName() : sa.name(), body);
         } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
         } finally {
            field.setAccessible(false);
         }
      }
   }

}
