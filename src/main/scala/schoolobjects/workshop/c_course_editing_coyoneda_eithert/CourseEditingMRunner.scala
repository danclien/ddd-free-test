package schoolobjects.workshop.c_course_editing_coyoneda_eithert

import scalaz._
import scalaz.syntax.functor._
import scalaz.effect._

import scala.slick.driver.PostgresDriver.simple._

import CourseEditingM._
import schoolobjects.SchoolObjects.Free._

import schoolobjects.workshop.database._
import schoolobjects.workshop.models._

class CourseEditingMRunner(implicit val session: Session) {

  def run[A](program: CourseEditingM[A]): \/[Throwable, A] = {
    \/.fromTryCatch { 
      session.withTransaction {
        val result = runFC(program.run)(toIO).unsafePerformIO

        result match {
          case \/-(success) => success
          case -\/(error) => throw new Exception(error)
        }
      }
    }
  }

  def toIO: CourseEditingF ~> IO = new (CourseEditingF ~> IO) {
    def apply[A](l: CourseEditingF[A]): IO[A] = l match {
      case CreateCourse(course)   => IO { createCourse(course) }
      case GetCourse(id)          => IO { getCourse(id) }
      case SaveCourse(_)          => IO { \/-(println(l)) }
      case DeleteCourse(_)        => IO { \/-(println(l)) }
      case AddActivity(_, _)      => IO { \/-(println(l)) }
      case SetActivity(_, _)      => IO { \/-(println(l)) }
      case RemoveActivity(_, _)   => IO { \/-(println(l)) }
    }
  }

  private def createCourse(course: Course) = {
    val id = Courses.insert(CourseRow(0, course.title, course.description, "enabled"))
    \/-(course.copy(id=Some(id)))
  }

  private def getCourse(id: Int) = {
    val query = Courses.withId(Courses.all, id)
    \/-(Course.fromDb(query.firstOption.get))
  }
}
