package org.scalatrain.basic

import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, ArrayBuffer}

object Collections {


  def main(args: Array[String]) {
    traversable()
    mutableCollections()
    immutableCollections()
    parallelCollections()
  }

  def traversable() = {

    val c = Array(1, 2, 3)

    val c2 = c.map(_ * 2)

    val c3 = c ++ c2

    println(c3.mkString)

//    val c4 = c3 collect { case i if i > 5 => 0 }
    val c4 = c3.filter { i => i > 5 }.map { i => 0 }
    println(c4.mkString)

    c.length
    c.isEmpty && c.nonEmpty
    c.hasDefiniteSize

    val result = c.find(_ > 2).map(_.toString)
    val result2 = c.collectFirst { case i if i > 2 => i.toString }
    println(result2)

    println(Array(1 -> "one", 2 -> "two").toList.apply(1))

    println(c.takeWhile(_ < 5).mkString)
    println(c.drop(1).mkString)
    println(c.dropWhile(_ < 5).mkString)

    println(c.head, c.tail.mkString)
    println(c.init.mkString, c.last)
    println(c.slice(1, 2).mkString)

    println(c.count(_ > 2))

    c.foldLeft (0) { case (acc, i) => acc +  i }
    (0 /: c) { case (acc, i) => acc +  i }

    c.reduceLeft { _ + _ } //1, 2 => 3, 3 => 6

    c.min + c.max + c.sum + c.product

    println(c.mkString("start", ",", "end"))

    // Addition, ++
    // map, flatMap, and collect
    // Conversions toArray, toList, toIterable, toSeq, toIndexedSeq, toStream, toSet, toMap
    // Size info operations isEmpty, nonEmpty, size, and hasDefiniteSize
    // Element retrieval operations head, last, headOption, lastOption, and find
    // Sub-collection retrieval operations tail, init, slice, take, drop, takeWhile, dropWhile, filter, filterNot, withFilter
    // Element tests exists, forall, count
    // Folds foldLeft, foldRight, /:, :\, reduceLeft, reduceRight
    // Specific folds sum, product, min, max
    // mkString
  }

  def mutableCollections(): Unit = {
    import collection.mutable
    // Seq: IndexedSeq, LinearSeq, Array, ArrayBuffer, ListBuffer
    val ab = ArrayBuffer("a", "b")


    "a" +: ab :+ "z"
    ab(0)

    // Set: HashSet
    val hs = mutable.HashSet("red", "green")
    hs += "blue"
    hs -= "red"
    hs.contains("green")
    hs & Set("blue", "yellow")
    println(hs & Set("blue", "yellow"))
    println(hs("green"))

    // Map: HashMap, TrieMap

    val m = mutable.HashMap((1, "one"), 2 -> "two")
    m.contains(1)
    m.getOrElseUpdate(3, hs.head)

    val m2 = ArrayBuffer(4 -> "four", 5 -> "five").toMap

    val m3 = m -- m2.keys

    m(1)

    m.update(6, "six")
    m(6) = "six"

    m += 6 -> "six"

    m.contains(1) == m.isDefinedAt(1)

    def foo(i: Int, mappingFunc: Int => String) = mappingFunc(i)
    foo(1, m)

    println(m)

    val f: Function1[Int, String] = _.toString
    val f1: Int => String = _.toString
    type ~[A, B] = Tuple2[A, B]

    val t: Int ~ String ~ Double = ((1, "2"), 3.0)

    case class !![A, B](a: A, b: B)

    new !!(new !!(1, "String"), 3.0) match {
      case a !! b !! c => println(a, b, c)
      case !!(!!(a, b), c) => println(a, b, c)
    }



  }

  def immutableCollections(): Unit = {
    import collection.immutable
    // Seq: List, Vector, Range
    val ls = List(1, 2, 3)

    val ls1 = 1 :: 2 :: 3 :: Nil

    val words = List("one", "two", "three")

    println(words.toIndexedSeq.sortWith { case (a, b) => a.size > b.size } )

    val map = (ls zip words).toMap

    for ((w, idx) <- words.zipWithIndex) println(w, idx)

//    println(map)

    val ls2 = ls ::: ls1

    ls match {
      case Nil =>
      case List(1, z @ _*) => println(z)
    }

    def foo(args: Int*) = println(args)

    foo(ls: _*)

    Vector(1, 2, 3)

    println(Range(1, 5))
    println(1 to 5)
    println(1 until 5)

    for (i <- 1 until ls.size) println(ls(i))

    // Set: HashSet
    Map.empty[Int, String]
    // Map: HashMap, TreeMap
  }

  def parallelCollections() = {

  }
}
