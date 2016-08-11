#include "com_kao_face_TinyCnnJni.h"
#include "faceLearning.h"


JNIEXPORT jint JNICALL Java_com_kao_face_TinyCnnJni_test_1jni(
        JNIEnv *env, jobject jthis, 
        jbyteArray src) {
    int len = env->GetArrayLength(src);
    char *args = new char[len];
    env->GetByteArrayRegion(src, 0, len, reinterpret_cast<jbyte*>(args));
    int ans = 0;
    for (int i=0; i<len; i++) {
        ans += args[i];
    }
    ans /= len;
    delete args;
    return ans;
}

JNIEXPORT jstring JNICALL Java_com_kao_face_TinyCnnJni_test_1tinycnn_1jni(
        JNIEnv *env, jobject jthis, 
        jbyteArray in_data, jint data_len, jint each_data_size, 
        jbyteArray labels, jint max_label,
        jbyteArray test_data,
        jint batch_size, jint epochs) {
    int len = env->GetArrayLength(in_data);
    char *in_char = new char[len];
    env->GetByteArrayRegion(in_data, 0, len, reinterpret_cast<jbyte*>(in_char));
    int len_label = env->GetArrayLength(labels);
    char *label_char = new char[len_label];
    env->GetByteArrayRegion(labels, 0, len_label, reinterpret_cast<jbyte*>(label_char));
    int len_test = env->GetArrayLength(test_data);
    char *test_char = new char[len_test];
    env->GetByteArrayRegion(test_data, 0, len_test, reinterpret_cast<jbyte*>(test_char));
    
    std::vector<tiny_cnn::vec_t> in_vec = {};
    for (int i=0; i<data_len; i++) {
        tiny_cnn::vec_t tmp_vec;
        for (int j=0; j<each_data_size; j++) {
            tmp_vec.push_back(in_char[i*each_data_size + j]);
        }
        in_vec.push_back(tmp_vec);
    }
    
    std::vector<tiny_cnn::label_t> label_vec = {};
    for (int i=0; i<len_label; i++) {
        label_vec.push_back(label_char[i]);
    }
    
    tiny_cnn::vec_t test_vec = {};
    for (int i=0; i<len_test; i++) {
        test_vec.push_back(test_char[i]);
    }
    
    std::string ans = test_tinycnn(in_vec, label_vec, max_label, test_vec, batch_size, epochs);

    delete in_char;
    delete label_char;
    delete test_char;
    return env->NewStringUTF(ans.c_str());
}

JNIEXPORT void JNICALL Java_com_kao_face_TinyCnnJni_init_1net_1jni(
        JNIEnv *env, jobject jthis, 
        jint width, jint height,
        jint max_label) {
    init_net(width, height, max_label);
}

JNIEXPORT void JNICALL Java_com_kao_face_TinyCnnJni_train_1net_1jni(
        JNIEnv *env, jobject jthis, 
        jbyteArray in_data, jint data_len, jint width, jint height,
        jbyteArray labels,
        jint batch_size, jint epochs) {
    int len = env->GetArrayLength(in_data);
    int img_size = width*height;
    assert(len == img_size*data_len);
    char *in_char = new char[len];
    env->GetByteArrayRegion(in_data, 0, len, reinterpret_cast<jbyte*>(in_char));
    int len_label = env->GetArrayLength(labels);
    assert(len_label == data_len);
    char *label_char = new char[len_label];
    env->GetByteArrayRegion(labels, 0, len_label, reinterpret_cast<jbyte*>(label_char));
    
    std::vector<tiny_cnn::vec_t> in_vec = {};
    for (int i=0; i<data_len; i++) {
        tiny_cnn::vec_t tmp_vec;
        for (int j=0; j<img_size; j++) {
            tmp_vec.push_back(in_char[i*img_size + j]);
        }
        in_vec.push_back(tmp_vec);
    }
    std::vector<tiny_cnn::label_t> label_vec = {};
    for (int i=0; i<len_label; i++) {
        label_vec.push_back(label_char[i]);
    }

    train_net(in_vec, label_vec, batch_size, epochs);

    delete in_char;
    delete label_char;
}

JNIEXPORT jstring JNICALL Java_com_kao_face_TinyCnnJni_test_1net_1jni(
        JNIEnv *env, jobject jthis, 
        jbyteArray in_data, jint width, jint height) {
    int len_in = env->GetArrayLength(in_data);
    assert(len_in == width*height);
    char *in_char = new char[len_in];
    env->GetByteArrayRegion(in_data, 0, len_in, reinterpret_cast<jbyte*>(in_char));

    tiny_cnn::vec_t in_vec = {};
    for (int i=0; i<len_in; i++) {
        in_vec.push_back(in_char[i]);
    }
    std::string ans = test_net(in_vec);
    delete in_char;
    return env->NewStringUTF(ans.c_str());
}
