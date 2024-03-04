import com.conventionalindexes.bplustree.BPlusTree;
import com.learnedmodel.learnedindex.MergingToLearnedModel;
import org.apache.commons.math3.random.EmpiricalDistribution;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test {
    public static void main(String[] args) {
        ExecutorService mergingExecutor = Executors.newSingleThreadExecutor();

        // Define a task for background merging
        Runnable mergingTask = () -> {
            // Simulate merging operation
            System.out.println("Background merging started..."+System.currentTimeMillis());
            try {
                // Simulate merging process
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Background merging completed."+System.currentTimeMillis());
        };

        // Execute the merging task using the ExecutorService
        mergingExecutor.execute(mergingTask);

        // Perform some other operation
        for(int i=0;i<100;i++) {
            System.out.println("Performing some other operation while merging is happening..."+System.currentTimeMillis());
        }
        // Continue with other operations
        // For demonstration, we'll just sleep for a while
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Shutdown the ExecutorService after use
        mergingExecutor.shutdown();
    }

    }