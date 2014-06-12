package schoolobjects.workshop.d_with_repo

import scalaz._
import scalaz.syntax.functor._
import scalaz.effect._

import scala.slick.driver.PostgresDriver.simple._

import CourseEditingM._
import schoolobjects.SchoolObjects.Free._

import schoolobjects.workshop.database._
import schoolobjects.workshop.models._

class CourseEditingMRunner(val session: Session) {
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
