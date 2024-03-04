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
    /*
        compute the CDF
        @param BPlusTree where leaf nodes are sorted from mutable tree
     */

    /**
     * Computes the empirical cumulative distribution function (CDF) for values stored in the given B+ tree.
     * Constructs a DataAndRegressionModel object containing the computed CDF and a Simple Regression model.
     *
     * @param bPlusTree The B+ tree containing the values
     * @param size      The size of the CDF array
     * @return A DataAndRegressionModel object containing the computed CDF and Simple Regression model
     */
    public DataAndRegressionModel empericalCDF(BPlusTree bPlusTree, int size) {
        double[] cdf = new double[size];
        Key[] keys = new Key[size];
        double[] values = new double[size];
        int index = 0;
        int keyIndex = 0;
        Node node = bPlusTree.leftMostNode();

        // Collect all values and keys from the B+ tree
        while (node != null) {
            for (int i = 0; i < node.getKeys().size(); i++) {
                for (String value : node.getKeys().get(i).getValues()) {
                    values[index] = node.getKeys().get(i).getKey();
                    index++;
                  //  double cumulativeProbability = Probability.normal(-Double.MAX_VALUE, Double.MAX_VALUE, value, true);
                }
                keys[keyIndex] = node.getKeys().get(i);
                keyIndex++;
            }
            node = node.getNext();
        }

        // Load values into the empirical distribution
        EmpiricalDistribution empiricalDistribution = new EmpiricalDistribution();
        empiricalDistribution.load(values);

        // Compute CDF and train Simple Regression model
        SimpleRegression regression = new SimpleRegression();
        for (int i = 0; i < values.length; i++) {
            cdf[i] = empiricalDistribution.cumulativeProbability(values[i]);
            System.out.println(values[i]+"=="+cdf[i]);
            regression.addData(values[i], cdf[i]);
        }

        // Construct and return DataAndRegressionModel object
        DataAndRegressionModel dataAndRegressionModel = new DataAndRegressionModel(keys, regression);
        return dataAndRegressionModel;
    }
    /*
    Emperical CDF computation using with optimized method call

     */
    public SimpleRegression empericalOptimizedCDF(BPlusTree bPlusTree, int size){
        // Get the leftmost node of the B+ tree
        Node node = bPlusTree.leftMostNode();

        // Initialize a SimpleRegression object to calculate linear regression
        SimpleRegression regression = new SimpleRegression();

        // Flag to indicate if it's the first index
        int  index = 0;

        // Calculate the increment for cdf
        double increment = 1.0 / size;

        // Initialize cumulative distribution function (cdf) to 0.0
        double cdf = 0.0;
        // All Keys
        Key[] keys= new Key[size];

        // Iterate through the nodes of the B+ tree
        while (node != null){
            // Iterate through the keys of the current node
            for (int i = 0; i < node.getKeys().size(); i++) {
                    if(node.getKeys().get(i).getValues().size() > 1){
                        // Increment cdf and add data to regression for each repeating value
                        double cdfValueForRepeated=cdf + increment;
                        for(int j = 0; j < node.getKeys().get(i).getValues().size(); j++){
                            cdf = cdf + increment;
                            regression.addData(node.getKeys().get(i).getKey(), cdfValueForRepeated);
                            System.out.println(node.getKeys().get(i).getKey()+"==="+cdfValueForRepeated);
                        }
                        keys[index]= node.getKeys().get(i);
                        index++;
                    }else{
                        // Add it to the regression model
                        cdf = cdf + increment;
                        regression.addData(node.getKeys().get(i).getKey(), cdf);
                        System.out.println(node.getKeys().get(i).getKey()+"==="+cdf);
                        keys[index]= node.getKeys().get(i);
                        index++;
                    }
                }
           // }
            // Move to the next node
            node = node.getNext();
        }
        return regression;
    }

}

