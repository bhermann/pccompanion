package de.thewhitespace.pccompanion

import scala.collection.mutable

/**
  * Represents a list of conflicts between reviewers
  * @author bhermann
  */
class Conflicts {
  var conflicts = mutable.ListBuffer[(Reviewer, Reviewer)]()

  def +(conflict: (Reviewer, Reviewer)) = {
    conflicts += conflict
  }

  def isConflict(revA : Reviewer, revB : Reviewer) : Boolean = {
    conflicts.contains((revA, revB)) || conflicts.contains((revB, revA))
  }

  def numberOfConflicts(revA : Reviewer, revB : Reviewer) : Integer = {
    conflicts.filter((x) => (x._1 == revA && x._2 == revB) || (x._2 == revA && x._1 == revB)).size
  }

  def conflicts(revA : Reviewer, revB : Reviewer): Seq[(Reviewer, Reviewer)] = {
    conflicts.filter((x) => (x._1 == revA && x._2 == revB) || (x._2 == revA && x._1 == revB))
  }
}
