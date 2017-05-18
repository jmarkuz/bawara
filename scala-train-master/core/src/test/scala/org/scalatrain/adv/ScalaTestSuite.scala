package org.scalatrain.adv

import org.scalatest._
import org.scalatest.tagobjects._
import org.scalatest.tags.Slow

@Slow
class ScalaTestSuite extends FunSuite {
  test("An empty List should have size 0") {
    assert(Nil.size == 1)
    assertResult(0) {
      val l = List(1,2,3)
      val r = l.takeWhile(_ > 10)
      r.size
    }
    intercept[NoSuchElementException] {
      Set.empty.head
    }
  }

  /**
   * run db tests testOnly org.scalatrain.adv.ScalaTestSuite -- -n org.scalatest.tags.DB
   * run slow tests testOnly org.scalatrain.adv.ScalaTestSuite -- -n org.scalatest.tags.Slow
   * run without db tests testOnly org.scalatrain.adv.ScalaTestSuite -- -l org.scalatest.tags.DB
   */
  test("Assume slow test", Disk, CPU, Network, DB) {
    val connectedToDb = true
    assume(connectedToDb)
    assert(1 == 1, "It must be so")
    cancel()
  }

  object DB extends Tag("org.scalatest.tags.DB")

  ignore("Ignored test") {
    fail("EPIC")
  }

  def genTest(n: Int): Unit = assert(List.fill(n)(1).size == n)

  for (n <- 1 to 10) test(s"An List should have size $n")(genTest(n))
}
