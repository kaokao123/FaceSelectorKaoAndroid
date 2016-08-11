#include "faceLearning.h"
#include "tiny_cnn/tiny_cnn.h"
#include <string>
#include <sstream>
#include <vector>

using namespace tiny_cnn;
using namespace tiny_cnn::activation;
using namespace tiny_cnn::layers;

// This variable will be individually allocated for each process who uses this DLL.
network<sequential> net;


std::string test_tinycnn(
        const std::vector<vec_t>& in_data, 
        const std::vector<label_t>& labels, int max_label,
        const vec_t& test_data,
        int batch_size, int epochs)
{
    int data_len = in_data.size();
    int data_size = in_data[0].size();
    network<sequential> test_net;
    
    test_net << fully_connected_layer<sigmoid>(data_size, data_size)
             << fully_connected_layer<softmax>(data_size, max_label + 1);
    
    adagrad opt;
    test_net.train<mse>(opt, in_data, labels, batch_size, epochs);
    std::stringstream ans;
    
    ans << "input: ";
    for (vec_t vec : in_data) {
        ans << "[";
        for (auto val : vec) {
            ans << val << ", ";
        }
        ans << "], ";
    }
    ans << std::endl;
    ans << "label: ";
    for (auto val : labels) {
        ans << ", " << val;
    }
    ans << std::endl;
    ans << "test: ";
    for (auto val : test_data) {
        ans << ", " << val;
    }
    ans << std::endl;
    
    ans << "result:" << test_net.predict_label(test_data) << std::endl
        << "similarity:" << test_net.predict_max_value(test_data) << std::endl;
    return ans.str();
}


void init_net(
        int width, int height,
        int max_label)
{
    net << convolutional_layer<tan_h>(85, 85, 5, 1, 6)   // conv5x5 6featuremaps
        << average_pooling_layer<tan_h>(81, 81, 6, 9)    // 28x28in, pool2x2
        << fully_connected_layer<tan_h>(9 * 9 * 6, 120)  // to 120out
        << fully_connected_layer<softmax>(120, max_label + 1);
}

void train_net(
        const std::vector<vec_t>& images, 
        const std::vector<label_t>& labels, 
        int batch_size, int epochs)
{
    adagrad opt;
    net.train<mse>(opt, images, labels, batch_size, epochs);
}

std::string test_net(
        const vec_t& in)
{
    std::stringstream ans;
    ans << net.predict_label(in) << "," << net.predict_max_value(in);
    return ans.str();
}

