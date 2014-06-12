import scalaz.{\/, \/-, -\/}

import org.scalatest._

import schoolobjects.framework.testing._

import schoolobjects.framework.configuration._
import schoolobjects.framework.database._
import schoolobjects.workshop.models._
import schoolobjects.workshop.b_course_editing_coyoneda._, CourseEditingM._

class CourseEditingCoyonedaSpec extends FunSpec with Matchers with IntegrationSpec {

  describe("CourseEditingMCoyoneda") {
    it("should return left disjuction if an exception is thrown") {
      val program = for (course <- getCourse(1)) yield course

      val courseEditingRunner = new CourseEditingMRunner()(null)
      val result = courseEditingRunner.run(program)

      result shouldBe a [-\/[_]]
    }

    it("should create a course") {
      val course = Course(None, "Create course test", "My course description", List())
      val program = for (newCourse <- createCourse(course)) yield newCourse

      database.withSession { implicit session =>
        val courseEditingRunner = new CourseEditingMRunner        
        val result = courseEditingRunner.run(program) 

        result shouldBe a [\/-[_]]
      }
    }

    it("should get a course") {
      val program = for (course <- getCourse(1)) yield course

      database.withSession { implicit session =>
        val courseEditingRunner = new CourseEditingMRunner        
        val result = courseEditingRunner.run(program) 

        result shouldBe a [\/-[_]]
      }
    }

    it("should create course + get course = same course") {
      val course = Course(None, "Create+GetCourse test", "My course description", List())      
      val program = for {
        newCourse <- createCourse(course)
        readCourse <- getCourse(newCourse.id.get)
      } yield (newCourse, readCourse)

      database.withSession { implicit session =>
        val courseEditingRunner = new CourseEditingMRunner        
        val result = courseEditingRunner.run(program) 

        val coursesAreEqual = result.map(r => r._1 == r._2) 

        coursesAreEqual should equal (\/-(true))
      }
    }  
  }
}
