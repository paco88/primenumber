## Requirements

You are to create two small applications for this programming task; one is called Randomizer, the other Prime.  Randomizer‘s job is to generate a series of positive random integers and send those to Prime via a distributed queue of integers.

Primes job is to receive the integers and calculate whether the integer is a prime or not and return the answer to Randomizer via a distributed queue that contains the original number and a Boolean; which Randomizer will print to system out.

Some points

- Use only the standard java library
- Both Applications will run on the same server
- The system should be as fast as possible
- The results do not have to be returned in the same order as received


## Implementation

The program `Randomizer` spawns a thread to generate random numbers and put them in a queue.  It also opens a server port and listens to connections from `Prime`.  Once a client is connected, it spawn 2 threads, one sends numbers from the queue to the client and another reads results from client and print them on stdout.

`Prime` opens connection to `Randomizer`, reads numbers from the port and put them in a queue.  It then spawns multiple worker threads (specified in command line), each thread takes a number from the queue, calls the checkPrime() function and put the result with the number to the result queue.  A separate thread reads the result queue and send them back to the server.


## Run Randomizer

`./run server`

## Run Prime
You can specify number of worker threads as an argument, the default is 1.
You can run multiple clients.

`./run client [num_of_threads]`

