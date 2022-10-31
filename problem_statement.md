CS 520: Introduction to Operating Systems
Homework Assignment #3
This assignment is somewhat open-ended—start working on it as soon as you can!
A reminder: You may work in groups; however, you may not show anyone your
code or copy of any part of anyone else’s code or report.
Those who submit isomorphic programs or same reports, get 0 points each as a
minimum. (For the rest of the penalties please check the Syllabus.)
I recommend that you write everything in small—no longer than one-page-long—
methods and test each method separately. Start by testing your pseudo-random
variable package and check the distributions to ensure that everything works.
Include this test in a separate method.
Then prepare and test the event data structure. After that, writing and debugging the rest
of the program will be straight-forward. I recommend that you finish the program in
the first week and spend the second week on experimenting and writing the report.
As a minimum, a program must be written and a report prepared with the specific
questions answered, but there is much room for using your creativity—please use it! The
most important thing that you will have learned from this assignment is the discreet
simulation technique, which you will apply (by re-using most of the code you develop to
deal with this particular problem) later.
Write the bus simulation, as explained in the class and described in the notes. Feel free to
look on the web for and then re-use any suitable code for the random number generator or
for linked list algorithms. With that, 1) don’t forget to include a reference to the source
of any code you re-use and 3) remember to test all re-used code as you are the only one
responsible for its performance..
The purpose of the simulation is to observe the behavior of the system, and answer the
following questions:
1. Does the distance between the adjacent buses remain the same? If not, what
should be done to ensure that it be the same?
2. What is the average size of a waiting queue at each stop (and what are its
maximum and minimum)? (You may provide this information on an hourly [simulation
time] base.)
Plot the positions of buses as a function of time (you will need to generate periodic
snapshots of the system for that). Feel free to change parameters; then observe and
document the results.

You must submit a zip file that includes

2

1. Your working Java program (it must be well-commented) as well as all required
input files in the txt format.
2. A README txt file explaining what you are submitting.
3. Your report, in the PDF format, including plots, observations, and—especially—
your recommendations for change in the scheduling policies. Needless, to say you
will need runs that demonstrate that your recommendations solve the problems
you observe. Remember that preparing a report is a very essential part of this
assignment; this is not merely a programming exercise. The program must
be working (or the assignment will get a grade of 0), but the quality of your
report will determine your grade.

Feel free to change the simulation parameters. My initial suggestion is that there are

• 15 bus stops
• 5 buses
• The time to drive between any two contiguous stops is 5 minutes
• The passenger’s mean arrival rate at each stop is 5 persons/min
• The boarding time is 2 seconds for each passenger
• The total simulation time is 8 hours.

Make sure you start this project as early as possible so that you have enough time to run
experiments and prepare a comprehensive report. (100 points)