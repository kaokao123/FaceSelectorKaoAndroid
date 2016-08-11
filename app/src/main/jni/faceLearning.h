#ifndef __FACE_LEARNING_H
#define __FACE_LEARNING_H

#include "tiny_cnn/tiny_cnn.h"
#include <string>
#include <vector>

std::string test_tinycnn(
    const std::vector<tiny_cnn::vec_t>& in_data, 
    const std::vector<tiny_cnn::label_t>& labels, int max_label,
    const tiny_cnn::vec_t& test_data,
    int batch_size, int epochs);

void init_net(
    int width, 
    int height,
    int max_label);

void train_net(
    const std::vector<tiny_cnn::vec_t>& images, 
    const std::vector<tiny_cnn::label_t>& labels, 
    int batch_size, 
    int epochs);

std::string test_net(
    const tiny_cnn::vec_t& in);


#endif
