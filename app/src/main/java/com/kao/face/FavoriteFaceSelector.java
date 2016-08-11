package com.kao.face;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

class FavoriteFaceSelector {
    private static final int IMG_SIZE = 85;
    private static final int NUM_PATTERN_LIKE = 2;

    public FavoriteFaceSelector() {
        TinyCnnJni.initNetJni(IMG_SIZE, IMG_SIZE, NUM_PATTERN_LIKE);
    }

    public class ClassificationResult implements Comparable {
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

    public void trainWith(AnImage[] imgs, List<Integer> indices, List<Integer> likes) {
        int len = indices.size();
        byte [] in_data = new byte[]{};
        byte [] labels = new byte[len];

        if (likes.size() != len) {
            System.out.println("ERROR: Mismatch size: indices and likes");
            return;
        }

        for (int i=0; i<len; i++) {
            labels[i] = (byte)((int)(likes.get(i)));
            in_data = imgs[indices.get(i)].getMargedArrayPutAfter(in_data);
        }

        TinyCnnJni.trainNetJni(in_data, len, IMG_SIZE, IMG_SIZE, labels, 3, 200);
    }

    public ClassificationResult classify(AnImage img) {
        String ans = TinyCnnJni.testNetJni(img.getPixels(), IMG_SIZE, IMG_SIZE);
        String [] anss = ans.split(",");
        int label = Integer.parseInt(anss[0]);
        float score = Float.parseFloat(anss[1]);
        return new ClassificationResult(label, score);
    }

    public List<ClassificationResult> getSortedFavorites(AnImage []imgs, int num) {
        ArrayList<ClassificationResult> ans = new ArrayList<ClassificationResult>();
        int len = imgs.length;
        for (int i=0; i<len; i++) {
            ClassificationResult result = classify(imgs[i]);
            result.index = i;
            if (result.label == 0) {
                continue;
            }
            ans.add(result);
        }
        Collections.sort(ans, Collections.<ClassificationResult>reverseOrder());

        for (int i=ans.size()-1; i>=num && num>=0; i--) {
            ans.remove(i);
        }
        return ans;
    }
}
