package org.scalatrain.basic.task

abstract class JsExpr
trait JsLiteral extends JsExpr
case class JsString(value: String) extends JsLiteral
case class JsNum(value: Int) extends JsLiteral
case class JsBinOp(operator: String, lhs: JsExpr, rhs: JsExpr) extends JsExpr

object JsInterpreter {
  def run(js: JsExpr): String = js match {
    case JsNum(v) => v.toString
//  case jsnum: JsNum => jsnum.value.toString //alternative

    case JsString(v) => v

    case JsBinOp("+", JsNum(lhs), JsNum(rhs)) => (lhs + rhs).toString
    case JsBinOp("-", JsNum(lhs), JsNum(rhs)) => (lhs - rhs).toString
    case JsBinOp("+", JsString(lhs), JsString(rhs)) => lhs + rhs
    case JsBinOp("+", JsNum(lhs), JsString(rhs)) => lhs.toString + rhs
    case JsBinOp("+", JsString(lhs), JsNum(rhs)) => lhs + rhs.toString
    case JsBinOp("-", JsString(lhs), JsNum(rhs)) => (lhs.toInt - rhs).toString
    case JsBinOp("-", JsNum(lhs), JsString(rhs)) => (lhs - rhs.toInt).toString
  }

  def interpret(js:JsExpr): JsLiteral = js match {
    case js: JsNum => js
    case js:JsString => js
    case JsBinOp("+", JsNum(lhs), JsNum(rhs)) => JsNum(lhs + rhs)
    case JsBinOp("-", JsNum(lhs), JsNum(rhs)) => JsNum(lhs - rhs)
    case JsBinOp("+", JsString(lhs), JsString(rhs)) => JsString(lhs + rhs)
    case JsBinOp("+", JsString(lhs), JsNum(rhs)) => JsString(lhs + rhs.toString)
    case JsBinOp("+", JsNum(lhs), JsString(rhs)) => JsString(lhs.toString + rhs)
    case JsBinOp("-", JsNum(lhs), JsString(rhs)) => JsNum(lhs - rhs.toInt)

    case JsBinOp(op, lhs, rhs) => interpret(JsBinOp(op, interpret(lhs) , interpret(rhs)))
  }
}
