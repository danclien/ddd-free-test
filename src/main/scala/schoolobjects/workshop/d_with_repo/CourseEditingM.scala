package schoolobjects.workshop.d_with_repo

import scalaz._, Scalaz._, Free._
import schoolobjects._

import workshop.models._

object CourseEditingM {
  type Result[A] = \/[String, A]

  // Defining the grammar for course editing
  sealed trait CourseEditingF[A]
  case class CreateCourse(course: Course) extends CourseEditingF[Result[Course]]
  case class GetCourse(id: Int) extends CourseEditingF[Result[Course]]
  case class SaveCourse(course: Course) extends CourseEditingF[Result[Unit]]
  case class DeleteCourse(course: Course) extends CourseEditingF[Result[Unit]]

  case class AddActivity(course: Course, activity: Activity) extends CourseEditingF[Result[Unit]]
  case class SetActivity(course: Course, activity: Activity) extends CourseEditingF[Result[Unit]]
  case class RemoveActivity(course: Course, activity: Activity) extends CourseEditingF[Result[Unit]]

  type CourseEditingCoyoneda[A] = Coyoneda[CourseEditingF, A]
  type CourseEditingFreeM[A] = Free.FreeC[CourseEditingF, A]
  type CourseEditingM[A] = EitherT[CourseEditingFreeM, String, A]

  implicit val courseEditingM = Free.freeMonad[CourseEditingCoyoneda]

  def lift[A](f: CourseEditingF[\/[String,A]]) = EitherT[CourseEditingFreeM, String, A] { liftFC { f } }

  def createCourse(course: Course) = lift { CreateCourse(course) }
  def getCourse(id: Int) = lift { GetCourse(id) }
  def saveCourse(course: Course)= lift { SaveCourse(course) }
  def deleteCourse(course: Course) = lift { DeleteCourse(course) }
  def addActivity(course: Course, activity: Activity) = lift { AddActivity(course, activity) }
  def setActivity(course: Course, activity: Activity)= lift { SetActivity(course, activity) }
  def removeActivity(course: Course, activity: Activity) = lift { RemoveActivity(course, activity)}
}
