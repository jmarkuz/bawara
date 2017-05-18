package org.scalatrain.basic.task

import org.scalatest.{Matchers, FunSuite}

class JsInterpreterTest extends FunSuite with Matchers {
  import JsInterpreter.{run => jsrun}

  test("Num should be num") {
    jsrun(JsNum(1)) should be("1")
  }

  test("2 + 3 = 5") {
    jsrun(JsBinOp("+", JsNum(2), JsNum(3))) should be("5")
  }

  test("3 - 2 = 1") {
    jsrun(JsBinOp("-", JsNum(3), JsNum(2))) should be("1")
  }

  test("concat strings") {
    jsrun(JsBinOp("+", JsString("3"), JsString("2"))) should be("32")
  }

  test("sum strings and nums ") {
    jsrun(JsBinOp("+", JsString("3"), JsNum(2))) should be("32")
    jsrun(JsBinOp("+", JsNum(2), JsString("3"))) should be("23")
    jsrun(JsBinOp("-", JsNum(2), JsString("3"))) should be("-1")
  }
}
