package org.birdfeed.chirp.database

import play.api.libs.json.Writes

/**
  * I am an idiot and this is worse
  * dstancu
  * hours spent here: 4
  */
trait Relation[T <: Any] {
  val slickTE: Any

  implicit val jsonWrites: Writes[this.type]

  override def equals(right: Any): Boolean = {
    if (right.getClass == this.getClass) {
      slickTE == right.asInstanceOf[this.type]
    } else { false }
  }
}
