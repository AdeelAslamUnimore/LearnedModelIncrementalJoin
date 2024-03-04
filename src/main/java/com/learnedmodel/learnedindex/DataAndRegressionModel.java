package com.learnedmodel.learnedindex;

import com.conventionalindexes.bplustree.Key;
import org.apache.commons.math3.stat.regression.SimpleRegression;
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
}
