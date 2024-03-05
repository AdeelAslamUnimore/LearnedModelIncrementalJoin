import com.conventionalindexes.bplustree.BPlusTree;
import com.join.inputtuples.Counter;
import com.join.inputtuples.Tuple;
import com.learnedmodel.learnedindex.MergingToLearnedModel;
import org.apache.commons.math3.random.EmpiricalDistribution;

import java.util.concurrent.*;

public class Test {
       final PriorityBlockingQueue<Tuple> priorityQueue;
       final Counter counter;

     public Test() {
         // Create a shared queue (LinkedList is not thread-safe, so we wrap it with synchronizedList)
         priorityQueue = new PriorityBlockingQueue<>();
         // Counter variable to count the number of tuples
         counter= new Counter();
        }

    public void inputTupleGeneration(){
         // Create a fixed-size thread pool with multiple threads
         ExecutorService executor = Executors.newFixedThreadPool(8);

         // Define a task for background processing
         Runnable dataCalculator = () -> {
             tupleGeneration();
         };

         // Submit multiple instances of the background task to the executor 4 is the number of thread
         for (int i = 0; i < 4; i++) {
             executor.submit(dataCalculator);
         }

         // Poll elements from the shared queue while continuing with other operations
         Runnable insertion = () -> {
            tupleToProposedJoinModel();
         };
        // many instances compute the join process in a parallel
        for (int i = 0; i < 4; i++) {
            executor.submit(insertion);
        }


         // Wait for all tasks to complete
         try {
             executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
             if(counter.getCount()==1000) {
                 executor.shutdown();
             }
         } catch (InterruptedException e) {
             e.printStackTrace();
         }
     }

    /**
     * Generated Tuples are inserted into the model for predicate evaluation
     * Use recursion in the same thread to insert the tuple from priority queue to the learned model
     * Get out from thread when the counter value is achieved
     */
    public void tupleToProposedJoinModel(){


        //if(counter.getCount()!=1000) {
        while(counter.getCount() < 1000) {
            try {
                while (!priorityQueue.isEmpty()) {
                    System.out.println(priorityQueue.poll().getTuple());
                }
                // Simulate a delay
                Thread.sleep(100);
                // tupleToProposedJoinModel();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //}
     }
    /**
     * Tuple generation by many threads
     */
    public void tupleGeneration(){
        for(;counter.getCount() < 1000; counter.increment()) {
            // Simulate some processing
            int randomNumber = (int) (Math.random() * 100);
            try {
                // Simulate a delay
                Thread.sleep(randomNumber);
                // Add the result to the shared queue
                priorityQueue.offer(new Tuple(randomNumber, System.currentTimeMillis()));
                // counter.increment();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
}