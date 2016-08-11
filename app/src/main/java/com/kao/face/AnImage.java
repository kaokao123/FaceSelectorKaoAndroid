package com.kao.face;

import java.io.Serializable;

class AnImage implements Serializable {
    private int width;
    private int height;
    private int [] color_pixels;
    private byte [] gs_pixels;


    public AnImage(int w, int h) {
        width = w;
        height = h;
        color_pixels = new int[w * h];
        gs_pixels = new byte[w * h];
    }

    public interface HowToLoad {
        void loadPixelsTo(int []pixels, byte[]grayScale_pixels);
    }

    public void willBeLoadedWith(HowToLoad howToLoad) {
        howToLoad.loadPixelsTo(color_pixels, gs_pixels);
    }

    public byte[] getMargedArrayPutAfter(byte[] src) {
        int orig_size = src.length;
        int this_size = gs_pixels.length;
        byte [] ans = new byte[orig_size + this_size];
        System.arraycopy(src, 0, ans, 0, orig_size);
        System.arraycopy(gs_pixels, 0, ans, orig_size, this_size);
        return ans;
    }

    public byte[] getPixels() {
        byte [] ans = new byte[gs_pixels.length];
        System.arraycopy(gs_pixels, 0, ans, 0, gs_pixels.length);
        return ans;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public String toString() {
        String ans = "";
        String newLine = System.getProperty("line.separator");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int val = (gs_pixels[x+y*width]) & 0xff;
                ans += " " + (val);
            }
            ans += newLine;
        }

        return ans;
    }
}
