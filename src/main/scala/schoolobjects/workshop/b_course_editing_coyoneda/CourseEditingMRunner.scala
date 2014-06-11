package schoolobjects.workshop.b_course_editing_coyoneda

import scalaz._
import scalaz.syntax.functor._
import scalaz.effect._

import scala.slick.driver.PostgresDriver.simple._

import CourseEditingM._

import schoolobjects.workshop.database._
import schoolobjects.workshop.models._

class CourseEditingMRunner(implicit val session: Session) {

  def run[A](program: CourseEditingFreeM[A]): \/[Throwable, A] = {
    \/.fromTryCatch { 
      session.withTransaction {
        runM(program)(toIO).unsafePerformIO   
      }      
    }
  }

  def toIO: CourseEditingF ~> IO = new (CourseEditingF ~> IO) {
    def apply[A](l: CourseEditingF[A]): IO[A] = l match {
      case CreateCourse(course)   => IO { createCourse(course) }
      case GetCourse(id)          => IO { getCourse(id) }
      case SaveCourse(_)          => IO { println(l) }
      case DeleteCourse(_)        => IO { println(l) }
      case AddActivity(_, _)      => IO { println(l) }
      case SetActivity(_, _)      => IO { println(l) }
      case RemoveActivity(_, _)   => IO { println(l) }
    }
  }

  private def createCourse(course: Course): Course = {
    val id = Courses.insert(CourseRow(0, course.title, course.description, "enabled"))
    course.copy(id=Some(id))
  }

  private def getCourse(id: Int): Course = {
    val query = Courses.withId(Courses.all, id)
    Course.fromDb(query.firstOption.get)
  }
}