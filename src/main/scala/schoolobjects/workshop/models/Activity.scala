package schoolobjects.workshop.models

import scalaz._, Scalaz._
import schoolobjects._
import SchoolObjects.Validation._

import schoolobjects.workshop._
import database._

case class Activity(id: Option[Int], title: String)

object Activity {
  def fromDb(a: ActivityRow) = {
    new Activity(Some(a.id), a.title)
  }

  def withValidation(title: String): ValidationResult[Activity] = {
    withValidation(None, title)
  }

  def withValidation(id: Option[Int], title: String): ValidationResult[Activity] = {
    ( 
      id.successNel[String] |@|
      validateLengthOf("Title", title, 256) |@|
      validatePresenceOf("Title", title)
    ) { (id, title, _) => Activity(id, title) }
  }
}