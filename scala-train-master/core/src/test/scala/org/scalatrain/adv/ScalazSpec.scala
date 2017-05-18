package org.scalatrain.adv

class ScalazSpec extends UnitSpec {
  import scalaz._
  import Scalaz._

  "I" should "show Scalaz examples" in {
    (1 > 0) ? "true" | "false" // if (1 > 0) "true" else false

    1.some | 0 should be (1) // Some(1).getOrElse(0)

    "string".some.fold(Nil: List[String])(s => List(s))
    "string".some.cata(List(_), Nil) should be (List("string"))

    none | 1.some should be (1.some)

    val any: String = null
    (any ?? "default").length should be (7)

    // IdOps

    val len: String => Int = _.length

    val dbl: Int => Double = _.toDouble

    "string" |> len |> dbl should be (6.0) // len("string")

    "string" <| println should be ("string")

    "string" |> (len >>> dbl) should === (6.0) // andThen
    dbl <<< len apply "string" should === (6.0) // compose

    NonEmptyList
  }

  it should "show Equal and Show examples" in {
//    1 === 1
    1 === "foo"
    1.some =/= 2.some

    1.shows should be ("1")
    1.println
  }

  it should "show Validation examples" in {

    NonEmptyList(1, 2, 3)
    "NonEmptyList.empty" shouldNot compile
    List(1).toNel should be (1.wrapNel.some)

    object scope {
      sealed trait Validation[+E, +A] {
        /** Return `true` if this validation is success. */
        def isSuccess: Boolean = this match {
          case Success(_) => true
          case Failure(_) => false
        }
        /** Return `true` if this validation is failure. */
        def isFailure: Boolean = !isSuccess
      }

      final case class Success[E, A](a: A) extends Validation[E, A]
      final case class Failure[E, A](e: E) extends Validation[E, A]
    }

    1.success[String] should be (Success(1))
    "fail".failure[Int] should be (Failure("fail"))

    val result1 = 1.success[String]
    val result2 = "error 1".failure[Int]
    val result3 = "error 2".failure[Int]
    def calculate(a: Int, b: Int, c: Int) = a * b * c

    (result1, result2, result3) match {
      case (Success(a), Success(b), Success(c)) => calculate(a, b, c).success[Int]
      case _ => "fail".failure[Int]
    }

    val product = (result1 |@| result2 |@| result3).apply(calculate)
    product <| println should be ("error 1error 2".failure[Int])

    {
      val result1 = 1.successNel[String]
      val result2 = "error 1".failureNel[Int]
      val result3 = "error 2".failureNel[Int]

      val product = (result1 |@| result2 |@| result3).apply(calculate)
      val s = product.map(_.toString)
      s | "empty" should be ("empty") // getOrElse
      val r = s.leftMap(nel => nel.map(_ + " occured"))
      val errors = r.fold(f => f.toList, s => Nil)
      errors should be (List("error 1 occured","error 2 occured"))
    }
  }
}
