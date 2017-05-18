package org.scalatrain.basic

object Syntax {

  def main(args: Array[String]): Unit = {
    println("======= Keywords ======\n")
    keywords(args)
    println("======= Basic Types And Literals ======\n")
    basicTypesAndLiterals()
    println("======= Operators ======\n")
    operators()
    println("======= Sugar ======\n")
    sugar()
    println("======= Functions ======\n")
    functions()
    println("======= Partial Functions ======\n")
    partialFunctions()
    println("======= Implicits ======\n")
    implicits()
    usefullImplicits()
    println("=============\n")
  }

  def keywords(args: Array[String]): Unit = {
    // Imports
    import java.util.Date
    import java.lang.{Long=>JLong, Integer, _}
    import java.{util=>jutil}
    import jutil.{Map=>_, _}
    import java.lang.Integer.bitCount
    bitCount(1)

    import args._
    length

    // final int constant = 1
    val constant: Int = 1

    // long i = 0
    var i = 0L: Long // type ascription

    //var j: Int = i // Won't compile
    val j: Int = i.asInstanceOf[Int]
    j.isInstanceOf[Int]

    val xml = <bean>Can't live without Spring</bean>; println(xml)

    def calc = {
      println("Calculating the truth")
      Thread.sleep(1000)
      true
    }

    lazy val how = if (calc) "Scala" else "Java"

    while (i < 10) i += 1

    do i -= 1 while (i > 0)

    // for comprehension
    for (arg <- args)
      println(arg)

    if (how.equals("Scala") || (how == "Java") && (how.eq(null)) || (how ne null)) println("bugaga")

    val num = how match {
      case "Scala" => 1
      case "Java" => 2
      case _ => return
    }

    val result = try {
       "one".toInt
    } catch {
      case e: Exception => 0
    } finally {
      println("Finally, only side-effects here.")
    }
    println(result)
  }

  def basicTypesAndLiterals() = {
    val a: Byte = 1
    val b: Short = 2
    val c: Int = 0x3
    val d: Long = 4
    val e: Char = '\u0041'
    val f: Float = 0.5f
    val g: Double = .6e8d
    val h: Boolean = true || false
    val i: Array[String] = new Array[String](5)


    val j: String = s"a = $a, b = ${b.toString} \n"
    val k: String = """Multi
        line \n"""

    val l: String =
      """|10 LET A = 10
         |20 LET B = 20
         |30 PRINT A+B""".stripMargin


    println(j)
    println(k)
    println(l)


    val m: Symbol = 'OK // Erlang's Atom
    val n: Symbol = 'WTF
    val o = Symbol("OK")
    println(s"m == n ${m == n}, m == o ${m == o}")

    val p: Tuple3[Int, String, Boolean] = (1, "one", true)

    val q: Function1[String, Boolean] = (s: String) => s.isEmpty()

    val x: Unit = ()
    val y: Null = null
    lazy val z: Nothing = throw new RuntimeException("It's never gonna happen.")
  }

  def operators() = {
    // Binary operators

    // Infix
    1 + 2 == 1.+(2)
    1 == 1 == 1.==(1)

    // Postfix, not recommended
    val one = 1 toString // newline is required here!
    val i = one concat "0" charAt 0

    println(i)

    // Prefix unary operators: +, -, !, and ~
    class U {
      def unary_! = println("!!!")

      // right associative operators end with ':'
      def +:(i: Int) = println(s"+: $i")
    }
    val u = new U
    !u
    u.unary_!

    2 +: u // same as u.+:(2)
  }

  def sugar() = {
    // Apply
    val f = (s: String) => s.isEmpty()
    f.apply("Not empty") // false
    f("") // true

    object Callable {
      def apply() = println("Called!")
    }

    Callable.apply()
    Callable()

    // Update
    class Updatable {
      def update(i: Int, value: String): Unit = println(s"Update $i with $value")
    }
    val u = new Updatable
    u.update(5, "Test")
    u(5) = "Test"
  }

  def functions(): Unit = {
    // Named arguments, default values, multiple argument lists, call-by name arguments
    def uberFunction(arg1: String, arg2: Int = 0)(body: => Unit) = {
      println(arg1)
      body
      println(arg2)
    }

    uberFunction("Hello", 1)(println("World"))

    uberFunction("Hello")(println("World"))

    uberFunction(arg2 = 2, arg1 = "Hello") {
      println("World")
    }

    def IfElse(pred: Boolean)(cons: => Unit)(alt: => Unit) = if (pred) cons else alt
    IfElse (true) {
      println("In truth we trust")
    } {
      println("Or not")
    }

    def varargs(args: String*) = println(args)
    varargs()
    varargs("one")
    varargs("one", "two")

    // Anonymous Functions
    def isEmpty(arg: String): Boolean = arg.isEmpty // type is Function1[String, Boolean]
    val isEmpty2 = (arg: String) => arg.isEmpty // also can be written as (String => Boolean)

    val isEmpty3 = new Function1[String, Boolean] {
      override def apply(v1: String): Boolean = v1.isEmpty
    }

    val isEmpty4 = isEmpty _ // eta expansion

    isEmpty("") == isEmpty2("") == isEmpty3("") == isEmpty4("")

  }

  def partialFunctions() = {
    def isDigit(s: String) = try {
      Integer.valueOf(s)
      true
    } catch {
      case e: NumberFormatException => false
    }

    val pf = new PartialFunction[String, Int] {
      override def isDefinedAt(x: String): Boolean = isDigit(x)

      override def apply(v1: String): Int = Integer.valueOf(v1)
    }

    def applyOrDefault(s: String, default: Int) = {
      if (pf.isDefinedAt(s)) pf(s) else default
    }

    println(applyOrDefault("1 2 3", 0))

    println(applyOrDefault("123", 0))

    val pf2: PartialFunction[String, Int] = {
      case s: String if isDigit(s) => Integer.valueOf(s)
    }

    println(pf2.isDefinedAt("1 2 3"))
    println(pf2("123"))
  }

  class Complex(val real: Double, val imaginary: Double = 0.0) {
    def + (rhs: Complex): Complex = new Complex(real + rhs.real, imaginary + rhs.imaginary)
    //      def +[N : Numeric](n: N) = new Complex(implicitly[Numeric[N]].plus(real, n))
  }


  def implicits(): Unit = {

    // implicit defs

    val c = new Complex(1.0)

    implicit def int2Complex(i: Int): Complex = new Complex(i)

    // Conversions to an expected type
    val i: Complex = 1

    def foo(c: Complex) = println(c.real)

    foo(2)

    // Conversions of the receiver of a selection
    println(2.imaginary)
    println((2 + i).real)

    // Implicit parameters
    def bar(s: Int)(implicit convertor: Int => Complex): Complex = convertor(s)

    bar(123)(int2Complex)

    class Config { val url = "https://github.com/nau/jscala" }

//    implicit val config = new Config

    def contextual(msg: String)(implicit c: Config) = {
      c.url
      import c._
      url
    }

//    contextual("asdf")
  }

  def usefullImplicits() = {
    1 -> "one" == (1, "one")
    1 →  "one". == (1, "one")

    "1".toInt

    val str = "abc"

    str.charAt(0) == str(0)

    val regex = "M | [IN]|B".r
  }

}
