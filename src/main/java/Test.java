import com.conventionalindexes.bplustree.BPlusTree;
import com.join.inputtuples.Counter;
import com.join.inputtuples.StreamGenerator;
import com.join.inputtuples.Tuple;
import com.learnedmodel.learnedindex.DataAndRegressionModel;
import com.learnedmodel.learnedindex.MergingToLearnedModel;
import org.apache.commons.math3.random.EmpiricalDistribution;

import java.util.concurrent.*;

public class Test {
    public static void main(String[] args) {
        StreamGenerator streamGenerator= new StreamGenerator();
        streamGenerator.inputTupleGeneration();

    }

    public void testCDFModel(){
        BPlusTree bPlusTree= new BPlusTree(4);
        bPlusTree.insert(10, 18);
        bPlusTree.insert(20, 25);
        bPlusTree.insert(30, 35);
        bPlusTree.insert(40,45);
        bPlusTree.insert(50, 18);
        bPlusTree.insert(60, 25);
        bPlusTree.insert(70, 35);
        bPlusTree.insert(80,45);
        DataAndRegressionModel merging= new MergingToLearnedModel().empericalOptimizedCDF( bPlusTree,8);
        merging.searGreaterThanSpecifiedKey(70);


    }
}