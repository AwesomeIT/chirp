package org.birdfeed.chirp.test

import org.scalatest._
import org.scalatest.words.ShouldVerb
import org.scalatestplus.play.{OneServerPerSuite, OneServerPerTest}
import play.api.Play

abstract class BaseSpec extends WordSpec with TestSuite with OneServerPerSuite with OneInstancePerTest with MustMatchers