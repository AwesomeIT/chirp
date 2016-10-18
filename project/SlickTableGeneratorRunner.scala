package chirp.build

import java.io.File
import com.typesafe.config.ConfigFactory
import slick.codegen.SourceCodeGenerator

object SlickTableGeneratorRunner {
  case class SlickGenerationError(message: String) extends Exception(message)

  def generate = {
    lazy val slick_config = ConfigFactory.parseFile(
      new File("conf/application.conf")
    ).resolve()

    if (slick_config.isEmpty) throw SlickGenerationError("Cannot read application.conf")

    // Slick Driver, JDBC Driver, Output Folder, Package, User, Pass
    SourceCodeGenerator.main(
      Array[String](
        slick_config.getString("slick.dbs.default.driver").replace("$", ""),
        slick_config.getString("slick.dbs.default.db.properties.driver"),
        slick_config.getString("slick.dbs.default.db.url"),
        "app/",
        "org.birdfeed.chirp.database",
        slick_config.getString("slick.dbs.default.db.user"),
        slick_config.getString("slick.dbs.default.db.password")
      )
    )
  }
}
