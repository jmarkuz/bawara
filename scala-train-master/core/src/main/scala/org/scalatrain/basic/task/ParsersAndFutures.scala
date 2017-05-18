package org.scalatrain.basic.task

import java.io.StringBufferInputStream

import org.scalatrain.basic.OOP.Config
import org.scalatrain.basic.Patterns.{Role, User}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration.Duration
import scala.util.parsing.combinator.RegexParsers
import scala.util.{Try, Failure, Success}
import scala.language.dynamics

object ParsersAndFutures {
  def main(args: Array[String]) {
//    futures()
    hack()
    parsers()
    ordersDsl()
  }

  def futures() = {
    def calc(i: Int) = {
      Thread.sleep(i)
      println(s"Calculated $i!")
      i
    }
    val f1 = Future {
      calc(1000)
    }
    val result = Await.result(f1, Duration("1 min"))
    println(result)

    // You can define your own thread pool
//    implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(10))

//    val fs = for (i <- 1 to 300 reverse) yield Future(calc(i))
    Thread.sleep(5000)

    println("==================")

//    val f = Future.sequence(fs)

    /*f.onComplete {
      case Success(results) => println(results)
      case Failure(e) => e.printStackTrace
    }

    f.onFailure { case e => e.printStackTrace }
    f.onSuccess { case res => println(res) }

    val newF = f andThen {
      case Success(results) =>
      case Failure(e) => e.printStackTrace
    }*/

    // Monad composition
    val fu = Future {User(1, "Martin", "", 50, Role("Creator"))}
    val fc = Future {new Config(Map("url" -> "jscala.org"))}
    val timeout = Duration("1 min")
    val user = Await.result(fu, timeout)
    val conf = Await.result(fc, timeout)

    val fn = fu.flatMap { case user =>
      fc.map {
        case c => user.name -> c.get("url")
      }
    }

    val fn2 = for {
      user <- fu
      if user.age > 18
      conf <- fc
    } yield user.name -> conf.get("url")

    fn
    fn2


    val f11 = Future(calc(100))
    val f12 = Future(calc(200))
    val f13 = Future(calc(300))
    val f14 = Future.firstCompletedOf(Seq(f11, f12, f13))
    println(Await.result(f14, timeout))


    val p = Promise[Int]()



    val ff = p.future

    Future {
      p.complete(Try(42))
    }

    println(Await.result(ff, timeout))


    type Closable =
    {
      def close(): Unit
    }

    def withResource[A <: Closable](r: A)(f: A => Unit) = {
      val res = f(r)
      r.close()
      res
    }

    class Res {
      def close(): Unit = println("HEHE")
    }

    withResource(new StringBufferInputStream("Hello"))(s => s.read())
    withResource(new Res)(println)



  }

  def hack() = {

    class Anything extends Dynamic {
      def updateDynamic(field: String)(value: Any) = {
        println(s"$field = $value")
      }
    }

    val any = new Anything

    any.deal = "Deal"
    any.cpty = "MSFT"

    val Expr = """(\w+)=(\d+)""".r
    "asdf=24" match {
      case Expr(name, value) => println(s"$name = $value")
    }

  }


  class JsExprParser extends RegexParsers {
    def plus: Parser[String] = literal("+")

    def minus: Parser[String] = "-"

    def op: Parser[String] = plus | minus

    def numFull: Parser[Int] = regex("""\d+""".r).map(s => s.toInt)

    def num: Parser[Int] = """\d+""".r ^^ (_.toInt)

    def stringLit1: Parser[~[~[String, String], String]] = "\"" ~ "\\w+".r ~ "\""

    def stringLit2: Parser[String ~ String ~ String] = "\"" ~ "\\w+".r ~ "\""

    def stringLit: Parser[String] = "\"" ~> "[^\"]+".r <~ "\""

    def jsString1 = stringLit map (s => JsString(s))

    def jsString = stringLit ^^ JsString

    def jsNum: Parser[JsNum] = num ^^ JsNum

    def term = jsNum | jsString

    def strings: Parser[List[JsString]] = jsString*

    def nums: Parser[Option[JsNum]] = jsNum.?

    def binop: Parser[JsBinOp] = term ~ op ~ expr ^^ { case l ~ op ~ r => JsBinOp(op, l, r) }

    def expr = binop | term

    def parseExpr(s: String) = parseAll(expr, s)
  }

  def parsers() = {
    val parser = new JsExprParser
    println(parser.parseExpr("1"))
    println(parser.parseExpr("\"string\""))
    println(parser.parseExpr(""" 1 - 2 + 3"""))
    println(parser.parseExpr(""" 1 - 2 + 3 + " is two" """))
    println(parser.parseAll(parser.nums, "1"))
  }

  def internalDsl() = {

    implicit def Num2JsNum(n: Int): Num2JsNum = new Num2JsNum(n)

    class Num2JsNum(n: Int) {
      def js = JsNum(n)
    }

//    println( 1.js + 2 - 1 + "3" )
    JsBinOp("+",
      JsBinOp("-",
        JsBinOp("+", JsNum(1), JsNum(2)),
        JsNum(1)),
      JsString("3"))
  }


  import scala.util.parsing.combinator.syntactical._

  object OrderDSL extends StandardTokenParsers {
    lexical.delimiters ++= List("(", ")", ",")
    lexical.reserved += ("buy", "sell", "shares", "at", "max", "min", "for", "trading", "account")

    def instr = trans ~ account_spec

    def trans = "(" ~> repsep(trans_spec, ",") <~ ")"

    def trans_spec = buy_sell ~ buy_sell_instr

    def account_spec = "for" ~> "trading" ~> "account" ~> stringLit

    def buy_sell = "buy" | "sell"

    def buy_sell_instr = security_spec ~ price_spec

    def security_spec = numericLit ~ ident ~ "shares"

    def price_spec = "at" ~ ("min" | "max") ~ numericLit

    def parseDsl(dsl: String) = {
      instr(new lexical.Scanner(dsl)) match {
        case Success(r, _) => println(r)
        case Failure(msg, _) => println(msg)
        case Error(msg, _) => println(msg)
      }
    }

  }

  def ordersDsl() = {
    val dsl =
      """(buy 100 IBM shares at max 45, sell 40 Sun shares at min 24,buy 25 CISCO shares at max 56)
         |for trading account "A1234" """.stripMargin
    OrderDSL.parseDsl(dsl)
  }

}
