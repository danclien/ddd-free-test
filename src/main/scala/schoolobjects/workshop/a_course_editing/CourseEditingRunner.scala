package schoolobjects.workshop.a_course_editing

import scalaz.{\/, ~>,Id}, Id.Id

import scala.slick.driver.PostgresDriver.simple._

import CourseEditingM._

import schoolobjects.workshop.database._
import schoolobjects.workshop.models._



class CourseEditingRunner(implicit val session: Session) {

  def run[A](program: CourseEditingFreeM[A]): \/[Throwable, A] = {
    \/.fromTryCatch { 
      session.withTransaction {
        runM(program) 
      }
    }
  }

  private def runM[A](program: CourseEditingFreeM[A]): A = {
    val exe: CourseEditingF ~> Id = new (CourseEditingF ~> Id) {
      def apply[B](l: CourseEditingF[B]): B = l match { 
        case CreateCourse(course, h) => h(createCourse(course))
        case GetCourse(id, h) => h(getCourse(id))
        case SaveCourse(_, a) => { println(l); a}
        case DeleteCourse(_, a) => { println(l); a}
        case AddActivity(_, _, a) => { println(l); a}
        case SetActivity(_, _, a) => { println(l); a}
        case RemoveActivity(_, _, a) => { println(l); a}
      }
    }

    program.runM(exe.apply[CourseEditingFreeM[A]])
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
