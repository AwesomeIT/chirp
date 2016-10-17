package org.birdfeed.chirp.auth.models

sealed trait Role {
  def level: Int
}

object Role {
  case object Administrator extends Role { val level = 0 }
  case object Researcher extends Role { val level = 1 }
  case object Evaluator extends Role { val level = 2 }

  def valueOf(value: String): Role = value match {
    case "Administrator" => Administrator
    case "Researcher"    => Researcher
    case "Evaluator"    => Evaluator
    case _ => throw new IllegalArgumentException()
  }
}