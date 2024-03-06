package com.learnedmodel.learnedindex;

import com.conventionalindexes.bplustree.BPlusTree;
import com.conventionalindexes.bplustree.Key;
import com.conventionalindexes.bplustree.Node;
import org.apache.commons.math3.random.EmpiricalDistribution;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
Merging is performed from the leaf node of the BPlusTree and create a learned model
Using Math.common fist compute the CDF and then train the model
 */
public class MergingToLearnedModel {
    /**
     Emperical CDF computation using optimized method call.
     Train the model while iterating the leaf nodes of BPlusTree.
     The return of the method is the Data and Regression Model.
 */
    public synchronized DataAndRegressionModel empericalOptimizedCDF(BPlusTree bPlusTree, int size){
        // Get the leftmost node of the B+ tree
        Node node = bPlusTree.leftMostNode();

        // Initialize a SimpleRegression object to calculate linear regression
        SimpleRegression regression = new SimpleRegression();

        // Index for maintaining the data array [Key: [Values]]
        int index = 0;

        // Initialize cumulative distribution function (cdf) to 0.0


    // Array to store all keys
    Key[] keys = new Key[size];

    // Iterate through the nodes of the B+ tree
        while (node != null) {
        // Iterate through the keys of the current node
        for (int i = 0; i < node.getKeys().size(); i++) {
            if(node.getKeys().get(i).getValues().size() > 1) {
                // Increment cdf and add data to regression for each repeating value
                int repeatingSize=node.getKeys().get(i).getValues().size();
                double cdf = (double) (index + repeatingSize) / size;
                 System.out.println(cdf);
                for (int j = 0; j < node.getKeys().get(i).getValues().size(); j++) {
                   // cdf = cdf + increment;
                    regression.addData(node.getKeys().get(i).getKey(), cdf);
                    keys[index] = new Key(node.getKeys().get(i).getKey(),node.getKeys().get(i).getValues().get(j));
                    index++;
                }
                // Store the key

            } else {
                // Add single value to the regression model
                 double cdf = (double) (index + 1) / size;
                 System.out.println(cdf);
                regression.addData(node.getKeys().get(i).getKey(), cdf);
                // Store the key
                keys[index] =new Key(node.getKeys().get(i).getKey(),node.getKeys().get(i).getValues().get(0));
                index++;
            }
        }
        // Move to the next node
        node = node.getNext();
    }

    // Construct DataAndRegressionModel object
    DataAndRegressionModel dataAndRegressionModel = new DataAndRegressionModel(keys, regression);
        return dataAndRegressionModel;
}


}

