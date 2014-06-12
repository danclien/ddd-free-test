package schoolobjects.workshop.models

import scalaz._, Scalaz._
import schoolobjects._
import SchoolObjects.Validation._

import schoolobjects.workshop._
import database._, models._

case class Course(id: Option[Int], title: String, description: String, activities: List[Activity])

object Course {
  def fromDb(a: CourseRow) = {
    new Course(Some(a.id), a.title, a.description, List[Activity]())
  }  

  def withValidation(title: String, description: String): ValidationResult[Course] = {
    withValidation(None, title, description)
  }

  def withValidation(id: Option[Int], title: String, description: String): ValidationResult[Course] = {
		(
			id.successNel[String] |@|
			validateLengthOf("Title", title, 256) |@| 
			validatePresenceOf("Title", title) |@| 
			validateLengthOf("Description", description, 1024)
		) { (id, title, _, desc) => Course(id, title, desc, List[Activity]()) }
  }
}
