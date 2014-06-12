# Example Implmentation of Workshop using Free

## June 12, 2014

### Added a separate `CourseEditingRepo` in `d_with_repo`

* Removed `implicit` on the `Session` parameter for `CourseEditingMRunner`
  * Using `implicit` only where required by Slick
* Takes in a `Session`
  * Allows handling of transactions at a higher level 
    * Probably in or near the controller
    * Currently handled in `CourseEditingMRunner.run`

### Added validation for `createCourse` in `e_with_validation`

#### Changes

* The "error" type for `CourseEditingM` changed to a NonEmptyList[String]
* Encoded validations in types found in `schoolobjects.framework.validation.Types`
  * Updated models in `schoolobjects.workshop.e_models` to use `String256` and `String1024` 
* Added example code in `e_CourseEditingCoyonedaEitherTRepoValidationSpec.scala`

#### Examples

##### Values with validation (e.g. `String256`)

* `schoolobjects.framework.validation.Types` shows how the types are created.
* `schoolobjects.workshop.e_models.Course` shows how the validated string types are used in the `case class`

##### Valid models

* `schoolobjects.workshop.e_models.Course` has a `create` method that can create a new Course that's valid.

##### How to use everything together

* `e_CourseEditingCoyonedaEitherTRepoValidationSpec.scala`'s test cases show off how the code can be used.
* The last test case, `""should fail validation if a single course is invalid in multiple courses""`, would be the closest to what we woud use in production.

#### Other Notes

* Validation can be done in two places
  * Model validation for an individual `Course`, `Activity`, etc. can be done by using the `Course.create` and `Activity.create` functions.
  * Validations that depend on previous/future actions can be done in `CourseEditingMRunner`


##### Additional cleanup that can be done

* Organize type aliases
  * Move type aliases for `Error` and `Result[A]` (`CourseEditingM.scala`) to an `object` in `schoolobjects.framework`
  * Other aliases should be standardized into a location too
* Change `String256` and `String1024` to be `class` instead of `case class`
* Implement `object` for `String256` and `String1024` with methods:
  * `def apply(value: String): String256` – should throw an exception if the input string is too long
  * `def try(value: String): ValidationNel[String, String256]` should validate and return `Success` or `Failure`
* Try to implement a parent class for `String256` and `String1024` to clean up code




### Recommended project organization/naming?

###### Random thoughts

Some point down the road, we should look over what the current models in the .NET stack is so we can organize our namespaces a bit better.

We do have 500+ models so some type of namespacing will be necessary. I think many of the models will be used in multiple apps so organizing by apps would be a bit awkward.

Organizing by a "context" may be the better options here, but we would need to come up with those contexts.


#### Rename project `schoolobjects.core` to `schoolobjects`

##### Namespaces

```
schooolobjects        
  framework         - Utility functions
  core              - User, Campus, Role, Right, etc.
    database      - Slick tables and queries
    models        - Domain models
  aware
    database      - Slick tables and queries
    models        - Domain models
  forethought
    database      - Slick tables and queries
    models        - Domain models
  workshop
    course_editing    - Bounded context for course editing
    database      - Slick tables and queries
    models        - Domain models
```

#### Naming for Play/web projects

`schoolobjects.aware.web`
`schoolobjects.forethought.web`
`schoolobjects.workshop.web`

## June 11, 2014

### Added implementations using `Coyoneda` and `EitherT`

* `Coyoneda` – No need to manually write out the instance of `Functor` anymore
* `EitherT` – Added ability to short-circuit processing at any step

### Switches to ScalaTest

* Switches to `ScalaTest` to avoid `scalaz` conficts that `specs2` was causing
  * Using `scalaz` 7.1.0-M7 in `ddd-free-test`
  * specs2 relies on `scalaz` 7.0.2
 

## June 10, 2014

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

### Added transactions handling

Use a Free interpreter to handle transactions