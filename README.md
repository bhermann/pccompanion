# PC Companion

A set of useful tools for a PC chair.

The current release is: [v0.4](https://github.com/bhermann/pccompanion/releases/tag/v0.4)

## Seating Arrangement

When conducting a PC meeting in person, two reviewers should not sit next to each other, if a one is a reviewer on a paper and the other one is named as a conflicting reviewer for this paper. 
Using this tool a seating arrangement with minimal conflicts can be computed. 

### Usage

Download three CSV exports from HotCRP:

1. **The list of PC assignments:** In the list of papers, select any paper discussed in the PC meeting, click on *Download*, select *PC assignments*, and click on *Go*.
2. **The list of PC conflicts:** In the list of papers, select any paper discussed in the PC meeting, click on *Download*, select *PC conflicts*, and click on *Go*.
3. **The extended PC info:** In the list of users, select the *All reviewiers* option and click on *Go*. Then select all entries, click on *Download*, select *PC info*, and click on *Go*.

Start the pccompanion executable using the three downloaded files:

```
pccompanion --info /path/to/conf18-pcinfo.csv --assignments /path/to/conf18-pcassignments.csv --conflicts /path/to/conf18-pcconflicts.csv
```

This now starts the seating discovery using a simulated annealing algorithm.
You can adjust the settings for this algorithm with these command line options:

```
--maxgen <value>
   The maximum number of generations (default is 1000000)

--maxnoimprovement <value>
   The maximum number of tolerated generation without improvement (default is 100000)

--sainittemp <value>
   The initial temperature for the simulated annealing algorithm (default is 0.3)

--sacooling <value>
   The cooling for the simulated annealing algorithm (default is 0.98)

--sacoolingschedule <value>
   The cooling schedule for the simulated annealing algorithm (default is 100)
```

### History
The implementation used in this project is based on https://github.com/alexorso/easychairscripts and has been ported to Scala and adapted to HotCRP.
Many authors have contributed to this implementation: 
* Created by Kim Herzig <kim@cs.uni-saarland.de> on 2008-03-14.
* Edited by Andreas Zeller <zeller@cs.uni-saarland.de>.
* Edited by Alex Orso <orso@cc.gatech.edu>.
* Edited by Yue Jia <yue.jia@ucl.ac.uk> and Mark Harman <mark.harman@ucl.ac.uk>
* Edited by Darko Marinov <marinov@illinois.edu>.

Their work is licensed under a Creative Commons Attribution 3.0
Unported License - http://creativecommons.org/licenses/by/3.0/
