package schoolobjects.workshop.d_with_repo

import scalaz._
import scalaz.syntax.functor._
import scalaz.effect._

import scala.slick.driver.PostgresDriver.simple._

import schoolobjects.workshop.database._
import schoolobjects.workshop.models._

class CourseEditingRepo(val session: Session) {
  implicit val s = session

  def createCourse(course: Course) = {
    val id = Courses.insert(CourseRow(0, course.title, course.description, "enabled"))
    course.copy(id=Some(id))
  }

  def getCourse(id: Int) = {
    val query = Courses.withId(Courses.all, id)
    Course.fromDb(query.firstOption.get)
  }
}
