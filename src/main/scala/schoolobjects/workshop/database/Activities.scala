package schoolobjects.workshop.database

import scalaz._, Scalaz._
import scala.slick.driver.PostgresDriver.simple._

import schoolobjects._
import SchoolObjects.SequenceTupleOption._

case class ActivityRow(id: Int, title: String, courseId: Int)

class Activities(tag: Tag) extends Table[ActivityRow](tag, "activity") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def title = column[String]("title")
  def courseId = column[Int]("courseid")
  
  def * = (id, title, courseId) <> (ActivityRow.tupled, ActivityRow.unapply)

  def option = (id.?, title.?, courseId.?) <> (optionApply, optionUnapply)
  def optionApply(comp: (Option[Int], Option[String], Option[Int])): Option[ActivityRow] = {
    sequence(comp).map(ActivityRow.tupled)
  }

  def optionUnapply(row: Option[ActivityRow]): Option[(Option[Int], Option[String], Option[Int])] = None
}

object Activities extends TableQuery(tag => new Activities(tag)) {
  def forCourse(courseId: Int) = Activities.where(a => a.courseId === courseId)
}