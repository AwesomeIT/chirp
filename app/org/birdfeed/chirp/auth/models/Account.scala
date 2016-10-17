package org.birdfeed.chirp.auth.models

case class Account(id: Int, email: String, password: String, name: String, role: Role)

object Account {

}