package org.birdfeed.chirp.database

import play.api.libs.json.Writes


trait Relation {
  // TODO: This is fucking horrible someone constrain this ASAP
  val slickTE: Any

  implicit val jsonWrites: Writes[this.type]

  override def equals(right: Any): Boolean = {
    if (right.getClass != this.getClass) {
      false
    } else { slickTE == right.asInstanceOf[this.type] }
  }
}
