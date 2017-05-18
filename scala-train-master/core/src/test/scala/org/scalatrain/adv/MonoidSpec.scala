package org.scalatrain.adv

import java.util.Date

import scala.annotation.tailrec
import scala.language.higherKinds

class MonoidSpec extends UnitSpec {
  "Monoid" should "be fun" in {

    trait Semigroup[F] {
      def append(f1: F, f2: => F): F
    }

    implicit object DatePlus extends Semigroup[Date] {
      def append(f1: Date, f2: => Date): Date = new Date(f1.getTime + f2.getTime)
    }

    def plus[A](l: A, r: A)(implicit s: Semigroup[A]) = s.append(l, r)

    plus(new Date(1), new Date(2)) should be(new Date(3))

    /*def sum[A](ls: Seq[A])(implicit s: Semigroup[A]) =
      ls.foldLeft(0)(s.append(_, _).tupled) */




    trait Monoid[A] extends Semigroup[A] {
      def zero: A
    }

    implicit object DateMonoid extends Monoid[Date] {
      def append(f1: Date, f2: => Date): Date = new Date(f1.getTime + f2.getTime)

      def zero: Date = new Date(0)
    }

    def sum[A](ls: Seq[A])(implicit m: Monoid[A]): A =
      ls.foldLeft(m.zero)((a, e) => m.append(a, e))

    sum(List(new Date(1), new Date(2))) should be(new Date(3))
  }

  it should "be interesting" in {
    import scalaz._
    import Scalaz._

    1.some ⊹ 2.some should be(3.some)
    "1".some ⊹ "2".some should be("12".some)
    Set(1) ⊹ Set(2) ⊹ Set(1) should be(Set(1, 2))

    (List(List(1,2), List(3, 4)) |+| List(List(5), List(6))).println

    (none[Int]).orZero should be(0)
  }

  "Monad" should "be shown" in {
    import scalaz._
    import Scalaz._

    trait Monad[A] {
      // return, pure, point, constructor, you name it
      def pure(a: A): Monad[A]


      def bind[B](f: A => Monad[B]): Monad[B]


      final def flatMap[B](f: A => Monad[B]) = bind(f)
      final def >>=[B](f: A => Monad[B]) = bind(f)

      final def map[B: Monad](f: A => B): Monad[B] = {
        val mb = implicitly[Monad[B]]

        flatMap(a => mb.pure(f(a)))


        bind(f andThen mb.pure)

        f >>> mb.pure |> bind
      }
    }
  }

  "Monad examples" should "be shown" in {
    import scalaz._
    import Scalaz._

    1.pure[List] should be (List(1))
    1.pure[Option] should be (1.some)

    def calc[F[_] : Monad, A : Numeric](a: A, b: A) = {
      implicitly[Numeric[A]].plus(a, b).pure[F]
    }

    def listCalc[A : Numeric](a: A, b: A) = calc[List, A](a, b)

    def optionCalc[A : Numeric](a: A, b: A) = calc[Option, A](a, b)

    listCalc(1, 2) should be (List(3))

    optionCalc(BigDecimal(2), BigDecimal(3)) should be (BigDecimal(5).some)

    def calcF[F[_] : Monad, A : Monoid](as: F[A], bs: F[A]) = {
      for (a <- as; b <- bs) yield a |+| b
    }

    calcF(List(1, 2), List(3, 4)) should be (List(4, 5, 5, 6))

    calcF(2.some, none) should be (none)
  }

  it should "show monad laws" in {
    import scalaz._
    import Scalaz._

    val f = { x: Int => (x + 100000).some }
    val g = { x: Int => (x * 2).some }

    // Left identity
    (3.pure[Option] >>= f) should === (f(3))

    // Right identity
    (3.some >>= (_.pure[Option])) should === (3.some)

    // Associativity
    (3.some flatMap f flatMap g) should === (3.some flatMap (a => f(a) flatMap g))
  }

}
