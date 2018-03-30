package de.thewhitespace.pccompanion

/**
  * Represents a paper including its reviewers and declared conflicts
  * @author bhermann
  */
class Paper(val id : Integer, title : String,  var reviewers : Seq[Reviewer], var conflicts : Seq[Reviewer]) {
  override def toString: String = s"$id - $title - reviewed by: $reviewers - conflicts with: $conflicts"

  def addReviewer(r : Reviewer) = {
    reviewers = reviewers ++ Seq(r)
  }

  def addConflict(r : Reviewer) = {
    conflicts = conflicts ++ Seq(r)
  }
}
