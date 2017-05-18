package org.scalatrain.adv

import javax.persistence.{Persistence, EntityManager}

object CakeApp {

  case class User(name: String)


  trait UserRepositoryComponent {
    def userLocator : UserLocator
    def userUpdater : UserUpdater

    trait UserLocator {
      def findAll: java.util.List[User]
    }

    trait UserUpdater {
      def save(user: User)
    }
  }

  trait UserRepositoryJPAComponent extends UserRepositoryComponent {
    val em: EntityManager
    def userLocator = new UserLocatorJPA(em)
    def userUpdater = new UserUpdaterJPA(em)

    class UserLocatorJPA(val em: EntityManager) extends UserLocator {
      def findAll = em.createQuery("from User", classOf[User]).getResultList
    }

    class UserUpdaterJPA(val em: EntityManager) extends UserUpdater {
      def save(user: User) { em.persist(user) }
    }
  }

  trait UserServiceComponent {
    def userService: UserService

    trait UserService {
      def findAll: java.util.List[User]
      def save(user: User)
    }
  }

  trait DefaultUserServiceComponent extends UserServiceComponent {
    this: UserRepositoryComponent =>

    def userService = new DefaultUserService

    class DefaultUserService extends UserService {
      def findAll = userLocator.findAll

      def save(user: User) {
        userUpdater.save(user: User)
      }
    }
  }

  object Container {
    val userServiceComponent = new DefaultUserServiceComponent with UserRepositoryJPAComponent {
      val em = Persistence.createEntityManagerFactory("cake.pattern").createEntityManager()
    }

    val userService = userServiceComponent.userService
  }

}
