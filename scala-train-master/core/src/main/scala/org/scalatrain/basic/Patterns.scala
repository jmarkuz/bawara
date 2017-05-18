package org.scalatrain.basic

object Patterns {
  def main(args: Array[String]) {
    println("======= Case Classes ======\n")
    caseClasses()
    println("\n======= Options ======\n")
    options()
    println("\n======= Pattern Matching ======\n")
    patterns()
    println("\n======= For Expressions ======\n")
    forExpressions()
  }

  /**
   * 1. All arguments are immutable fields
   * 2. Pre-defined equals, hashCode and toString methods
   * 3. Generated apply() method that calls default constructor
   * 4. Generated unapply() method for pattern matching
   */
  abstract class JsExpr
  case class JsString(value: String) extends JsExpr
  case class JsNum(value: Int) extends JsExpr
  case class JsBinOp(operator: String, lhs: JsExpr, rhs: JsExpr) extends JsExpr
  case object JsNull extends JsExpr

  def caseClasses(): Unit = {

    class JsStringFull(val value: String) extends JsExpr {

      override def toString: String = getClass.getSimpleName + s"($value)"

      def canEqual(other: Any): Boolean = other.isInstanceOf[JsStringFull]

      override def equals(other: Any): Boolean = other match {
        case that: JsStringFull =>
          (that canEqual this) &&
            value == that.value
        case _ => false
      }

      override def hashCode(): Int = value.##

      def copy(newValue: String = this.value): JsStringFull = new JsStringFull(value = newValue)
    }

    object JsStringFull {
      def apply(value: String) = new JsStringFull(value)
      def unapply(x: JsStringFull): Option[String] = Some(x.value)
    }
    JsStringFull("asdf")

    val sum = JsBinOp("+", JsNum(1), JsNum(2))
    val java = new JsBinOp("+", new JsNum(1), new JsNum(2))
    println(sum)

    val _2x2 = sum.copy(operator = "*", lhs = JsNum(2))
    println(_2x2)
  }

  def options() = {
    val some: Option[String] = Some("Alex")
    val none: Option[String] = None

    println(some.isDefined)
    println(none.isEmpty)

    // Side effects
    println("------- foreach -------")
    println(some.foreach(r => println(r)))
    println(none.foreach(r => println(r)))

    // Map
    println("------- map -------")
    println(some.map(r => r.length))
    println(none.map(r => r.length))

    // flatMap
    println("------- flatMap -------")
    def getSurname(name: String): Option[String] = if (name == "Martin") Some("Odersky") else None

    println(Some("Martin").map(getSurname))

    println(Some("Martin").flatMap(getSurname))

    println(some.flatMap(getSurname))

    println(none.flatMap(getSurname))

    // Filter
    println("------- filter -------")
    println(some.filter(_ == "Alex"))
    println(some.filter(_ != "Alex"))
    println(none.filter(_ != "Alex"))

    println("------- toList -------")
    println(some.toList)

    println("------- get, orElse & getOrElse -------")
    println(some.get)
    println(none.getOrElse("Martin"))
    println(none.orElse(some))

    println("------- Java and NPE -------")
    println(Option("asdf"))
  }

  def patterns() = {
    val sum = JsBinOp("+", JsNum(1), JsNum(2))

    val two = 2


    (2: Any) match {
      // constant patterns
      case 1 =>
      case "sum" =>
      case None =>
      case Math.PI =>
      // variable patterns
      case anything =>
      case `two` =>
      // typed patterns & pattern guards
      case s: String if s.startsWith("Fly") =>
      // wildcard patterns
      case _ =>
    }

    // tuple patterns
    (1, "one") match {
      case (key, value) => println(s"$key -> $value")
    }

    sum match {
      // constructor patterns
      case JsBinOp("+", num, _) => num
      // variable binding
      case op @ JsBinOp("+", _, rhs @ JsNum(v2: Int)) if v2 > 2 => println(s"$op $rhs $v2")

        if (op.isInstanceOf[JsBinOp] && (op ne null) && op.operator == "+") {
          val binop = op.asInstanceOf[JsBinOp]
          if (binop.rhs.isInstanceOf[JsNum] && binop.rhs.ne(null)) {
            val rhs = binop.rhs.asInstanceOf[JsNum]
            if (rhs.value.isInstanceOf[Int] && rhs.value.asInstanceOf[Int] > 2) {
              val v2 = rhs.value
              println(s"$op $rhs $v2")
            }
          }
        }
    }

    // Patterns in variable definitions:
    val (a, b) = (1, "one")

    // Partial functions
    val withDefault: Option[Int] => Int = {
      case Some(x) => x
      case None => 0
    }

    println(withDefault(Some(42)))
    println(withDefault(None))

    def tryCatch(body: => Unit)(catchBlock: PartialFunction[Exception, Unit]) = {
      try body catch {
        case e: Exception => if (catchBlock.isDefinedAt(e)) catchBlock(e)
      }
    }

    tryCatch("hehe".toInt) {
      case e: NumberFormatException => println("hehe isn't a number")
    }


  }

  case class Role(name: String)
  case class User(id: Int, name: String, email: String, age: Int, role: Role)


  def forExpressions(): Unit = {
    // Basically, for-expression converts to either:
    // foreach
    // map
    // flatMap.map
    // flatMap.flatMap.map


    def getUserFromDb(id: Int): Option[User] =
      if (id == 1) Some(User(1, "Martin", "", 50, Role("Master"))) else None


    def saveDataForUser1(idOpt: Option[Int], dataOpt: Option[String]) = {
      idOpt match {
        case Some(id) => getUserFromDb(id) match {
          case Some(user) => dataOpt match {
            case Some(data) => println(s"Data $data saved for user $user")
            case None =>
          }
          case None =>
        }
        case None =>
      }
    }



    def saveDataForUser2(idOpt: Option[Int], dataOpt: Option[String]) = {
      idOpt.foreach { case id =>
        getUserFromDb(id).foreach { case user =>
          dataOpt.foreach { case data =>
            println(s"Data $data saved for user $user")
          }
        }
      }
    }



    def saveDataForUser3(idOpt: Option[Int], dataOpt: Option[String]) = {
      for (id <- idOpt;
           user <- getUserFromDb(id);
           data <- dataOpt)
        println(s"Data $data saved for user $user")
    }




    def saveDataForUser4(idOpt: Option[Int], dataOpt: Option[String]) = {
      for {
        id <- idOpt
        user <- getUserFromDb(id)
        data <- dataOpt
      } println(s"Data $data saved for user $user")
    }

    saveDataForUser1(None, None)
    saveDataForUser2(Some(1), None)
    saveDataForUser3(Some(2), Some("Scala rulez"))
    saveDataForUser4(Some(1), Some("Scala rulez"))

    def getUserRoleById(id: Int): Option[Role] = {
      getUserFromDb(id) map { user => user.role }

      for (user <- getUserFromDb(id)) yield user.role
    }

    def getUserById1(idOpt: Option[Int]): Option[User] = {
//        val res: Option[Option[User]] = idOpt.map { case id => getUserFromDb(id) }
      idOpt.flatMap { case id => getUserFromDb(id) }
    }

    def getUserById2(idOpt: Option[Int]) = {
      val res = (idOpt).flatMap { case id => (getUserFromDb(id)).map { case user => user}
      }
      res
    }

    def getRoleForUserById(idOpt: Option[Int]) = {
      for {
        id <- idOpt
        _ = println(id)
        User(_, name, _, age, role) <- getUserFromDb(id)
        roleName = role.name
        if age > 21
      } yield roleName
    }
  }
}
