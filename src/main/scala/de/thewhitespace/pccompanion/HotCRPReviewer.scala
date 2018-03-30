package de.thewhitespace.pccompanion

/**
  * Represents reviewers from HotCRP
  * @author bhermann
  */
case class HotCRPReviewer(first : String, last : String, email : String) extends Reviewer(ReviewSequence.consume()) {
  override def toString = s"${f"${id}%02d"} - $first $last <$email>"
}
