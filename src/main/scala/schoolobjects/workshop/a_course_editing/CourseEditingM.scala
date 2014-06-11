package schoolobjects.workshop.a_course_editing

import scalaz._, Scalaz._, Free.liftF
import schoolobjects._

import workshop.models._

// Defining the grammar for course editing
sealed trait CourseEditingF[+A]
case class CreateCourse[A](course: Course, f: (Course => A)) extends CourseEditingF[A]
case class GetCourse[A](id: Int, f: (Course => A)) extends CourseEditingF[A]
case class SaveCourse[A](course: Course, o: A) extends CourseEditingF[A]
case class DeleteCourse[A](course: Course, o: A) extends CourseEditingF[A]

case class AddActivity[A](course: Course, activity: Activity, o: A) extends CourseEditingF[A]
case class SetActivity[A](course: Course, activity: Activity, o: A) extends CourseEditingF[A]
case class RemoveActivity[A](course: Course, activity: Activity, o: A) extends CourseEditingF[A]


object CourseEditingM {

  // Implementing functor for course editing
  // This can actually be automatically generated using Coyoneda (?!?)
  // http://scastie.org/5664
  implicit def courseEditingFFunctor[B]: Functor[CourseEditingF] = new Functor[CourseEditingF] {
    def map[A,B](fa: CourseEditingF[A])(f: A => B): CourseEditingF[B] = 
      fa match {
        case CreateCourse(course, h) => CreateCourse(course, x => f(h(x)))
        case GetCourse(id, h) => GetCourse(id, x => f(h(x)))
        case SaveCourse(course, a) => SaveCourse(course, f(a))
        case DeleteCourse(course, a) => DeleteCourse(course, f(a))
        case AddActivity(course, activity, a) => AddActivity(course, activity, f(a))
        case SetActivity(course, activity, a) => SetActivity(course, activity, f(a))
        case RemoveActivity(course, activity, a) => RemoveActivity(course, activity, f(a))
      }
  }  

  // Creating the Free monad
  type CourseEditingFreeM[A] = Free[CourseEditingF, A]

  // Helper functions to chaining calls easier
  def createCourse(course: Course): CourseEditingFreeM[Course] = liftF { CreateCourse(course, a => a) }
  def getCourse(id: Int): CourseEditingFreeM[Course] = liftF { GetCourse(id, a => a) }
  def saveCourse(course: Course): CourseEditingFreeM[Unit] = liftF { SaveCourse(course, Unit) }
  def deleteCourse(course: Course): CourseEditingFreeM[Unit] = liftF { DeleteCourse(course, Unit) }
  def addActivity(course: Course, activity: Activity): CourseEditingFreeM[Unit] = liftF { AddActivity(course, activity, Unit) }
  def setActivity(course: Course, activity: Activity): CourseEditingFreeM[Unit] = liftF { SetActivity(course, activity, Unit) }
  def removeActivity(course: Course, activity: Activity): CourseEditingFreeM[Unit] = liftF { RemoveActivity(course, activity, Unit )}
}