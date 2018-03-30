package de.thewhitespace.pccompanion

/**
  * Represents a reviewer.
  * @author bhermann
  */
class Reviewer(val id : Integer) {
  def apply(id: Integer): Reviewer = new Reviewer(id)

  override def toString: String = s"Reviewer $id"
}
