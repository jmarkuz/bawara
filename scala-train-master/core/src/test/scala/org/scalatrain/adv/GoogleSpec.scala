package org.scalatrain.adv

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.scalatest.selenium.WebBrowser
import org.scalatest.{ShouldMatchers, FlatSpec}
import org.scalatest.concurrent.Eventually._
import org.scalatest.selenium.Page

object GoogleHomePage extends Page {
  val url = "http://www.google.com/?hl=en"
}

trait HtmlUnit { implicit val webDriver: WebDriver = new HtmlUnitDriver }
trait Chrome { implicit val webDriver: WebDriver = new ChromeDriver }
trait Firefox { implicit val webDriver: WebDriver = new FirefoxDriver }

class SimpleGoogleSpec extends GoogleSpec with HtmlUnit
class ChromeGoogleSpec extends GoogleSpec with Chrome
class FirefoxGoogleSpec extends GoogleSpec with Firefox

abstract class WebSpec extends FlatSpec with ShouldMatchers with WebBrowser {
  implicit val webDriver: WebDriver
}

abstract class GoogleSpec extends WebSpec {

  "The Google search" should "work" in {
    go to "http://www.google.com"
    go to GoogleHomePage
    click on "q"
    textField("q").value = "ScalaTest"
    submit()
    // Google's search is rendered dynamically with JavaScript.
    eventually {
      pageTitle should startWith ("ScalaTest")
      textField("q").value should be("ScalaTest")
    }
    click on linkText("Images")

    val q = find("q")
    println(q)

    val elements = findAll(className("lst")).toSeq
    val elements2 = findAll(cssSelector(".lst")).toSeq
    println(elements)
    println(elements2)
    elements.size should be (elements2.size)

    for (e <- elements; if e.tagName != "input") e should be ('displayed)

    pageSource

    add cookie ("name", "Alex")
    cookie("name").value should be ("Alex")

//    capture to "MyScreenShot.png"

    quit()
  }
}

