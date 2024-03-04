package com.conventionalindexes.bplustree;
import java.util.ArrayList;
import java.util.List;

/*
 * author :
 */

/**
 * The Class Key.
 */
public class Key {

    /** The key. */
    int key;

    /** The list of values for the key. Set only for external nodes*/
    List<Integer> values;

    /**
     * Instantiates a new key and its value.
     *
     * @param key
     *            the key
     * @param value
     *            the value
     */
    public Key(int  key, int value) {
        this.key = key;
        if (null == this.values) {
            values = new ArrayList();
        }
        this.values.add(value);
    }

    /**
     * Instantiates a new key
     *
     * @param key
     *            the key
     */
    public Key(int key) {
        this.key = key;
        this.values = new ArrayList();
    }

    /**
     * Gets the key.
     *
     * @return the key
     */
    public int getKey() {
        return key;
    }

    /**
     * Sets the key.
     *
     * @param key
     *            the new key
     */
    public void setKey(int key) {
        this.key = key;
    }

    /**
     * Gets the values.
     *
     * @return the values
     */
    public List<Integer> getValues() {
        return values;
    }

    /**
     * Sets the values.
     *
     * @param values
     *            the new values
     */
    public void setValues(List<Integer> values) {
        this.values = values;
    }

    public String toString() {
        return "Key [key=" + key + ", values=" + values + "]";
    }

}