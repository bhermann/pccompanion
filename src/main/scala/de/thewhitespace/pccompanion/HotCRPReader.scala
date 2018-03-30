package de.thewhitespace.pccompanion

import java.io.File

import kantan.csv._
import kantan.csv.ops._

import scala.collection.mutable.ListBuffer


/**
  * Reads data from exported HotCRP csv files.
  * @author bhermann
  */
class HotCRPReader {

  def readPCInfo(filename : String) : Seq[HotCRPReviewer] = {
    val file = new File(filename)
    implicit val reviewerDecoder: HeaderDecoder[HotCRPReviewer] = HeaderDecoder.decoder("first", "last", "email")(HotCRPReviewer.apply _)
    val reader = file.readCsv[List,HotCRPReviewer](rfc.withHeader)
    reader.map(x => x.right.get)
  }

  def readPCAssignments(filename : String, pc : Seq[HotCRPReviewer]) : Seq[Paper] = {
    val file = new File(filename)
    implicit val assignmentDecoder : HeaderDecoder[HotCRPPCAssignmentData] = HeaderDecoder.decoder("paper", "action", "email", "round", "title")(HotCRPPCAssignmentData.apply _)
    val reader = file.asCsvReader[HotCRPPCAssignmentData](rfc.withHeader)

    val papers = ListBuffer[Paper]()

    def assignReviewer(paperId : Int, email : String) = {
      val reviewers = pc.filter(_.email.equals(email))
      if (reviewers.isEmpty) println(s"Did not find reviewer with email $email.")
      if (reviewers.size > 1) println(s"Found non unique reviewer entry for email $email. Using the first entry.")

      val paper = papers.filter(_.id.equals(paperId))
      if (paper.isEmpty) println(s"Did not find paper with id $paperId.")
      if (paper.size > 1) println(s"Found non unique paper for id $paperId. Using the first entry.")

      if (reviewers.nonEmpty && paper.nonEmpty) {
        val reviewer = reviewers.head
        val p = paper.head

        p.addReviewer(reviewer)
      }
    }
    reader.foreach(x => {
      x match {
        case Right(HotCRPPCAssignmentData(paperId, "clearreview",_, _, title)) => papers += new Paper(paperId, title, Seq(), Seq())
        case Right(HotCRPPCAssignmentData(paperId, "secondary", email, _, _)) => assignReviewer(paperId, email)
        case Right(HotCRPPCAssignmentData(paperId, "pcreview", email, _, _)) => assignReviewer(paperId, email)
        case Right(x) => println(s"Unknown case found: $x")
        case Left(_) => println("An unknown error occured while reading pc assignments. Is this the correct file?")
      }
    })

    papers
  }

  def readPCConflicts(filename : String, papers : Seq[Paper], pc : Seq[HotCRPReviewer]) : Seq[Paper] = {
    val file = new File(filename)
    implicit val conflictsDecoder : HeaderDecoder[HotCRPPCConflicts] = HeaderDecoder.decoder("paper", "title", "first", "last", "email", "conflicttype")(HotCRPPCConflicts.apply _)
    val reader = file.asCsvReader[HotCRPPCConflicts](rfc.withHeader)

    reader.foreach(x => {
      x match {
        case Right(HotCRPPCConflicts(paperId, _, _, _, email, _)) => {
          val reviewers = pc.filter(_.email.equals(email))
          if (reviewers.isEmpty) println(s"Did not find reviewer with email $email.")
          if (reviewers.size > 1) println(s"Found non unique reviewer entry for email $email. Using the first entry.")

          val paper = papers.filter(_.id.equals(paperId))
          if (paper.isEmpty) println(s"Did not find paper with id $paperId.")
          if (paper.size > 1) println(s"Found non unique paper for id $paperId. Using the first entry.")

          if (reviewers.nonEmpty && paper.nonEmpty) {
            val reviewer = reviewers.head
            val p = paper.head

            p.addConflict(reviewer)
          }
        }
        case Right(x) => println(s"Unknown case found: $x")
        case Left(_) => println("An unknown error occured while reading pc conflicts. Is this the correct file?")
      }
    })

    papers
  }
}

