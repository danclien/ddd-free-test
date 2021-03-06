package schoolobjects.workshop.e_with_validation

import scalaz._
import scalaz.syntax.functor._
import scalaz.effect._

import scala.slick.driver.PostgresDriver.simple._

import schoolobjects.workshop.database._
import schoolobjects.workshop.e_models._

class CourseEditingRepo(val session: Session) {
  implicit val s = session

  def createCourse(course: Course) = {
    val id = Courses.insert(CourseRow(0, course.title.value, course.description.value, "enabled"))
    course.copy(id=Some(id))
  }

  def getCourse(id: Int) = {
    val query = Courses.withId(Courses.all, id)
    Course.fromDb(query.firstOption.get)
  }
}
