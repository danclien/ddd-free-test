package schoolobjects.workshop.e_models

import scalaz._, Scalaz._
import schoolobjects._

import schoolobjects.framework.validation._
import SchoolObjects.Validation._

import schoolobjects.workshop._
import database._

case class Course(id: Option[Int], title: String256, description: String1024, activities: List[Activity])

object Course {
  def fromDb(a: CourseRow) = {
    new Course(Some(a.id), String256(a.title), String1024(a.description), List[Activity]())
  }  

  def create(title: String, description: String, activities: List[Activity]): ValidationResult[Course] = {
    (
      string256(title, "Title") |@| 
      string1024(description, "Description")
    ) { 
      Course(None, _, _, activities) 
    }
  }
}
