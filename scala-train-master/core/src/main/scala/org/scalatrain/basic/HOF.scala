package org.scalatrain.basic

import java.util

object HOF {

  def main(args: Array[String]) {
    println("======= Higher Order Functions ======\n")
    hof()
    println("======= Currying ======\n")
    currying()
    println("======= Usefull HOFs =====\n")
    usefullHOFs()
  }

  def hof(): Unit = {
    // Command Pattern
    /*
    public interface Command {
      void execute();
    }
    public class MyCommand implements Command {
      public void execute() {
        System.out.println("MyCommand");
      }
    }
    public void doCommand(Command command) { command.execute(); }
     */
    def doCommand(f: () => Unit) = f
    doCommand(() => println("MyCommand"))

    // Strategy Pattern
    def filter(ints: Array[Int], p: Int => Boolean) =
      for (i <- ints) if (p(i)) println(s"Filter $i")

    val array = Array(1, 2, 3)

    filter(array, (i: Int) => i > 2)
    filter(array, (i) => i > 2)
    filter(array, i => i > 2)
    filter(array, _ > 2)

    // Factory, Converter
    def createFactory: Int => String = (i) => i.toString
  }

  def currying(): Unit = {
    // def func(a: A, b: B): C convert to def curriedFunc(a: A): B => C
    def foo(a: Int, b: String): Boolean = true
    def curried(a: Int): String => Boolean = foo(a, _)

    val func = curried(1)

    foo(1, "1") == func("1") == curried(1)("1")

    def filter(ints: Array[Int], p: Int => Boolean): Unit =
      for (i <- ints) if (p(i)) println(s"Filter $i")

    def curriedFilter(ints: Array[Int]): (Int => Boolean) => Unit = filter(ints, _)

    val array = Array(1, 2, 3)

    val arrayEnclosed = curriedFilter(array)
    arrayEnclosed(_ > 0)

    def filter2(ints: Array[Int])(p: Int => Boolean): Unit = filter(ints, p)
    def curriedFilter2(ints: Array[Int]): (Int => Boolean) => Unit = filter2(ints)
  }

  def usefullHOFs() = {

    def trololo(s: String): MyStringList = {
      val mySl = new MyStringList
      mySl += s + " la" * 8
      mySl
    }

    class MyStringList {
      var sl = new util.ArrayList[String]()

      def +=(s: String) = {
        sl.add(s)
      }

      def filter(p: String => Boolean) = {
        val it = sl.iterator()
        while (it.hasNext) {
          val curr = it.next
          if (p(curr) == false) it.remove()
        }
        this
      }

      filter(s => s == "Scala")
      filter(_.length > 42)

      def map(f: String => String): this.type = {
        var idx = 0
        while (idx < sl.size()) {
          val curr = sl.get(idx)
          sl.set(idx, f(curr))
          idx += 1
        }
        this
      }

      map(_.length.toString)

      def flatMap(f: String => MyStringList): this.type = {
        val result = new util.ArrayList[String]()
        val it = sl.iterator()
        while (it.hasNext) {
          val curr = it.next
          val list = f(curr).sl
          result.addAll(list)
        }
        sl = result
        this
      }

      flatMap(trololo)

      def fold(init: String, acc: (String, String) => String): String = {
        var result = init
        val it = sl.iterator()
        while (it.hasNext) {
          val curr = it.next
          result = acc(result, curr)
        }
        result
      }

      def concat: String = {
        var result = ""
        val it = sl.iterator()
        while (it.hasNext) {
          val curr = it.next
          result = result + curr
        }
        result
      }

      def concat2: String = fold("", (res: String, curr: String) => res + curr)

      def fold2(init: String)(acc: (String, String) => String): String = fold(init, acc)

      def concat3: String = fold2("")(_ + _)

      override def toString: String = sl.toString
    }

    // Examples:
    val mySl = new MyStringList
    mySl += "Scala"
    mySl += "Java"

    println(mySl)

    println(mySl.filter(_ == "Scala"))

    println(mySl.map(_.length.toString))

    println(mySl.flatMap(trololo))

    mySl += "6"

    println(mySl.fold2("") { (acc, curr) =>
      acc + "{" + curr + "} "
    })
  }

}
