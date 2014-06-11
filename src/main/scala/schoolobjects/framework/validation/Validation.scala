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


// class String256(val value: String) extends AnyVal
// object String256 {
//   def fromString(value: String): Option[String256] = {
//     if(value.length > 256) None
//     else new Some(new String256(value))
//   }
// }

// class String1024(val value: String) extends AnyVal
// object String1024 {
//   def fromString(value: String): Option[String1024] = {
//     if(value.length > 1024) None
//     else new Some(new String1024(value))
//   }  
// }