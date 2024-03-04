package com.learnedmodel.learnedindex;
/*
   This model represents a data structure containing a key and its associated Cumulative Distribution Function (CDF) value. It serves the purpose of training the model and also for inference.

   The key value is a numerical value, while the CDF is computed as a double value. The learned model is trained on a sorted array of input values, which predicts the CDF of a given value. This predicted CDF value is then multiplied with the key to determine the position of the key.

   Any errors in the model are corrected using binary search or exponential search algorithms.
*/
public class LearnedModel {
    // Member variables to store the key and the cumulative distribution function (CDF) value
    private double key;
    private double cdfKey;

    // Constructor to initialize the key and CDF value
    public LearnedModel(double key, double cdfKey) {
        this.key = key;
        this.cdfKey = cdfKey;
    }

    // Getter method to retrieve the key
    public double getKey() {
        return key;
    }

    // Getter method to retrieve the CDF value
    public double getCdfKey() {
        return cdfKey;
    }
}
