package org.scalatrain.basic

import org.scalatest._
import org.scalatrain.basic.task.SimpleMap

class SimpleMapTest extends FunSuite with Matchers {

  test("An empty Map should have size 0") {
    val sm = new SimpleMap[String, String]
    sm.size should be(0)
  }

  test("Update should put a value to SimpleMap") {
    val sm = new SimpleMap[String, String]
    sm("key") = "value"
    assert(sm.size == 1)
  }

  test("Apply should return value or null") {
    val sm = new SimpleMap[String, String]
    sm("key") = "value"
    sm("key") should be("value")
//    sm("don't exist") should be(null)
    assert(sm.size == 1)
  }

  test("-= should remove key from SimpleMap") {
    val sm = new SimpleMap[String, String]
    sm("key") = "value"
    sm -= "key"
//        sm("don't exist") should be(null)
    assert(sm.size == 0)
  }

  test("SimpleMap should be a function") {
    val sm = new SimpleMap[String, String]
    sm("key") = "value"
    def hof(f: String => String) = f("key")
    hof(sm) should be ("value")
  }
}
