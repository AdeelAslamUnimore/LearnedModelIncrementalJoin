package com.streamjoin.bplusandlearnedmodel;

import com.conventionalindexes.bplustree.BPlusTree;
import com.join.constants.Constants;
import com.learnedmodel.learnedindex.DataAndRegressionModel;
import com.learnedmodel.learnedindex.MergingToLearnedModel;

import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Business Logic include insertion and searching for keys:
 * Background merging from mutable to immutable keys
 * Consider the incremental sliding window size
 * Merge the BPlus tree mutable structure to immutable learned model depends on threshold
 */
public class StreamSelfJoin {
   // Tuple counter
    private int tupleCounter;
    // initilize the LinkedListBPlusTree
    private LinkedList<BPlusTree> linkedListOfBPlusTree;
    // Initilize the list of data and Regression model for inference with learned Model
    private LinkedList<DataAndRegressionModel> dataAndRegressionModelLinkedList;
    /*
    Constructor to initialize the tuple counter
     */
    public StreamSelfJoin(){
        this.tupleCounter=0;
        // LinkedList for mutable indexing data structure
        this.linkedListOfBPlusTree= new LinkedList<>();
        // Linkedlist for immutable data structure (Immutable tree)
        this.dataAndRegressionModelLinkedList= new LinkedList<>();
        // Initialize the BPlusTree
        BPlusTree bPlusTree= new BPlusTree(Constants.ORDERFORBPLUSTREE);
        this.linkedListOfBPlusTree.add(bPlusTree);
    }
    /*
       Complete Join procedure, It include indexing data structure and learned model
    */
    public void streamInequalityJoinProcessing(int tuple){
        BPlusTree bPlusTree= this.linkedListOfBPlusTree.getLast();
        bPlusTree.insert(tuple, tuple);
        tupleCounter++;
        // Create a search logic it should be parallel
        // Method for searching
        // Write a method that perform searching


        // Merging from mutable Indexing data Structure
        if(tupleCounter>=Constants.MERGETHRESHOLD){
            // Size of Tuples for Computing the Emperical CDF
            int sizeOfTuples=tupleCounter;
            // Re initialize the tuple counter.
            tupleCounter=0;
            // New BPlusTree in the linked list for new insertion in mutable index data structure
            this.linkedListOfBPlusTree.add(new BPlusTree(Constants.ORDERFORBPLUSTREE));
            // Create an ExecutorService with a single thread
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            // Define a task to be executed asynchronously
            Callable<DataAndRegressionModel> task = () -> {
                // Call your method here
                return new MergingToLearnedModel().empericalOptimizedCDF( this.linkedListOfBPlusTree.get(linkedListOfBPlusTree.size()-2),sizeOfTuples);
            };
            // Submit the task for execution
            Future<DataAndRegressionModel> future = executorService.submit(task);
            //
            try {
                // Wait for the task to complete and get the result
                DataAndRegressionModel immutableLearnedIndex = future.get();
                // Do something with the result if needed
                this.dataAndRegressionModelLinkedList.add(immutableLearnedIndex);
                //System.out.println("Task completed: " + immutableLearnedIndex);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Shutdown the ExecutorService after use
            executorService.shutdown();
        }


    }

}
