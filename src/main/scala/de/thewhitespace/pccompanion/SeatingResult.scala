package de.thewhitespace.pccompanion

/**
  * Represents a result from the seating allocation algorithm.
  * @author bhermann
  */
class SeatingResult(val seatingPlan : Seq[Reviewer], val remainingConflicts: Seq[(Reviewer, Reviewer)] ) {

}
