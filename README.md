# Dynamic Scheduler

A greedy algorithm in Java that solves the interval scheduling problem for cancellable requests that are received throughout the course of the day.
The algorithm uses a Priority Queue to prevent the need to resort the requests based on finish times every time a new request is added or canceled. 
Rather than using Java's built-in Priority Queue(based on minimum heaps) that performs peek, poll, and add in O(logn) time, 
the algorithm uses a custom-built Priority Queue built with static arrays to perform the same operations in constant time.  

Have a quick look: [Demo Video](https://aryanshah701.github.io/show-scheduler.html)

### Getting Started

Follow the steps below to get started and run the algorithm.  

Once the repo has been cloned onto your local machine, use the command line to navigate to the src directory and run the following block of code to compile the java file.

```
javac ConstantPQ.java
```

Once the java file has been compiled, you can run the program using the `java` keyword and then use input redirection with `<` to supply the program one of the sample tests as follows

```
java ConstantPQ < ../sample-tests/millionRequests.txt
```

You can also use output redirection with `>` to redirect the output from stdout to a file as shown below.

```
java ConstantPQ < ../sample-tests/millionRequests.txt > millionRequestsOutput.txt
```

### Request .txt files  

The program can parse requests in textual files that follow the conventions listed below.

1. Requests to the resource must be of the form \<start-time\>:\<end-time\>
2. To cancel a request, a line of the form cancel \<start-time\>:\<end-time\> must be used
3. To model the passage of time during the day, a single 24-hour time must be used

When the program comes across a 24-hour time, it schedules all requests that begin prior to that 24-hour time to simulate the passing of time until that given time.  

Refer to the sample request files in the sample-tests directory to get a better understanding of the input.

## Built With

* [Java](https://docs.oracle.com/en/java/)

## Acknowledgments

* [Kevin Gold](kgold@ccs.neu.edu) - *Sample Tests and Request class*

