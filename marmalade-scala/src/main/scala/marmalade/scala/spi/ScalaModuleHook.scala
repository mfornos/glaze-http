package marmalade.scala.spi

import marmalade.spi.HookProvider
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

/**
 * Registers {@link DefaultScalaModule} on every mapper.
 *
 */
class ScalaModuleHook extends HookProvider {

  override def acceptMapper(mime: String): Boolean = true

  override def acceptService(name: Class[_]): Boolean = false

  override def visitMapper(mime: String, mapper: ObjectMapper) = mapper.registerModule(DefaultScalaModule)

  override def visitService(name: Class[_], imp: AnyRef) = throw new UnsupportedOperationException

}