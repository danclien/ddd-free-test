# Example Implmentation of Workshop using Free

## Changes - June 11, 2014

### Added implementations using `Coyoneda` and `EitherT`

* No need to manually write out the instance of `Functor` anymore
* Added ability to short-circuit processing at any step


## Changes - June 10, 2014

### Use `TableQuery` `object`s

Instead of in each file:

```
lazy val courses = TableQuery[Courses]
```

Create as a companion object to the `Table` class:

```
object Courses extends TableQuery(tag => new Courses(tag))
```


### Save common queries in the `TableQuery` `object`

```

object Courses extends TableQuery(tag => new Courses(tag)) {
  val all = Courses.where(c => c.status === "enabled")
  val allWithDisabled = Courses

  def withId[A](query: Query[Courses, A], id: Int) = query.where(c => c.id === id)

  def insert(row: CourseRow)(implicit session: Session): Int = {
    (Courses returning Courses.map(_.id)) += row
  }     
}

```


### Create a generic `ValidationResult[A]` type

Instead of:

```
trait Validation[R] {
  type StringValidation = ValidationNel[String, String]
  type EntityValidation = ValidationNel[String, R]
}
```

```
object Course extends Validation[Course] {

  def withValidation(id: Option[Int], title: String, description: String): EntityValidation = {
    (
      id.successNel[String] |@|
      validateLengthOf("Title", title, TitleMaxLength) |@| 
      validatePresenceOf("Title", title) |@| 
      validateLengthOf("Description", description, DescriptionMaxLength)
    ) { (id, title, _, desc) => Course(id, title, desc, List[Activity]()) }
  }

  private val TitleMaxLength = 256
  private val DescriptionMaxLength = 1024
}
```

Use:

* Generic ValidationResult that can be used in both validations that return `String` and other types.
* Validation object does not have to inherit from the Validation[R] trait anymore

```
trait Validation {
  type ValidationResult[A] = ValidationNel[String, A]
}
```

```
object Course {
  def withValidation(id: Option[Int], title: String, description: String): ValidationResult[Course] = {
    (
      id.successNel[String] |@|
      validateLengthOf("Title", title, 256) |@| 
      validatePresenceOf("Title", title) |@| 
      validateLengthOf("Description", description, 1024)
    ) { (id, title, _, desc) => Course(id, title, desc, List[Activity]()) }
  }
}

```


### Strongly-typed `Configuration` class

Use a strongly-typed configuration class to make it harder to fat-finger a config.


### Drop the cake pattern

Use constructor injection instead.


### Removed inherting from an `Entity` class

Plain objects are more flexible.

### Removed aggregates

Bounded contexts can be done using Free or other methods.

### Added transactions hanlding

Use a Free interpreter to handle transactions

### Namespace models/database entries

Possibly even adding an additional namespace under `database` and `models`.

```
schooolobjects.core       - Project
  framework           - Utility functions
  aware
    database          - Slick tables and queries
    models            - Domain models
  forethought
    database          - Slick tables and queries
    models            - Domain models
  workshop
    course_editing            - Bounded context for course editing
    database          - Slick tables and queries
    models            - Domain models
    
schoolobjects.aware.web       – Project (web project)

schoolobjects.forethought.web – Project (web project)

schoolobjects.workshop.web    – Project (web project)

  
```