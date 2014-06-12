package schoolobjects.workshop.e_models

import scalaz._, Scalaz._
import schoolobjects._

import schoolobjects.framework.validation._
import SchoolObjects.Validation._

import schoolobjects.workshop._
import database._

case class Activity(id: Option[Int], title: String256)

object Activity {
  def fromDb(a: ActivityRow) = {
    new Activity(Some(a.id), String256(a.title))
  }

  def create(title: String): ValidationResult[Activity] = {
    string256(title, "Title").map(Activity(None, _))
  }
}