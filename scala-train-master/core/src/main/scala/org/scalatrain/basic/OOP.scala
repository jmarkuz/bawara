package org.scalatrain.basic

import java.util.Date

import org.scalatrain.basic.Syntax.Complex

import scala.beans.BeanProperty
import scala.collection.mutable.ArrayBuffer

object OOP {
  def main(args: Array[String]) {
    classes()
    traits()
    generics()
    typeMembers()
    contextBounds()
  }

  def classes() = {

    abstract class AbstractDbService(val dbUrl: String, private var retryCount: Int = 0) {

      println(s"I'm ${getClass} constructor")

      def this() = {
        this("default", 1)
        println("Auxiliary constructor")
      }

      lazy val cache = {
        //calculate()
      }

      protected def doConnect(): Unit

      private[basic] val b = 2
      private val a = b + 1
      println(s"a = $a, b = $b, $name")
      val name = "Martin"
    }

    val anon = new AbstractDbService("") {
      override protected def doConnect(): Unit = println(s"Connect $b")
    }

    class OracleDbService extends AbstractDbService("oracle") {

      private val oracle = 1

      private[this] val thisOracle = 2

      def privacyTest(service: OracleDbService) = {
        service.oracle + thisOracle
        //        service.thisOracle
      }

      override protected def doConnect(): Unit = ???
    }

    val oracle = new OracleDbService

    class User {
      private var _name: String = _

      @BeanProperty def name: String = _name

      def name_=(n: String) = {
        if (n.nonEmpty) _name = n
      }

      @BeanProperty var age: Int = _

      object gender

    }

    val u = new User
    println(s"${u.name} ${u.age}")
    u.name = "Alex"
    u.name = ""
    u.setAge(30)
    println(s"${u.name} ${u.age} ${u.gender}")

  }

  class Animal {
    println(s"I'm Animal constructor")
  }

  trait Furry extends Animal {
    println(s"I'm Furry constructor")
  }

  trait HasLegs extends Animal {
    println(s"I'm HasLegs constructor")
  }

  trait HasHands {
    println(s"I'm HasHands constructor")
  }

  trait FourLegged extends HasLegs {
    println(s"I'm FourLegged constructor")
  }

  trait TwoLegged extends HasLegs {
    println(s"I'm TwoLegged constructor")
  }

  class Cat extends Animal with FourLegged with Furry{
    println(s"I'm Cat constructor")
  }

  class Config(c: Map[String, String]) {
    def get(n: String) = c(n)
  }

  def traits() = {
    // Linearization

    // Cat ￼ ￼ FourLegged ￼ ￼ HasLegs ￼ ￼ Furry ￼ ￼ Animal ￼ ￼ AnyRef ￼ ￼ Any
    val cat = new Cat
    val man = new Animal with TwoLegged with HasHands

    def compound(animal: HasLegs with HasHands) = {}
//        compound(cat)   // Won't compile
    compound(man)



    // Self type

    trait User { def name: String = getClass.getSimpleName }
    trait Tweeter {
      user: User =>

      def tweet(msg: String) = println(s"$name: $msg")
    }
//    trait Wrong extends Tweeter
    val user = new User with Tweeter


    // Stackable

    abstract class IntQueue {
      def get(): Int

      def put(x: Int)
    }

    class BasicIntQueue extends IntQueue {
      private val buf = new ArrayBuffer[Int]

      def get() = buf.remove(0)

      def put(x: Int) {
        buf += x
      }
    }
    val queue = new BasicIntQueue
    queue.put(10)
    println(queue.get())

    trait Doubling extends IntQueue {
      abstract override def put(x: Int) {
        super.put(2 * x)
      }
    }

    val doublequeue = new BasicIntQueue with Doubling
    doublequeue.put(10)
    println(doublequeue.get())

    trait Incrementing extends IntQueue {
      abstract override def put(x: Int) {
        super.put(x + 1)
      }
    }
    trait Filtering extends IntQueue {
      abstract override def put(x: Int) {
        if (x >= 10) super.put(x)
      }
    }

    val compQ = new BasicIntQueue with Doubling with Incrementing with Filtering
    compQ.put(7)
    compQ.put(10)
    compQ.put(20)
    println(compQ.get())
    println(compQ.get())



    implicit val config = new Config(Map("url" -> "jscala.org"))
    def $(key: String)(implicit config: Config) = config.get(key)


    val url = $("url")

  }

  def generics() = {
    import java.{util=>ju}


//    val j: ju.Collection[ju.Collection[Int]] = new ju.ArrayList[ju.ArrayList[Int]])()
    val ls: Traversable[Traversable[Int]] = List(List(1))

    class Base[+A, -B] {
//      def op(arg: A) = println(arg)
//      def get: B = ???
    }
    val a: Base[Animal, Cat] = new Base[Cat, Animal]

    // Self type and generics
    trait User { def name: String = getClass.getSimpleName }

    trait Tweeter[A <: User] {
      user: A =>
      def tweet[C](msg: C) = println(s"${user.name}: $msg")
      def as[C] = this.asInstanceOf[C]
    }
    // trait Wrong extends Tweeter
    val user = new User with Tweeter[User]
    user.tweet[String]("asdf")
  }

  def typeMembers() = {

    trait List {
      type Item <: Animal
      type Mapping[A] = Map[A, Item]

      class Dependent // Path dependent type
      def create = new Dependent

      def printDependent(i: Dependent) = println(i)

    }

    class CatList extends List {
      type Item = Cat
      type IntMapping = Mapping[Int]
      def getItem = new Cat
    }

    val l1 = new CatList
    val l2 = new CatList
    l2.printDependent(l2.create)
//    l2.printDependent(l1.create)
  }

  def contextBounds() = {
    def calculate1[A : Numeric](a: A, b: A) = {



    }
    def calculate2[A](a: A, b: A)(implicit ev1: Numeric[A]) = {
      ev1.plus(a, b)
    }

    println(calculate2(2, 3))
    println(calculate2(2.5, 3.5))
    println(calculate2(BigDecimal(3), BigDecimal(3)))
    implicit object ComplexNumeric extends Numeric[Complex] {
      override def plus(x: Complex, y: Complex): Complex = new Complex(x.real + y.real, x.imaginary + y.imaginary)

      override def toDouble(x: Complex): Double = ???

      override def toFloat(x: Complex): Float = ???

      override def toInt(x: Complex): Int = ???

      override def negate(x: Complex): Complex = ???

      override def fromInt(x: Int): Complex = ???

      override def toLong(x: Complex): Long = ???

      override def times(x: Complex, y: Complex): Complex = ???

      override def minus(x: Complex, y: Complex): Complex = ???

      override def compare(x: Complex, y: Complex): Int = ???
    }

    calculate2(new Complex(1), new Complex(2))


    def bean[A : Manifest](f: A => Unit): A = {
      val a = implicitly[Manifest[A]].runtimeClass.newInstance.asInstanceOf[A]
      f(a)
      a
    }
    println(bean[Date](d => d.setTime(0)))


    trait Match[A] {
      def getTypeName: String
    }
    implicit object StringMatch extends Match[String] {
      override def getTypeName: String = "String"
    }
    implicit object ListStringMatch extends Match[List[String]] {
      override def getTypeName: String = "List[String]"
    }

    def strange[A : Match](arg: A) = {
      implicitly[Match[A]].getTypeName
    }

    println(strange("Trololo"))
    println(strange(List("Trololo")))
//    strange(1)
  }
}
