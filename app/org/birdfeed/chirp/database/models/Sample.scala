package org.birdfeed.chirp.database.models

import com.google.inject.Inject
import org.birdfeed.chirp.database._
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{Json, Writes}

/**
  * Sample relation instance representing one row object.
  *
  * @param slickTE Slick table element from codegenerated tables
  */
class Sample @Inject()(dbConfigProvider: DatabaseConfigProvider)(val slickTE: Tables.Sample#TableElementType) extends Tables.SampleRow(
  slickTE.id, slickTE.name, slickTE.userId, slickTE.s3Url, slickTE.createdAt, slickTE.updatedAt
) with Relation[Tables#Sample] {

  override def equals(rhs: Any): Boolean = {
    if (rhs.getClass != this.getClass) { false }
    else {
      val cmp = rhs.asInstanceOf[this.type]
      (slickTE.id == id
        && slickTE.name == name
        && slickTE.userId == userId
        && slickTE.s3Url == s3Url
        && slickTE.createdAt.compareTo(createdAt) == 0
        && slickTE.updatedAt.compareTo(updatedAt) == 0
      )
    }
  }

  // TODO: Fill this in!
  implicit val jsonWrites: Writes[this.type] = Writes { sample =>
    Json.obj(
      "id" -> id,
      "name" -> name
    )
  }
}