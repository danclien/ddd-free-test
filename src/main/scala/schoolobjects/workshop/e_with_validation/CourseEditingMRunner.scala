package schoolobjects.workshop.e_with_validation

import scalaz._, effect._
import scala.slick.driver.PostgresDriver.simple._

import schoolobjects.SchoolObjects.FreeFunctions.runFC
import schoolobjects.workshop.database._
import schoolobjects.workshop.e_models._

import CourseEditingM._

class CourseEditingMRunner(val session: Session) {

  def run[A](program: CourseEditingM[A]): \/[Throwable, A] = {
    \/.fromTryCatch { 
      session.withTransaction {
        val result = runFC(program.run)(toIO).unsafePerformIO

        result match {
          case \/-(success) => success
          case -\/(error) => throw new Exception(error.list.mkString(", "))
        }
      }
    }
  }

  private val repo = new CourseEditingRepo(session)

  private def toIO: CourseEditingF ~> IO = new (CourseEditingF ~> IO) {
    def apply[A](l: CourseEditingF[A]): IO[A] = l match {
      case CreateCourse(course)   => IO { \/-(repo.createCourse(course)) }
      case GetCourse(id)          => IO { \/-(repo.getCourse(id)) }
      case SaveCourse(_)          => IO { \/-(println(l)) }
      case DeleteCourse(_)        => IO { \/-(println(l)) }
      case AddActivity(_, _)      => IO { \/-(println(l)) }
      case SetActivity(_, _)      => IO { \/-(println(l)) }
      case RemoveActivity(_, _)   => IO { \/-(println(l)) }
    }
  }
}
