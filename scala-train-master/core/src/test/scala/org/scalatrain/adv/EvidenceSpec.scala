package org.scalatrain.adv

class EvidenceSpec extends UnitSpec {
  trait Trade {
    def cpty = "DB"
  }
  class Swap extends Trade
  class Fx extends Trade
  class Fra extends Trade

  def process(ts: Iterable[Swap]) = ???
//    def process(ts: Iterable[Fx]) = ???

  trait Processable[A <: Trade]
  implicit object ProcessableSwap extends Processable[Swap]
  implicit object ProcessableFx extends Processable[Fx]

  def processSwapOrFx[A <: Trade](ts: Iterable[A])(implicit ev: Processable[A]) = ts.map(_.cpty)

  "processSwapOrFx" should "process only Swap and Fx trades" in {
    processSwapOrFx(new Swap :: Nil) should === (List("DB"))
    processSwapOrFx(new Fx :: Nil) should === (List("DB"))
    "processSwapOrFx(new Fra :: Nil)" shouldNot typeCheck
  }
}
