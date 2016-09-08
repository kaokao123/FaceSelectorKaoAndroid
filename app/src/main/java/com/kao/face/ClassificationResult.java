package com.kao.face;

import java.io.Serializable;

public class ClassificationResult implements Comparable, Serializable {
    public int label;
    public float score;
    public int index;

    public ClassificationResult(int label, float score) {
        this.label = label;
        this.score = score;
        this.index = -1;
    }

    @Override
    public int compareTo(Object o) {
        ClassificationResult oo = (ClassificationResult)o;
        return (int)(this.score*10000) - (int)(oo.score*10000);
    }
}
