package schoolobjects.framework.validation

import scalaz._, Scalaz._

// TODO: `String256` and `String1024` should be `class` not `case class`
// Need `object` for each that implements `apply`
// that throws an exception if length is exceeded
case class String256(val value: String) extends AnyVal
case class String1024(val value: String) extends AnyVal

trait TypesFunctions {
  def string256(value: String): ValidationNel[String, String256] = string256(value, "Value")  
  def string256(value: String, name: String): ValidationNel[String, String256] = {
    if(value.length > 256) s"$name must be shorter than 256 characters".failNel
    else (new String256(value)).successNel
  }

  def string1024(value: String): ValidationNel[String, String1024] = string1024(value, "Value")
  def string1024(value: String, name: String): ValidationNel[String, String1024] = {
    if(value.length > 1024) s"$name must be shorter than 256 characters".failNel
    else (new String1024(value)).successNel
  }

}