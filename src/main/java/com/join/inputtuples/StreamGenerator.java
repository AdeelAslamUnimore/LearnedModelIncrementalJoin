package com.join.inputtuples;

import com.streamjoin.bplusandlearnedmodel.StreamSelfJoin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

public class StreamGenerator {
    final PriorityBlockingQueue<Tuple> priorityQueue;
    final Counter counter;
    final StreamSelfJoin streamSelfJoin;


    public StreamGenerator() {
        // Create a shared queue (LinkedList is not thread-safe, so we wrap it with synchronizedList)
        this.priorityQueue = new PriorityBlockingQueue<>();
        // Counter variable to count the number of tuples
         this.counter= new Counter();
         // Stream self join object that contains windowing logic
        this.streamSelfJoin= new StreamSelfJoin();
    }

    public void inputTupleGeneration(){
        // Create a fixed-size thread pool with multiple threads
        ExecutorService executor = Executors.newFixedThreadPool(1000);

        // Define a task for background processing
        Runnable dataCalculator = () -> {
            tupleGeneration();
        };
        /**
         * Adjust the threads for creating data
         */
        // Initiate the number of threads it should be according to the predefined threshold from executor service
        for (int i = 0; i < 500; i++) {
            executor.submit(dataCalculator);
        }

        // Initiate the number of threads it should be according to the predefined threshold from executor service
        Runnable insertion = () -> {
            tupleToProposedJoinModel();
        };
        /**
         * Adjust the threads for reading data from the shared queue
         */
        // many instances compute the join process in a parallel
        for (int i = 0; i < 500; i++) {
            executor.submit(insertion);
        }


        // Wait for all tasks to complete
         try {
             executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            // if(counter.getCount()==1000) {
                 executor.shutdown();
           //  }
         } catch (InterruptedException e) {
             e.printStackTrace();
         }
    }

    /**
     * Generated Tuples are inserted into the model for predicate evaluation
     * Continually read from the queue head
     * Acts as a consumer from the queue
     */
    public void tupleToProposedJoinModel(){
        //if(counter.getCount()!=1000) {
        while(true) {
            try {
                if (!priorityQueue.isEmpty()) {
                    long threadId = Thread.currentThread().getId();
                   // System.out.println(System.currentTimeMillis()+"Reading into Queue Number===="+priorityQueue.poll().getTuple()+"Thread ID=="+threadId+"   TimeStamp==="+priorityQueue.poll().getTimeStamp());
                    streamSelfJoin.streamInequalityJoinProcessing(priorityQueue.poll().getTuple());
                }

                // Simulate a delay
                Thread.sleep(5);
                // tupleToProposedJoinModel();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //}
    }
    /**
     * Tuple generation by many threads
     * Continually insert the tuples into the priority queue
     * Acts as a producer for the tuple
     */
    public void tupleGeneration(){
        while(true) {
            // Simulate some processing
            int randomNumber = (int) (Math.random() * 100);
            try {
                // Simulate a delay
                Thread.sleep(5);
                // Add the result to the shared queue
                long threadId = Thread.currentThread().getId();
                priorityQueue.offer(new Tuple(randomNumber, System.currentTimeMillis()));

               // System.out.println(System.currentTimeMillis()+"Insertion into Queue Number==="+randomNumber+"Thread ID=="+threadId);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }


}
