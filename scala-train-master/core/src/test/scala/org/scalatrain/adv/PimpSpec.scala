package org.scalatrain.adv

class PimpSpec extends UnitSpec {
  trait Trade {
    def cpty = "DB"
  }
  class Swap extends Trade {
    def notional = 1.0
  }
  class Fx extends Trade {
    def price = 2.0
  }
  class Fra extends Trade

  trait Showable {
    def showInLog: String = ???
    def showInXml: String = ???
    def showInUi: String = ???
  }

  class ShowableSwap extends Swap with Showable


  // Pimp My Library
  implicit class RichTrade(t: Trade) {
    def showInLog: String = t.toString
    def showInXml: String = t.toString
    def showInUi: String = t.toString
  }

  "Trade" should "be shown with Pimp My Library" in {
    (new Swap).showInLog should not be (empty)
  }

  // Pimp My Library
  implicit class FxShowOps(t: Fx) {
    def showInLog: String = t.price.toString
    def showInXml: String = t.toString
    def showInUi: String = t.toString
  }

  "Fx" should "be shown with Pimp My Library" in {
    (new Fx).showInLog should be ("2.0")
  }


  trait TradeShow {
    def show(t: Trade): String
  }
  object LogTradeShow extends TradeShow {
    def show(t: Trade) = t.toString
  }
  object XmlTradeShow extends TradeShow {
    def show(t: Trade) = t.toString
  }

  implicit class ShowTradeOp(t: Trade) {
    def showTrade(implicit kind: TradeShow): String = kind.show(t)
  }

  "Trade" should "be shown by simple Show type-class" in {
    class XmlService {
      implicit val showKind = XmlTradeShow
      (new Swap).showTrade should not be (empty)
    }
    new XmlService
    (new Swap).showTrade(LogTradeShow)
  }
}
