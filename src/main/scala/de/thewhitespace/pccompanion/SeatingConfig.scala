package de.thewhitespace.pccompanion

/**
  * Internal representation of the configuration of the seating arrangement algorithm.
  * @author bhermann
  */
case class SeatingConfig(pcinfoFile : String = "",
                         pcassignmentFile : String = "",
                         pcconflictFile : String = "",
                         maxGen : Int = 1000000,
                         maxNoImprovements: Int = 100000,
                         saInitTemp : Double = 0.3,
                         saCooling : Double = 0.98,
                         saCoolingSchedule : Int = 100) {
}
