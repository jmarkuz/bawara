package org.scalatrain.adv

import java.util
import javax.persistence.{TypedQuery, EntityManager}

import org.mockito.Mockito
import org.scalatest.mock.MockitoSugar
import org.scalatrain.adv.CakeApp.{UserRepositoryJPAComponent, DefaultUserServiceComponent, User}
import Mockito._

class CakeSpec extends UnitSpec with MockitoSugar {

  trait MockEntitManager {
    val em = mock[EntityManager]
  }

  trait TestContainer {
    val userService = new DefaultUserServiceComponent
      with UserRepositoryJPAComponent
      with MockEntitManager
  }

  "findAll" should "use the EntityManager's typed queries" in new TestContainer {
    val query = mock[TypedQuery[User]]
    val users: java.util.List[User] = new util.ArrayList[User]()

    when(userService.em.createQuery("from User", classOf[User])) thenReturn query
    when(query.getResultList) thenReturn users

    userService.userService.findAll should ===(users)
  }
}
