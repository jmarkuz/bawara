package org.scalatrain.adv

import scala.annotation.implicitNotFound


class TypeClassesSpec extends UnitSpec {
  trait Trade extends Product with Serializable {
    def cpty = "DB"
  }
  case class Swap(notional: Double = 1.0) extends Trade
  case class Fx(price: Double = 2.0) extends Trade
  case class Fra() extends Trade

  // Type class
  @implicitNotFound("No member of type class Show in scope for ${A}")
  trait Show[A] {
    def show(a: A): String
  }
  implicit class ShowOps[A](a: A)(implicit s: Show[A]) {
    def show: String = s.show(a)
  }
  implicit object GenericTradeShow extends Show[Trade] {
    override def show(a: Trade): String = a.toString
  }
  implicit object FxShow extends Show[Fx] {
    override def show(a: Fx): String = a.price.toString
  }

  "Generic show operation" should "show trades" in {
    (new Fx).show
//    new ShowOps(1).show
    println(List(new Fx, new Swap).map(_.show))
  }

  class LogService {
    implicit object LogFxShow extends Show[Fx] {
      def show(a: Fx): String = a.price.toString
    }
  }

  trait Num[A] {
    def plus(l: A, r: A): A
    def div(l: A, r: Int): A
  }

  object Operations {
    def sum(xs: Seq[Int]) = xs.reduce(_ + _)
    def avg(xs: Seq[Int]) = sum(xs) / xs.size
  }

  "Sum and avg" should "be calculated for Int and Double" in {
//    Operations.sum(Seq(1, 2, 3)) should be (6)
//    Operations.avg(Seq(2.0, 4.0)) should be (3.0 +- 0.1)
  }


}
