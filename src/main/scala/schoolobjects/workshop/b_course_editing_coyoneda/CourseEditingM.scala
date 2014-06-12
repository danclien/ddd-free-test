package schoolobjects.workshop.b_course_editing_coyoneda

import scalaz._, Scalaz._, Free._
import schoolobjects._

import workshop.models._

// Defining the grammar for course editing
sealed trait CourseEditingF[A]
case class CreateCourse(course: Course) extends CourseEditingF[Course]
case class GetCourse(id: Int) extends CourseEditingF[Course]
case class SaveCourse(course: Course) extends CourseEditingF[Unit]
case class DeleteCourse(course: Course) extends CourseEditingF[Unit]

case class AddActivity(course: Course, activity: Activity) extends CourseEditingF[Unit]
case class SetActivity(course: Course, activity: Activity) extends CourseEditingF[Unit]
case class RemoveActivity(course: Course, activity: Activity) extends CourseEditingF[Unit]

object CourseEditingM {
  type CourseEditingCoyoneda[A] = Coyoneda[CourseEditingF, A]
  type CourseEditingFreeM[A] = Free.FreeC[CourseEditingF, A]

  def createCourse(course: Course) = liftFC { CreateCourse(course) }
  def getCourse(id: Int) = liftFC { GetCourse(id) }
  def saveCourse(course: Course)= liftFC { SaveCourse(course) }
  def deleteCourse(course: Course) = liftFC { DeleteCourse(course) }
  def addActivity(course: Course, activity: Activity) = liftFC { AddActivity(course, activity) }
  def setActivity(course: Course, activity: Activity)= liftFC { SetActivity(course, activity) }
  def removeActivity(course: Course, activity: Activity) = liftFC { RemoveActivity(course, activity)}
}
