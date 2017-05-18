package org.scalatrain.adv

import org.scalatest.{GivenWhenThen, FunSpec}

import scala.collection.mutable

class StackSpec extends FunSpec with GivenWhenThen {

  describe("A Stack") {

    it("should pop values in last-in-first-out-order") {

      Given("a non-empty stack")
      val stack = new mutable.Stack[Int]
      stack.push(1)
      stack.push(2)
      val oldSize = stack.size

      When("pop is invoked on the stack")
      val result = stack.pop()

      Then("the most recently pushed element should be returned")
      assert(result === 2)

      And("the stack should have one less item than before")
      assert(stack.size === oldSize - 1)
    }

    it("should throw NoSuchElementException if an empty stack is popped") {

      Given("an empty stack")
      val emptyStack = new mutable.Stack[String]

      When("pop is invoked on the stack")
      Then("NoSuchElementException should be thrown")
      intercept[NoSuchElementException] {
        emptyStack.pop()
      }

      And("the stack should still be empty")
      assert(emptyStack.isEmpty)
    }
  }
}