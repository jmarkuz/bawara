package org.scalatrain.basic

import org.scalatest._
import org.scalatrain.basic.task.SimpleMap

class SimpleMapHofTest extends FunSuite with Matchers {

  test("SimpleMap should have filterKey function") {
    val sm = new SimpleMap[String, String]
    sm("key") = "value"
    sm.filterKey(_ != "key").size should be (0)
  }

  test("SimpleMap should have filter function") {
    val sm = new SimpleMap[String, String]
    sm("key") = "value"
//    sm.filter(_ != ("key", "value")).size should be (0)
  }

  test("SimpleMap should have map function") {
    val sm = new SimpleMap[String, String]
    sm("key") = "value"
    val res = sm.map(kv => (kv._1.length.toString, kv._2.length.toString))
//    res("3") should be ("5")
  }

  test("SimpleMap should have flatMap function") {
    val sm = new SimpleMap[String, String]
        sm("key") = "value"
//    sm.flatMap(kv => new SimpleMap).size should be (0)
  }

  test("SimpleMap should have fold function") {
    val sm = new SimpleMap[String, String]
        sm("key") = "value"
    sm.foldLeft(""){ case (acc, kv) => acc + kv._1 + kv._2 } should be ("keyvalue")
  }

  test("SimpleMap should have collect, get, and apply constructor") {
    val sm = SimpleMap("key" -> "value", "key1" -> "")
    val res = sm.collect{ case (k, v) if v.startsWith("val") => k -> "var" }
    res.size should be (1)
    res.get("key") should be(Some("var"))
    res.get("key1") should be(None)
  }

  test("Varience") {
    val mapping = 1.to(20).map(i => i -> i).foldLeft(new SimpleMap[Int, Int]) {
      case (sm,(key, value)) =>
        sm(key) = value
        sm
    }
    println(mapping)

    val mapping1 = SimpleMap((1 to 20).map(i => (i, i)) : _*)
    println(mapping1)

/*    val mapping2 = SimpleMap(1 -> "one", 22 -> "twenty two")
    mapping2.map{
      case  (key, value) =>
        val newValue = value.split("\\s+").map(_.length).
    }*/
    //println(mapping2)
  }
}
