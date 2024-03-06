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
        // For Testing

//        for (int i=0;i<10;i++){
//            BPlusTree bPlusTree1= new BPlusTree(Constants.ORDERFORBPLUSTREE);
//            linkedListOfBPlusTree.add(bPlusTree1);
//            DataAndRegressionModel dataAndRegressionModel= new DataAndRegressionModel(null, null);
//            dataAndRegressionModelLinkedList.add(dataAndRegressionModel);
//        }
    }
    /*
       Complete Join procedure, It include indexing data structure and learned model
    */
    public void streamInequalityJoinProcessing(int tuple){

        // Perform lookup operation over the existing data structure including mutable Indexing data structure and immutable learned model
        lookupOperation(tuple);
        // Perform insertion operation into
        bPlusTree.insert(tuple, tuple);
        tupleCounter.increment();

//        // Merging from mutable Indexing data Structure

        synchronized (this) {
            if (tupleCounter.getCount() >= Constants.MERGETHRESHOLD) {
                // Initiate the merging process
                mergeFromMutableToLearnedModel();
            }
     }
    }

    /**
     * Lookup operation that include number of threads equal to total number of data structure
     * It perform parallel lookup on all instances of list
     * It utilizes Java Executor service for parallelization
     * @param tuple
     */
    public void lookupOperation(int tuple){
        // Total number of threads that perform join
        int totalSize = linkedListOfBPlusTree.size() + dataAndRegressionModelLinkedList.size() + 1;
       // CountDownLatch latch = new CountDownLatch(totalSize);
        ExecutorService executorService = Executors.newFixedThreadPool(totalSize);
        // Task for current index structure
        executorService.submit(() -> {
            bPlusTree.greaterThenSpecificValue(tuple);
        });

// Tasks for linkedListOfBPlusTree
        for (BPlusTree bPlusTreeInLinkedList : linkedListOfBPlusTree) {
            executorService.submit(() -> {
             // System.out.println("Linked list Thread==="+tupleCounter.getCount());
                bPlusTreeInLinkedList.greaterThenSpecificValue(tuple);

            });
        }

// Tasks for dataAndRegressionModelLinkedList
        for (DataAndRegressionModel model : dataAndRegressionModelLinkedList) {
            executorService.submit(() -> {
                try {
                   // System.out.println("Data and Regression Model======"+tuple);
                    model.searGreaterThanSpecifiedKey(tuple);
                    System.out.println("Data and Regression Model==="+tupleCounter.getCount()+"==="+tuple);
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

//               System.out.println("DataAndRegressionModel Bplus Tree   "+ tupleCounter.getCount());

            });
        }

        try {

            executorService.shutdown();

        } catch (Exception e) {
            e.printStackTrace();
        }

       // executorService.shutdown();
       // System.exit(-1);

    }
    public void shutdownExecutor(ExecutorService executor ) {
        executor.shutdown(); // Shut down the executor after processing
    }

    /**
     * Merging From mutable indexing data structure to learned model
     */
    public synchronized void mergeFromMutableToLearnedModel(){
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
                executorService.shutdown();
                //System.out.println("Task completed: " + immutableLearnedIndex);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Shutdown the ExecutorService after use
        if(mergeStart==true) {
            executorService.shutdown();
            System.out.println("Merged Completed");
            mergeStart=false;
        }
    }

}
