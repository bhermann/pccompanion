package de.thewhitespace.pccompanion

/**
  * Automatic sequence for assigning ids to reviewers.
  * @author bhermann
  */
object ReviewSequence {
  var currentVal = 0

  def consume() : Integer = {
    currentVal += 1
    currentVal
  }
}
