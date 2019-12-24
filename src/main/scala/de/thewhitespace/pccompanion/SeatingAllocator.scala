package de.thewhitespace.pccompanion

import scala.util.Random

/**
  * An implementation of a seating arragement allocator.
  * Based on the simulated annealing algorithm written in Python from
  * https://github.com/alexorso/easychairscripts
  *
  * Created by Kim Herzig <kim@cs.uni-saarland.de> on 2008-03-14.
  * Edited by Andreas Zeller <zeller@cs.uni-saarland.de>.
  * Edited by Alex Orso <orso@cc.gatech.edu>.
  * Edited by Yue Jia <yue.jia@ucl.ac.uk> and Mark Harman <mark.harman@ucl.ac.uk>
  * Edited by Darko Marinov <marinov@illinois.edu>.
  * Adapted to HotCRP and Scala by Ben Hermann <ben.hermann@uni-paderborn.de>.
  *
  * @author bhermann
  */
class SeatingAllocator {

  def findConflicts(papers: Seq[Paper]): Conflicts = {
    val result = new Conflicts

    papers.foreach(p => {
      for {
        r <- p.reviewers
        c <- p.conflicts
      } {
        result + (r, c)
      }
    })

    result
  }

  def searchSeating(config: SeatingConfig, reviewers: Seq[Reviewer], papers: Seq[Paper]) : SeatingResult = {

    // construct conflicts
    val conflicts: Conflicts = findConflicts(papers)

    var seating_plan = reviewers

    val max_gen = config.maxGen
    val max_no_improvement = config.maxNoImprovements
    val SearchSAInitTemp = config.saInitTemp
    val SearchSACooling = config.saCooling
    val SearchSACoolingSchedule = config.saCoolingSchedule

    var curr_gen = 0
    var no_improvement = 0
    var bad_moves = 0
    var t = SearchSAInitTemp


    while (curr_gen < max_gen) {
      println(s"Gen $curr_gen has ${fitness(seating_plan, conflicts)} seating conflicts.")
      val new_seating_plan = Random.shuffle(seating_plan)
      val curr_fitness = fitness(seating_plan, conflicts)
      val new_fitness = fitness(new_seating_plan, conflicts)

      if (Math.exp(-(new_fitness - curr_fitness) / t) > new Random().nextDouble()) {
        if (curr_fitness == new_fitness) {
          no_improvement += 1
        } else {
          no_improvement = 0
          if (curr_fitness < new_fitness) {
            bad_moves += 1
          }
        }
        seating_plan = new_seating_plan
      } else {
        no_improvement += 1
      }



      if (fitness(seating_plan, conflicts) == 0) {
        println(s"Found solution at Gen $curr_gen | No. of seating conflicts: ${fitness(seating_plan, conflicts)}")
        return new SeatingResult(seating_plan, remainingConflicts(seating_plan, conflicts))
      }

      if (no_improvement > max_no_improvement) {
        println(s"Failed! current solution at Gen $curr_gen | No. of seating conflicts: ${fitness(seating_plan, conflicts)}")
        println("Generated suboptimal seating assignment")
        return new SeatingResult(seating_plan, remainingConflicts(seating_plan, conflicts))
      }

      if (curr_gen % SearchSACoolingSchedule == 0)  t = t * SearchSACooling
      curr_gen += 1
    }

    println(s"Exceeding max_gen | No. of seating conflicts: ${fitness(seating_plan, conflicts)}")
    new SeatingResult(seating_plan, remainingConflicts(seating_plan, conflicts))
  }

  def fitness(seatingPlan: Seq[Reviewer], seatingConflicts: Conflicts): Integer = {
    seatingPlan.zip(seatingPlan.drop(1)).map(x => seatingConflicts.numberOfConflicts(x._1, x._2)).foldLeft(0)(_ + _)
  }

  def remainingConflicts(seatingPlan: Seq[Reviewer], seatingConflicts: Conflicts) : Seq[(Reviewer, Reviewer)] = {
    seatingPlan.zip(seatingPlan.drop(1)).flatMap(x => seatingConflicts.conflicts(x._1, x._2))
  }

}

object SeatingAllocator extends SeatingAllocator {

  val cliParser = {
    new scopt.OptionParser[SeatingConfig]("pccompanion") {
      head("Seating Arragement Constructor", s"(v${BuildInfo.version})")


      version("version").text("Prints the version of the command line tool.")

      help("help").text("Prints this help text.")
      override def showUsageOnError = true

      opt[String]("info").required().action((x,c) => c.copy(pcinfoFile = x)).text("The path to the pcinfo CSV file from HotCRP. (required)")
      opt[String]("assignments").required().action((x,c) => c.copy(pcassignmentFile = x)).text("The path to the pcassignments CSV file from HotCRP. (required)")
      opt[String]("conflicts").required().action((x,c) => c.copy(pcconflictFile = x)).text("The path to the pcconficts CSV file from HotCRP. (required)")

      opt[Int]("maxgen").action((x,c) => c.copy(maxGen = x)).text(s"The maximum number of generations (default is ${SeatingConfig().maxGen})")
      opt[Int]("maxnoimprovement").action((x,c) => c.copy(maxNoImprovements = x)).text(s"The maximum number of tolerated generation without improvement (default is ${SeatingConfig().maxNoImprovements})")
      opt[Double]("sainittemp").action((x,c) => c.copy(saInitTemp = x)).text(s"The initial temperature for the simulated annealing algorithm (default is ${SeatingConfig().saInitTemp})")
      opt[Double]("sacooling").action((x,c) => c.copy(saCooling = x)).text(s"The cooling for the simulated annealing algorithm (default is ${SeatingConfig().saCooling})")
      opt[Int]("sacoolingschedule").action((x,c) => c.copy(saCoolingSchedule = x)).text(s"The cooling schedule for the simulated annealing algorithm (default is ${SeatingConfig().saCoolingSchedule})")
    }
  }

  def main(args: Array[String]): Unit = {
    cliParser.parse(args, SeatingConfig()) match {
      case Some(config) => {
        cliParser.showHeader()
        performSearch(config)
      }
      case None => {}
    }

  }

  def performSearch(config: SeatingConfig): Unit = {
    val reader = new HotCRPReader()
    val pc = reader.readPCInfo(config.pcinfoFile)
    val papers = reader.readPCAssignments(config.pcassignmentFile, pc)
    val papersWithConflicts = reader.readPCConflicts(config.pcconflictFile, papers, pc)

    val result = searchSeating(config, pc, papers)

    println()
    println("The found seating arrangement is:")
    println(result.seatingPlan.mkString(",\n"))

    println()
    println("The remaining conflicts are:")
    println(result.remainingConflicts.mkString(",\n"))
  }


}
