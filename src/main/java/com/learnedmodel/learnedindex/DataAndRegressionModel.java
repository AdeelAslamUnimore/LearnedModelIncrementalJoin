package com.learnedmodel.learnedindex;

import com.conventionalindexes.bplustree.Key;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class represents a combination of data and a regression model, used in an immutable part of tuple evaluation.
 * It encapsulates an array of keys containing both key and payload data, along with a Simple Regression model.
 * Key attributes include the array of mutable indexing data structure and the Simple Regression model.
 */
public class DataAndRegressionModel {
    // Contains an array that holds both keys and their payload
    private Key[] keys;
    // Represents a Simple Regression Model with slope and intercept
    private SimpleRegression simpleRegression;

    /**
     * Constructs a DataAndRegressionModel object with the specified array of keys and Simple Regression model.
     *
     * @param keys             The array of keys containing both key and payload data
     * @param simpleRegression The Simple Regression model with slope and intercept
     */
    public DataAndRegressionModel(Key[] keys, SimpleRegression simpleRegression) {
        this.keys = keys;
        this.simpleRegression = simpleRegression;
    }

    /**
     * Retrieves the array of keys.
     *
     * @return The array of keys
     */
    public Key[] getKeys() {
        return keys;
    }

    /**
     * Retrieves the Simple Regression model.
     *
     * @return The Simple Regression model
     */
    public SimpleRegression getSimpleRegression() {
        return simpleRegression;
    }
    /**
     * Search RelevantKey
     * @param   key
     */
    public void searchRelevantKey(int key){
        // Predicted by model
        double predictedCDF= simpleRegression.predict((double)key);
        // Get index by multiplying the CDF with that key
        int predictedPosition= (int) (predictedCDF*keys.length);
        int index;
        if (keys[predictedPosition].getKey() == key) {
            index = predictedPosition;
        } else if (keys[predictedPosition].getKey() < key) {
            index = binarySearch(keys, 0, predictedPosition, key);
        } else {
            index = binarySearch(keys, predictedPosition, keys.length - 1, key);
        }
    }
    /**
     * Search greater than Specific key
     * @param key
     */
    public synchronized void searGreaterThanSpecifiedKey(int key){
        // Predicted by model

        double predictedCDF= simpleRegression.predict(key);
        // Get index by multiplying the CDF with that key
        int predictedPosition= (int) (predictedCDF*keys.length);
        if(predictedPosition<0){
            predictedPosition=0;
        }
        if(predictedPosition>=keys.length){
            predictedPosition=keys.length-1;
        }
        int index;
        System.out.println(key+"====="+predictedCDF+"==="+ predictedPosition+"==="+keys.length);
        try {
            System.out.println(keys[predictedPosition].getKey() + "=====" + key);
        }catch (Exception e){
            System.out.println(e);
            System.out.println(key+"====="+ predictedPosition+"==="+keys.length);
//            for(Key keyN:keys){
//                System.out.println(keyN);
//            }
            System.exit(-1);
        }
        if (keys[predictedPosition].getKey() == key) {
          //  System.out.println(predictedCDF+"=Equal="+keys.length+"Key=="+key);
            index = predictedPosition;
            // Iterating all tuples
            int counter=0;
            for(int i=index;i<keys.length;i++){
                counter++;
            }
        } else if (keys[predictedPosition].getKey() < key) {
          //  System.out.println(predictedCDF+"=Less="+keys.length+"Key=="+key);
            index = binarySearch(keys, 0, predictedPosition, key);
            int counter=0;
            for(int i=index;i<keys.length;i++){
                counter++;
            }
        } else {
          //  System.out.println(predictedCDF+"=Great="+keys.length+"Key=="+key);
            index = binarySearch(keys, predictedPosition, keys.length - 1, key);
            int counter=0;
            for(int i=index;i<keys.length;i++){
                counter++;
            }
        }
    }
    /**
     * Band search
     * @param  key
     * @param threshold
     */
    public void bandSearch(int key, int threshold){
        double predictedCDF= simpleRegression.predict((double)key);
        // Get index by multiplying the CDF with that key
        int predictedPosition= (int) (predictedCDF*keys.length);
        int index;
        if (keys[predictedPosition].getKey() == key) {
            index = predictedPosition;
            computeBandJoin( index,  key,  threshold );
        } else if (keys[predictedPosition].getKey() < key) {
            index = binarySearch(keys, 0, predictedPosition, key);
            computeBandJoin( index,  key,  threshold );
        } else {
            index = binarySearch(keys, predictedPosition, keys.length - 1, key);
            computeBandJoin( index,  key,  threshold );
        }
    }

    /**
     * binarySearch to identify particular key.
     * @param keys array need to be search
     * @param start start from where search should start
     * @param end end upto where search should be performed
     * @param target target key
     * @return index
     */

    public static int binarySearch(Key[] keys, int start, int end, int target) {
        int left = start;
        int right = end - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (keys[mid].getKey() == target) {
                // Found the target key, return its index
                return mid;
            } else if (keys[mid].getKey() < target) {
                // If current key is less than target, search in the right half
                left = mid + 1;
            } else {
                // If current key is greater than target, search in the left half
                right = mid - 1;
            }
        }

        // If the target key is not found, return the index where it should be inserted
        return left;
    }


    public void computeBandJoin(int index, int key, int threshold ){
        // Create an ExecutorService with a fixed thread pool size
        ExecutorService executor = Executors.newFixedThreadPool(2);

// Forward loop task
        Runnable forwardTask = () -> {
            for (int i = index; i < keys.length; i++) {
                if (Math.abs(keys[index].getKey() - key) < threshold) {
                    // Process forward loop
                    // Add your processing logic here
                } else {
                    break;
                }
            }
        };

// Backward loop task
        Runnable backwardTask = () -> {
            for (int i = index; i >= 0; i--) {
                if (Math.abs(keys[index].getKey() - key) < threshold) {
                    // Process backward loop
                    // Add your processing logic here
                } else {
                    break;
                }
            }
        };

// Submit tasks to the executor
        executor.submit(forwardTask);
        executor.submit(backwardTask);

// Shutdown the executor after completing the tasks
        executor.shutdown();


    }



}
