package schoolobjects.workshop.e_with_validation

import scalaz._
import scalaz.syntax.functor._
import scalaz.effect._
import Id.Id

import scala.slick.driver.PostgresDriver.simple._

import CourseEditingM._
import schoolobjects.SchoolObjects.Free._

import schoolobjects.workshop.database._
import schoolobjects.workshop.models._

object CourseEditingMValidation {
  def validate[A](program: CourseEditingM[A]): \/[NonEmptyList[String], A] = {
    runFC(program.run)(forValidation)
  }

  private def forValidation: CourseEditingF ~> Id = new (CourseEditingF ~> Id) {
    def apply[A](l: CourseEditingF[A]): Id[A] = l match {
      case CreateCourse(course)   => validateCreateCourse(course)
      case GetCourse(id)          => getCourse(id)
      case SaveCourse(_)          => \/-(println(l))
      case DeleteCourse(_)        => \/-(println(l))
      case AddActivity(_, _)      => \/-(println(l))
      case SetActivity(_, _)      => \/-(println(l))
      case RemoveActivity(_, _)   => \/-(println(l))
    }
  }

  private def validateCreateCourse(course: Course) = {
    // Using the validation method that was already there
    // May be a cleaner way of extracting the ApplicativeBuilder from Course.withValidation
    val result = Course.withValidation(course.id, course.title, course.description)

    // Converts the ValidationResult[Course] into a \/[NonEmptyList[String], Course]
    // Also giving the course a valid `id` because future calls can depend on it
    result.disjunction.map(c => c.copy(id=Some(1)))
  }

  private def getCourse(id: Int) = {
    // Must return a valid course here. :(
    if(id > 0) \/-(Course(Some(1), "", "", List()))    
    else -\/(NonEmptyList("`id` cannot be <= 0"))
  }
}