package org.scalatrain.adv

import org.scalatest.prop.PropertyChecks

import scala.util.Try

/**
 * http://www.slideshare.net/JulienTruffaut/beyond-scala-lens
 * https://github.com/julien-truffaut/Monocle
 */
class LensSpec extends UnitSpec with PropertyChecks {

  case class Iso[A, B](to: A => B, from: B => A) {
    def compose[C](iso: Iso[B, C]): Iso[A, C] =
      Iso(a => iso.to(to(a)), c => from(iso.from(c)))

    def reverse: Iso[B, A] = Iso(from, to)

    def modify(f: B => B)(a: A): A = to andThen f andThen from apply a
  }

  "Iso" should "be useful" in {
    case class User(name: String)

    val userIso = Iso[User, String](_.name, User.apply(_))
    val s2l = Iso[String, List[Char]](_.toList, _.toString)
    val u2l = userIso compose s2l

    val l2u = u2l.reverse

    forAll((n: String) => {
      val u = User(n)
      userIso.from(userIso.to(u)) should === (u)
    })

    forAll((s: String) => userIso.to(userIso.from(s)) should ===(s))

    val updated = userIso.modify(_.capitalize)(User("alex"))
    updated should be(User("Alex"))
  }

  sealed trait Json
  case class JNumber(v: Double) extends Json
  case class JString(s: String) extends Json
  case class JObject(obj: Map[String, Json]) extends Json

  "Prism" should "be useful" in {
    case class Prism[A, B](getOption: A => Option[B], from: B => A) {
      def modifyOption(f: B => B)(a: A): Option[A] = getOption(a).map(f andThen from)

      def modify(f: B => B): A => A = a => modifyOption(f)(a).getOrElse(a)

      def compose[C](p: Prism[B, C]): Prism[A, C] = Prism(
        a => getOption(a).flatMap(b => p.getOption(b)),
        c => p.from andThen from apply c
      )
    }

    implicit class IsoToPrism[A, B](iso: Iso[A, B]) {
      def prism: Prism[A, B] = ???
    }
    case class User(name: String)

    val userIso = Iso[User, String](_.name, User.apply(_))

    userIso.prism

    val jNum: Prism[Json, Double] = Prism(getOption = {
      case JNumber(v) => Some(v)
      case _ => None
    },
    d => JNumber(d)
    )
    jNum.modify(_ + 1)(JNumber(2.0)) should be(JNumber(3.0))
    jNum.modify(_ + 1)(JString("")) should be(JString(""))
    jNum.modifyOption(_ + 1)(JString("")) should be(None)

    val double2Int = Prism[Double, Int](
      d => if (d == Math.floor(d)) Some(d.toInt) else None,
      _.toDouble
    )

    val jInt = jNum compose double2Int

    jInt.getOption(JNumber(2)) should be(Some(2))
    jInt.getOption(JNumber(3.2)) should be(None)
    jInt.getOption(JString("")) should be(None)
  }


  case class Config(servers: List[Server])
  case class Addr(host: String, port: Int)
  case class Server(enabled: Boolean, addr: Addr)

  val conf = Config(List(Server(true, Addr("localhost", 8080))))
  def modPort(c: Config, port: Int): Config = {
    ???
  }


  "Lens" should "be useful" in {

    modPort(conf, 8088).servers.head.addr.port should be(8088)

    case class Lens[S, A](get: S => A, set: (A, S) => S) {

      def modify(f: A => A): S => S = s => ???

      def compose[B](other: Lens[A, B]): Lens[S, B] = ???

      def compose[B](other: Iso[A, B]): Lens[S, B] = ???
    }

    def l = Lens[Config, List[Server]](???, ???)

    def serversRemover = l.modify(ss => Nil)

    serversRemover(conf).servers should be(empty)

    def serversAdder(s: Server) = l.modify(ss => s :: ss)
  }

  "Scalaz Lens" should "be useful" in {
     import scalaz._
     import Scalaz._

    val servers = Lens.lensu[Config, List[Server]](
      set = (c, ls) => c.copy(ls),
      get = _.servers
    )
    val headServer = Lens.lensu[List[Server], Server]((ls, s) => s :: ls.tail, _.head)
    val addr = Lens.lensu[Server, Addr]((s, a) => s.copy(addr = a), _.addr)
    val enabled = Lens.lensu[Server, Boolean]((s, a) => s.copy(enabled = a), _.enabled)

    val srv = Server(false, Addr("localhost", 8080))

    enabled.get(srv) should be(false)
    enabled.set(srv, true).enabled should be (true)
    enabled.mod(!_, srv).enabled should be (true)

    val toggle = enabled =>= (!_)
    (srv |> toggle).enabled should be (true)


    val conf = Config(List(srv, Server(true, Addr("production", 1234))))

    val mainServer = servers >=> headServer
    (mainServer =>= toggle)(conf).toString.println

    val port: Lens[Addr, Int] = ???
    def modPort(f: Int => Int): Config => Config = ???

    val modified = modPort(_ + 1)(conf)

    servers.get(modified) map (s => (addr >=> port).get(s)) should be (List(8081, 1235))
  }
}
