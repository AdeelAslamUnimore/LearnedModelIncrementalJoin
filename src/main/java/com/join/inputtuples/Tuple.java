package com.join.inputtuples;
/**
 * Represents a tuple with an associated timestamp.
 */
public class Tuple implements Comparable<Tuple> {

    /** The value of the tuple. */
    private final int tuple;

    /** The timestamp associated with the tuple. */
    private final long timeStamp;

    /**
     * Constructs a new Tuple object with the given value and timestamp.
     *
     * @param tuple the value of the tuple
     * @param timeStamp the timestamp associated with the tuple
     */
    public Tuple(int tuple, long timeStamp) {
        this.tuple = tuple;
        this.timeStamp= timeStamp;
    }

    /**
     * Compares this Tuple with another Tuple based on their timestamps.
     *
     * @param record the Tuple to compare with
     * @return a negative integer, zero, or a positive integer if this Tuple is less than, equal to,
     *         or greater than the specified Tuple, respectively, based on their timestamps
     */
    @Override
    public int compareTo(Tuple record) {
        return Long.compare(this.timeStamp, record.timeStamp);
    }

    /**
     * Returns the value of the tuple.
     *
     * @return the value of the tuple
     */
    public int getTuple() {
        return tuple;
    }
}
