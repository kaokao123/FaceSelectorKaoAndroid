package com.kao.face;

public class TinyCnnJni {
    static {
        System.loadLibrary("TinyCnnJni");
        _this = new TinyCnnJni();
    }

    private static TinyCnnJni _this;
    private TinyCnnJni() {}
    private static TinyCnnJni getInstance() {
        return _this;
    }

    private native int test_jni(byte []arg);
    public static int testJni(byte []arg) {
        return getInstance().test_jni(arg);
    }

    private native String test_tinycnn_jni(
            byte []in_data, int data_len, int each_data_size,
            byte []labels, int max_label,
            byte []test_data,
            int batch_size, int epoch);
    public static String testTinyCnnJni(
            byte []inData, int dataLen, int eachDataSize,
            byte []labels, int max_label,
            byte []testData,
            int batch_size, int epoch) {
        return getInstance().test_tinycnn_jni(inData, dataLen, eachDataSize, labels, max_label, testData, batch_size, epoch);
    }

    private native void init_net_jni(
            int width, int height,
            int max_label);
    public static void initNetJni(
            int width, int height,
            int max_label) {
        getInstance().init_net_jni(width, height, max_label);
    }

    private native void train_net_jni(
            byte []in_data, int data_len, int width, int height,
            byte []labels,
            int batch_size, int epoch);
    public static void trainNetJni(
            byte []in_data, int data_len, int width, int height,
            byte []labels,
            int batch_size, int epoch) {
        getInstance().train_net_jni(in_data, data_len, width, height, labels, batch_size, epoch);
    }

    private native String test_net_jni(
            byte []in_data, int width, int height);
    public static String testNetJni(
            byte []in_data, int width, int height) {
        return getInstance().test_net_jni(in_data, width, height);
    }
}
