package schoolobjects.workshop.a_course_editing

import scalaz.{\/, ~>,Id}, Id.Id

import schoolobjects.workshop.database._
import schoolobjects.workshop.models._

import CourseEditingM._


object CourseEditingPrinter {

  def print[A](program: CourseEditingFreeM[A]): A = {
    val exe: CourseEditingF ~> Id = new (CourseEditingF ~> Id) {
      def apply[B](l: CourseEditingF[B]): B = l match { 
        case CreateCourse(_, h) => { println(l); h(Course(None, "Title", "Description", List()))}
        case GetCourse(id, retValue) => { println(l); retValue(Course(Some(id), "Title", "Description", List())) }
        case SaveCourse(_, a) => { println(l); a}
        case DeleteCourse(_, a) => { println(l); a}
        case AddActivity(_, _, a) => { println(l); a}
        case SetActivity(_, _, a) => { println(l); a}
        case RemoveActivity(_, _, a) => { println(l); a}
      }
    }

    println()
    val result = program.runM(exe.apply[CourseEditingFreeM[A]])
    println()

    result
  }
}