package org.scalatrain.basic.task

import org.scalatest.{FunSuite, Matchers}
import org.scalatrain.basic.task.ParsersAndFutures.JsExprParser

class JsParserTest extends FunSuite with Matchers {
  import org.scalatrain.basic.task.JsInterpreter.{run => jsrun}

  def parse(s: String) = {
    (new JsExprParser).parseExpr(s).get
  }

  test("Num should be num") {
    jsrun(parse("1")) should be("1")
  }

  test("2 + 3 = 5") {
    jsrun(parse("2 + 3")) should be("5")
  }

  test("3 - 2 = 1") {
    jsrun(parse("3 - 2")) should be("1")
  }

  test("concat strings") {
    jsrun(parse(""" "3" + "2" """)) should be("32")
  }

  test("sum strings and nums ") {
    jsrun(parse(""" "3" + 2 """)) should be("32")
    jsrun(parse(""" 2 + "3" """)) should be("23")
  }
}
