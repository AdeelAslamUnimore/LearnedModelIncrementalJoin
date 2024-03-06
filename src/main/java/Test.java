import com.conventionalindexes.bplustree.BPlusTree;
import com.join.inputtuples.Counter;
import com.join.inputtuples.StreamGenerator;
import com.join.inputtuples.Tuple;
import com.learnedmodel.learnedindex.DataAndRegressionModel;
import com.learnedmodel.learnedindex.MergingToLearnedModel;
import org.apache.commons.math3.random.EmpiricalDistribution;

import java.util.Arrays;
import java.util.concurrent.*;

public class Test {
    public static void main(String[] args) {
//        StreamGenerator streamGenerator= new StreamGenerator();
//        streamGenerator.inputTupleGeneration();
        new Test().testCDFModel();

    }

    public void testCDFModel(){
        double[] data = {1.2, 2.5, 3.6, 4.8, 5.1, 6.3, 7.2, 5.1, 9.7, 10.2, 4.0, 12.5, 45.6, 69.0, 24.5, 44.5};

        // Sort the data in ascending order
        Arrays.sort(data);

        int totalPoints = data.length;

        // Initialize the incremental CDF array
        double[] incrementalCDF = new double[totalPoints];

        // Compute the incremental CDF for each data point
        double prevValue = Double.NaN;
        double prevCDF = 0.0;
        for (int i = 0; i < totalPoints; i++) {
            double value = data[i];
            if (value != prevValue) {
                // Only increment CDF if the value is different from the previous one
                incrementalCDF[i] = (double) (i + 1) / totalPoints;
                prevCDF = incrementalCDF[i];
            } else {
                // Use the previous CDF value for repeating values
                incrementalCDF[i] = prevCDF;
            }
            prevValue = value;
        }

        // Print the incremental CDF values
        System.out.println("Incremental CDF:");
        for (int i = 0; i < totalPoints; i++) {
            System.out.println(data[i] + ": " + incrementalCDF[i]);
        }
    }
}