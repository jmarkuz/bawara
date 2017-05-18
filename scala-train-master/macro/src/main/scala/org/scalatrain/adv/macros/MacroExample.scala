package org.scalatrain.adv.macros

import scala.language.experimental.macros
import scala.language.dynamics
import scala.reflect.macros.whitebox
import scalaz.Lens

/**
 * http://docs.scala-lang.org/overviews/macros/overview.html
 * http://docs.scala-lang.org/overviews/quasiquotes/syntax-summary.html
 * http://infoscience.epfl.ch/record/185242/files/QuasiquotesForScala.pdf
 */

trait Str[A] {
  def str(a: A): String
}

object MacroExample  {


  implicit def strGen[A]: Str[A] = macro MacroExampleImpl.strGen[A]

  def macro1(code: Any): String = macro MacroExampleImpl.macro1

  def timed1[A](code: => A): A ={
    val start = System.nanoTime()
    val r = code
    val diff = System.nanoTime() - start
    println(diff)
    r
  }
  def timed[A](code: => A): A = macro MacroExampleImpl.timed[A]

  def asrt(code: Boolean): Unit = macro MacroExampleImpl.asrt

  implicit class Ops[A](c: A) {
    def lens = new FieldGen(c)
  }
  class FieldGen[A](c: A) extends Dynamic {
    def selectDynamic(propName: String): Lens[A, _] = macro MacroExampleImpl.selectDynamic[A]
  }

  def genLens[S](fieldName: String) = macro GenLensMacroImpl.mkLensImpl2[S]
}

object MacroExampleImpl {


  def strGen[A: c.WeakTypeTag](c: whitebox.Context): c.Expr[Str[A]] = {
    import c.universe._
    val aTpe = weakTypeOf[A]

    c.Expr[Str[A]](q"""
                       new org.scalatrain.adv.macros.Str[$aTpe] {
                         def str(a: $aTpe) = a.toString
                       }
                   """)
  }

  def macro1(c: whitebox.Context)(code: c.Expr[Any]): c.Expr[String] = {
    import c.universe._
    val tree = code.tree
    val str = showCode(tree)
    val ast = showRaw(tree)
    val strast = s"$str\n$ast"

//    c.abort(c.enclosingPosition, "EPIC FAIL")

    c.Expr(q"$strast")
  }

  def timed[A: c.WeakTypeTag](c: whitebox.Context)(code: c.Expr[A]): c.Expr[A] = {
    import c.universe._
    val tree = code.tree
    val str = showCode(tree)
    c.Expr(q"""
           {
           val start = System.nanoTime()
           val r = $tree
           val diff = (System.nanoTime() - start) / 1000000
           println($str + " run in " + diff + "ms")
           r
      }""")
  }

  def asrt(c: whitebox.Context)(code: c.Expr[Boolean]): c.Expr[Unit] = {
    import c.universe._

    val tree = code.tree
    println(showRaw(tree))

    val msg = tree match {
      case q"$lhs == $rhs" => s"$lhs should be $rhs"
      case _ => s"${showCode(tree)} was false"
    }

    val str = showCode(tree)
    c.Expr(q"""
      if (!($tree)) throw new RuntimeException($msg)
      """)
  }

  def selectDynamic[A: c.WeakTypeTag](c: whitebox.Context)(propName: c.Expr[String]) = {
    import c.universe._
    val (aTpe) = weakTypeOf[A]
    val name = propName.tree
    val caller = c.prefix.tree
    val q"org.scalatrain.adv.macros.MacroExample.Ops[$userType]($expr).lens" = caller

    println(showRaw(caller))
    println(showCode(caller))
    c.Expr(q"org.scalatrain.adv.macros.MacroExample.genLens[$aTpe]($propName)")
  }


}