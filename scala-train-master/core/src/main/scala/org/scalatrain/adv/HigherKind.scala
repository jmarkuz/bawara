package org.scalatrain.adv

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * http://adriaanm.github.io/files/higher.pdf
 * http://ropas.snu.ac.kr/~bruno/papers/ScalaGeneric.pdf
 * http://jnordenberg.blogspot.com/2008/08/hlist-in-scala.html
 * https://apocalisp.wordpress.com/2010/06/08/type-level-programming-in-scala/
 */
object HigherKind {
  class JavaStyle {

    trait Iterable[T] {
      def map[U](f: T => U): Iterable[U]
      def filter(p: T ⇒ Boolean): Iterable[T]
      def remove(p: T ⇒ Boolean): Iterable[T] = filter(x ⇒ !p(x))
    }

    trait List[T] extends Iterable[T] {
      def map[U](f: (T) => U): List[U] // redundant
      def filter(p: T ⇒ Boolean): List[T]
      // need to override
//      override def remove(p: T ⇒ Boolean): List[T] = super.remove(p)
      override def remove(p: T ⇒ Boolean): List[T] = filter(x ⇒ !p(x))
    }

  }

  class ScalaStyle {

    trait Iterable[T, Container[_]] {
      def map[U](f: T => U): Container[U]
      def filter(p: T ⇒ Boolean): Container[T]
      def remove(p: T ⇒ Boolean): Container[T] = filter(x ⇒ !p(x))
    }

    trait List[T] extends Iterable[T, List]
    trait Option[T] extends Iterable[T, Option]
  }

  trait Functor[F[_]]{
    def map[A, B](fa: F[A])(f: A => B): F[B]
  }

  class TypeCurrying {
    type StringStringMap = Map[String, String]
    // partial application
    type StringMap[A] = Map[String, A]

    def foo[A](sm: StringMap[A]) = ???
    foo(Map("a" -> 1))

    type StringIntMap = StringMap[Int]

    // def map[A, B](c: List[A], f: A => B): List[B]
    type ListHolder = Functor[List]

    // def map[A, B](c: Map[String, A], f: A => B): Map[String, B]
    type StringMapFunctor = Functor[StringMap]

    type StringMapLambdaFunctor = Functor[({type x[a]=Map[String, a]})#x]

    type GenericMapLambdaFunctor[T] = Functor[({type x[a]=Map[T, a]})#x]
  }


  object ValueLevelVsTypeLevel {
    // abstract class
    abstract class C {
      type x
      val x: Int
    }

    // path dependent types
    def foo(arg: C) = arg.x //(referencing field value/function x in object C)
    type X = C#x // (referencing field type x in trait C)

    // function implementation
    def f(x: Int) : Int = x
    type f[x <: X] = x

    // partial application
    def curry(a: Int, b: String): Double = ???
    def curried(a: Int): String => Double  = curry(a, _)
    type Curry[A, B, C] = Tuple3[A, B, C]
    type BC[B, C] = Curry[Int, B, C]
    type ISD = BC[String, Double]
    implicitly[ISD =:= Tuple3[Int, String, Double]]
  }


  implicit class Mapper[A, Container[_]: Functor](c: Container[A]) {
    def myMap[B](f: A => B): Container[B] = implicitly[Functor[Container]].map(c)(f)
  }

  implicit object FutureFunctor extends Functor[Future] {
    override def map[A, B](fa: Future[A])(f: (A) => B): Future[B] = fa.map(f)
  }

  Future(123).myMap(_.toString)
//  List(1,2,3).myMap(_.toString)
}


object HListTest {
  sealed trait HList

  final class HNil extends HList {
    def ::[T](v : T) = HCons(v, this)
  }

  val HNil = new HNil()

  final case class HCons[H, T <: HList](head : H, tail : T) extends HList {
    def ::[T](v : T) = HCons(v, this)
  }

  type ::[H, T <: HList] = HCons[H, T]
  val :: = HCons // HCons.apply(_, _)

  val list = 10 :: "hello" :: true :: HNil

  def show(l: Int :: String :: Boolean :: HNil) = {
    val n :: _ :: _ :: HNil = l
    println(n)
  }

  def main(args: Array[String]) {
    show(list)
  }
}
