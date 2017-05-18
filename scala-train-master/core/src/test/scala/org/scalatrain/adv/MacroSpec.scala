package org.scalatrain.adv

import scala.language.experimental.macros
import org.scalatrain.adv.macros.{Str, MacroExampleImpl, MacroExample, GenLens}

class MacroSpec extends UnitSpec {
  import MacroExample._
  "Macro1" should "print code" in {
    val code = macro1 {
      val a = 1
      val b = 2
      println(a + b)
    }

    println(code)

    def longCalculation = {Thread.sleep(150); 1}
    println("Result: " + timed(longCalculation))

    val ex = the [RuntimeException] thrownBy asrt("1".toInt == 2)
    println(ex.getMessage)

//    println(admin.lens.name)
  }

  case class User(name: String, age: Int)

  val admin = User("admin", 45)


  "GenLens" should "generate Lens" in {

    val nameL = GenLens[User](_.name)

    nameL.get(admin) should be ("admin")






//    def calc[A : Str](a: A) = implicitly[Str[A]].str(a)
    def calc[A](a: A)(implicit ev: Str[A]) = ev.str(a)

    calc(2+2) should be ("4")
  }
}
