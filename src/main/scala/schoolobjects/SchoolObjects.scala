package schoolobjects

import schoolobjects.framework._

object SchoolObjects {
  object Validation extends validation.Validation with validation.TypesFunctions
  object SequenceTupleOption extends shapeless.SequenceTupleOption
  object FreeFunctions extends schoolobjects.framework.scalaz.FreeFunctions
}