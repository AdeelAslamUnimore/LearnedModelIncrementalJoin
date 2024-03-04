package com.streamjoin.bplusandlearnedmodel;

import com.conventionalindexes.bplustree.BPlusTree;
import com.join.constants.Constants;

/**
 * Business Logic include insertion and searching for keys:
 * Background merging from mutable to immutable keys
 * Consider the incremental sliding window size
 * Merge the BPlus tree mutable structure to immutable learned model depends on threshold
 */
public class StreamJoin {
    /*
        Complete Join procedure, It include indexing data structure and learned model
     */
    public void streamInequalityJoinProcessing(int tuple, BPlusTree bPlusTree){
        bPlusTree.insert(tuple, tuple);


    }
}
