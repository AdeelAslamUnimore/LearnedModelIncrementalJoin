package com.streamjoin.bplusandlearnedmodel;

import com.conventionalindexes.bplustree.BPlusTree;
import com.join.constants.Constants;
import com.join.inputtuples.Counter;
import com.learnedmodel.learnedindex.DataAndRegressionModel;
import com.learnedmodel.learnedindex.MergingToLearnedModel;

import java.util.LinkedList;
import java.util.concurrent.*;

/**
 * Business Logic include insertion and searching for keys:
 * Background merging from mutable to immutable keys
 * Consider the incremental sliding window size
 * Merge the BPlus tree mutable structure to immutable learned model depends on threshold
 */
public class StreamSelfJoin {
   // Tuple counter
    private Counter tupleCounter;
    // initilize the LinkedListBPlusTree
    private final LinkedList<BPlusTree> linkedListOfBPlusTree;
    // Initilize the list of data and Regression model for inference with learned Model
    private final LinkedList<DataAndRegressionModel> dataAndRegressionModelLinkedList;
    // ExecutorService or parallel execution for lookup operation both Index structure and Learned Model
    private ExecutorService executorService;
    // BPlus tree for insertion
    private BPlusTree bPlusTree;
    /*
    Constructor to initialize the tuple counter
     */

    public StreamSelfJoin(){
        this.tupleCounter=new Counter();
        // LinkedList for mutable indexing data structure
        this.linkedListOfBPlusTree= new LinkedList<>();
        // Linked list for immutable data structure (Immutable tree)
        this.dataAndRegressionModelLinkedList= new LinkedList<>();
        // Initialize the BPlusTree
        bPlusTree= new BPlusTree(Constants.ORDERFORBPLUSTREE);
        this.linkedListOfBPlusTree.add(bPlusTree);
    }
    /*
       Complete Join procedure, It include indexing data structure and learned model
    */
    public void streamInequalityJoinProcessing(int tuple){
        // BPlus tree insertion

        bPlusTree.insert(tuple, tuple);
        tupleCounter.increment();

        //Initiate a
            lookupOperation(tuple);

        // Merging from mutable Indexing data Structure

            if (tupleCounter.getCount() >= Constants.MERGETHRESHOLD) {
                // Initiate the merging process
                mergeFromMutableToLearnedModel();
            }
        }



    /**
     * Lookup operation that include number of threads equal to total number of data structure
     * It perform parallel lookup on all instances of list
     * It utilizes Java Executor service for parallelization
     * @param tuple
     */
    public void lookupOperation(int tuple){
        // Size is total instance of data structures both mutable and immutable during merge
              int totalSize = linkedListOfBPlusTree.size() +( dataAndRegressionModelLinkedList.size()+1);
        CountDownLatch latch = new CountDownLatch(totalSize); // Create a countdown latch with the total number of tasks

        ExecutorService executorService = Executors.newFixedThreadPool(totalSize); // Create a fixed thread pool depending on the total size

// Current index structure
        executorService.submit(() -> {
            // Ensure thread-safe access to bPlusTree
            synchronized (bPlusTree) {
                // Access bPlusTree safely
                bPlusTree.greaterThenSpecificValue(tuple);
            }
            latch.countDown(); // Signal that this task has completed
        });

// Parallel execution of indexing data structure
        for (BPlusTree bPlusTreeInLinkedList : linkedListOfBPlusTree) {
            executorService.submit(() -> {
                // Ensure thread-safe access to bPlusTree
                synchronized (bPlusTreeInLinkedList) {
                    // Access bPlusTree safely
                    bPlusTreeInLinkedList.greaterThenSpecificValue(tuple);
                }
                latch.countDown(); // Signal that this task has completed
            });
        }

// Process DataAndRegressionModel linked list
        for (DataAndRegressionModel model : dataAndRegressionModelLinkedList) {
            executorService.submit(() -> {
                // Ensure thread-safe access to model
                synchronized (model) {
                    // Access model safely
                    model.searGreaterThanSpecifiedKey(tuple);
                }
                latch.countDown(); // Signal that this task has completed
            });
        }

        try {
            latch.await(); // Wait until all tasks have completed
            executorService.shutdown(); // Shut down the executor service after all tasks are done
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
    public void shutdownExecutor(ExecutorService executor ) {
        executor.shutdown(); // Shut down the executor after processing
    }

    /**
     * Merging From mutable indexing data structure to learned model
     */
    public void mergeFromMutableToLearnedModel(){
        // Size of Tuples for Computing the Emperical CDF
        int sizeOfTuples=tupleCounter.getCount();
        // Re initialize the tuple counter.
        tupleCounter.setCount(0);
        // New BPlusTree in the linked list for new insertion in mutable index data structure
        this.linkedListOfBPlusTree.add(bPlusTree);
        // BPlus tree reinitilize it
        bPlusTree= new BPlusTree(Constants.ORDERFORBPLUSTREE);
        boolean mergeStart=false;
        // Create an ExecutorService with a single thread
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        // Define a task to be executed asynchronously
        Callable<DataAndRegressionModel> task = () -> {
            // Call your method here
            return new MergingToLearnedModel().empericalOptimizedCDF( this.linkedListOfBPlusTree.getLast(),sizeOfTuples);
        };
        // Submit the task for execution
        Future<DataAndRegressionModel> future = executorService.submit(task);
        //
        synchronized (dataAndRegressionModelLinkedList) {
            try {
                // Wait for the task to complete and get the result
                DataAndRegressionModel immutableLearnedIndex = future.get();
                // Do something with the result if needed

                this.dataAndRegressionModelLinkedList.add(immutableLearnedIndex);
                linkedListOfBPlusTree.removeLast();
                mergeStart=true;
                //System.out.println("Task completed: " + immutableLearnedIndex);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Shutdown the ExecutorService after use
        if(mergeStart==true) {
            executorService.shutdown();
        }
    }

}
