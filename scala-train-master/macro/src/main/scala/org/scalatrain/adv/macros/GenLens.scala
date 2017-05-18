package org.scalatrain.adv.macros

import scala.language.experimental.macros
import scalaz.Lens

class GenLens[A] {
  def apply[B](field: A => B): Lens[A, B] = macro GenLensMacroImpl.genLensImpl[A, B]
}

object GenLens {
  def apply[A] = new GenLens[A]
}