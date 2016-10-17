package org.birdfeed.chirp.build

import java.io.File
import com.typesafe.config.ConfigFactory

object TableGenerator {
  case class SlickGenerationError(message: String) extends Exception(message)

  def main(args: Array[String]): Unit = {
    lazy val slick_config = ConfigFactory.parseFile(
      new File("conf/application.conf")
    ).resolve()

    if (slick_config.isEmpty) throw SlickGenerationError("Cannot read application.conf")

    // Slick Driver, JDBC Driver, Output Folder, Package, User, Pass
    slick.codegen.SourceCodeGenerator.main(
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
