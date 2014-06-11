package schoolobjects.workshop.database

import scala.slick.driver.PostgresDriver.simple._

case class CourseRow(id: Int, title: String, description: String, status: String)

class Courses(tag: Tag) extends Table[CourseRow](tag, "course") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def title = column[String]("title")
  def description = column[String]("description")
  def status = column[String]("status")

  def * = (id, title, description, status) <> (CourseRow.tupled, CourseRow.unapply)
}

object Courses extends TableQuery(tag => new Courses(tag)) {
  val all = Courses.where(c => c.status === "enabled")
  val allWithDisabled = Courses

  def withId[A](query: Query[Courses, A], id: Int) = query.where(c => c.id === id)

  def insert(row: CourseRow)(implicit session: Session): Int = {
    (Courses returning Courses.map(_.id)) += row
  }     
}