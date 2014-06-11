import scalaz.{\/, \/-, -\/}

import org.scalatest._

import schoolobjects.framework.testing._

import schoolobjects.framework.configuration._
import schoolobjects.framework.database._
import schoolobjects.workshop.models._
import schoolobjects.workshop.a_course_editing._, CourseEditingM._


// class SetSpec extends FunSpec {

//   describe("A Set") {
//     describe("when empty") {
//       it("should have size 0") {
//         assert(Set.empty.size == 0)
//       }

//       it("should produce NoSuchElementException when head is invoked") {
//         intercept[NoSuchElementException] {
//           Set.empty.head
//         }
//       }
//     }
//   }
// }

class CourseEditingSpec extends FunSpec with Matchers with IntegrationSpec {

  // "A Stack" should "pop values in last-in-first-out order" in {
  //   val stack = new Stack[Int]
  //   stack.push(1)
  //   stack.push(2)
  //   stack.pop() should be (2)
  //   stack.pop() should be (1)
  // }

  // it should "throw NoSuchElementException if an empty stack is popped" in {
  //   val emptyStack = new Stack[Int]
  //   a [NoSuchElementException] should be thrownBy {
  //     emptyStack.pop()
  //   } 
  // }

  describe("CourseEditingM") {
    it("should print a chain of commands") {
      val newActivity = Activity(None, "New activity 1")

      val program = for {
        course <- getCourse(5)
        _ <- addActivity(course, newActivity)
        _ <- setActivity(course, newActivity)
        _ <- removeActivity(course, newActivity)
      } yield course

      val result = CourseEditingPrinter.print(program)
      println("Course: " + result)
    }

    it("should return left disjuction if an exception is thrown") {
      val program = for (course <- getCourse(1)) yield course

      val courseEditingRunner = new CourseEditingRunner()(null)
      val result = courseEditingRunner.run(program)

      result shouldBe a [-\/[_]]
    }

    it("should create a course") {
      val course = Course(None, "Create course test", "My course description", List())
      val program = for (newCourse <- createCourse(course)) yield newCourse

      database.withSession { implicit session =>
        val courseEditingRunner = new CourseEditingRunner        
        val result = courseEditingRunner.run(program) 

        result shouldBe a [\/-[_]]
      }
    }

    it("should get a course") {
      val program = for (course <- getCourse(1)) yield course

      database.withSession { implicit session =>
        val courseEditingRunner = new CourseEditingRunner        
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
        val courseEditingRunner = new CourseEditingRunner        
        val result = courseEditingRunner.run(program) 

        val coursesAreEqual = result.map(r => r._1 == r._2) 

        coursesAreEqual should equal (\/-(true))
      }
    }  
  }
}
