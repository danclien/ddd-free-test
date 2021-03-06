package schoolobjects.framework.validation

import scalaz._, Scalaz._

trait Validation {
  type ValidationResult[A] = ValidationNel[String, A]

  def validateLengthOf(field: String, string: String, length: Int): ValidationResult[String] = {
    if (string.length > length)
      s"$field must be shorter than $length characters".failNel
    else
      string.successNel
  }

  def validatePresenceOf(field: String, string: String): ValidationResult[String] = {
    if (string.length == 0)
      s"$field must have a value".failNel
    else
      string.successNel
  }  
}


