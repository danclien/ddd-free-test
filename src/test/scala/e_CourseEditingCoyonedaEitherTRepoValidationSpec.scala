import scalaz._, Scalaz._

import org.scalatest._

import schoolobjects.framework.testing._
import schoolobjects.framework.configuration._
import schoolobjects.framework.database._

import schoolobjects._, SchoolObjects.Validation._

import schoolobjects.workshop.e_models._
import schoolobjects.workshop.e_with_validation._, CourseEditingM._

class CourseEditingCoyonedaEitherTRepoValidationSpec extends FunSpec with Matchers with IntegrationSpec {

  describe("CourseEditingMCoyonedaEitherTRepoValidation") {
    it("should return left disjuction if an exception is thrown") {
      val program = for (course <- getCourse(1)) yield course

      val courseEditingRunner = new CourseEditingMRunner(null)
      val result = courseEditingRunner.run(program)

      result shouldBe a [-\/[_]]
    }

    it("should create a course") {      
      // Raw values
      val title = "Create course test"
      val description = "My course description"

      // Validated course creation
      val courseValidation: ValidationResult[Course] = Course.create(title, description, List())

      // Run if it's a valid course        
      def onValid(course: Course) = {
        val program = for (newCourse <- createCourse(course)) yield newCourse

        database.withSession { session =>
          val courseEditingRunner = new CourseEditingMRunner(session)       
          courseEditingRunner.run(program) 
        }        
      }

      // Similar to (a |@| b) { onValid }, but with only one validation result
      val result = courseValidation.map { onValid }

      // Asserts
      result shouldBe a [Success[_]]
      result.map(r => r shouldBe a [\/-[_]])
    }

    it("should return a validation error for an invalid course") {
      val longTitle = List.fill(1000)("X").mkString("")
      val description = "My course description"

      // Validated course creation
      val courseValidation = Course.create(longTitle, description, List())

      // Run if it's a valid course        
      def onValid(course: Course) = {
        val program = for (newCourse <- createCourse(course)) yield newCourse

        database.withSession { session =>
          val courseEditingRunner = new CourseEditingMRunner(session)       
          courseEditingRunner.run(program) 
        }        
      }

      val result = courseValidation.map { onValid }

      // Asserts      
      result shouldBe a [Failure[_]]      
    }    

    it("should get a course") {
      val program = for (course <- getCourse(1)) yield course

      database.withSession { session =>
        val courseEditingRunner = new CourseEditingMRunner(session)         
        val result = courseEditingRunner.run(program) 

        result shouldBe a [\/-[_]]
      } 
    }

    it("should create course + get course = same course") {
      /* Arrange */
      val title = "Create+GetCourse test"
      val description = "My course description"

      /* Act */
      val courseValidation = Course.create(title, description, List())

      val result = courseValidation.map { course =>
        val program = for {
          newCourse <- createCourse(course)
          readCourse <- { getCourse(newCourse.id.get) }
        } yield (newCourse, readCourse)

        database.withSession { session =>
          val courseEditingRunner = new CourseEditingMRunner(session)         
          val result = courseEditingRunner.run(program) 

          val coursesAreEqual = result.map(r => r._1 == r._2) 

          coursesAreEqual should equal (\/-(true))
        }        
      }

      /* Assert */
      result shouldBe a [Success[_]]
   }  

    it("should fail validation if a single course is invalid in multiple courses") {      
      /* Arrange */
      val title = "Create course test"      
      val description = "My course description"
      val longTitle = List.fill(1000)("X").mkString("")

      /* Act */
      val result = (
        Course.create(title, description, List())       |@|
        Course.create(longTitle, description, List())
      ) { (c1, c2) =>
        val program = for {
          newCourse1 <- createCourse(c1)
          newCourse2 <- createCourse(c2)
        } yield (newCourse1, newCourse2)

        database.withSession { session =>
          new CourseEditingMRunner(session).run(program)
        }
      }

      /* Assert */
      result shouldBe a [Failure[_]]
    }   
  }
}
