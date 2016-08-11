package com.kao.face;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FaceSelectorKaoActivity extends AppCompatActivity {
    private final ImageLoader IMAGE_LOADER = new ImageLoaderAndroid();
    private static final String LOG_TAG = "[FaceSelectorKao]";

    private class ImageLoaderAndroid implements ImageLoader {
        public AnImage[] loadImage(String filename) {
            AnImage[] ans = null;
            try {
                final InputStream ims = getAssets().open(filename);
                final Bitmap bitmap = BitmapFactory.decodeStream(ims);
                final int width = bitmap.getWidth();
                final int height = width;
                int cnt = bitmap.getHeight() / height;

                ans = new AnImage[cnt];
                for (int i=0; i<cnt; i++) {
                    final int ii = i;
                    ans[i] = new AnImage(width, height);
                    ans[i].willBeLoadedWith(new AnImage.HowToLoad() {
                        @Override
                        public void loadPixelsTo(int[] pixels, byte[] grayScale_pixels) {
                            for (int x=0; x<width; x++) {
                                for (int y=0; y<height; y++) {
                                    int color = bitmap.getPixel(x, (y + (ii*height)));
                                    pixels[x+y*width] = color;
                                    int  red =   ((color & 0x00ff0000) >> 16);
                                    int  green = ((color & 0x0000ff00) >> 8);
                                    int  blue =  (color & 0x000000ff);
                                    int avg = (red+green+blue)/3;
                                    grayScale_pixels[x+y*width] = (byte)avg;
                                }
                            }
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return ans;
        }
    }

    private class TestFaceSelectorTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Log.e(LOG_TAG, "=== JNI test start ===");

            AnImage[] imgs = IMAGE_LOADER.loadImage("faces.jpg");
            int numImgs = 6;

            Log.e(LOG_TAG, "=== JNI tiny_cnn connection test ===");
            String ret = TinyCnnJni.testTinyCnnJni(
                    new byte[]{10, 0, 0, 0,  0, 10, 0, 0,  0, 0, 10, 0,  0, 0, 0, 10,  0, 0, 0, 0},
                    5,
                    4,
                    new byte[]{1, 2, 3, 4, 5},
                    5,
                    new byte[]{1, 3, 2, 7},
                    3,
                    2000
            );
            Log.e(LOG_TAG, "" + ret);

            Log.e(LOG_TAG, "=== Face image classification test ===");
            Random rrrr = new Random();
            List<Integer> indices = new ArrayList<Integer>();
            for (int i=0; i<numImgs; i++) {
                indices.add(rrrr.nextInt(imgs.length));
            }
            List<Integer> labels = new ArrayList<Integer>();
            for (int i=0; i<numImgs; i++) {
                labels.add(rrrr.nextInt(2));
            }
            String logstr = "Indices: [";
            for (int i=0; i<numImgs; i++) {
                logstr += "" + indices.get(i) + ", ";
            }
            Log.e(LOG_TAG, logstr + "]");
            logstr = "Like: [";
            for (int i=0; i<numImgs; i++) {
                logstr += "" + labels.get(i) + ", ";
            }
            Log.e(LOG_TAG, logstr + "]");
            FavoriteFaceSelector selecotor = new FavoriteFaceSelector();
            Log.e(LOG_TAG, "Start Training...");
            selecotor.trainWith(imgs, indices, labels);
            Log.e(LOG_TAG, "Training done!! :)");
            List<FavoriteFaceSelector.ClassificationResult> results = selecotor.getSortedFavorites(imgs, 6);
            int numResults = results.size();
            for (int i=0; i<numResults; i++) {
                FavoriteFaceSelector.ClassificationResult result = results.get(i);
                Log.e(LOG_TAG, "["+result.index+"] "+result.score+" ("+result.label+")");
            }

            return null;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_selector_kao);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(LOG_TAG, "=== JNI test task exec ===");
        (new TestFaceSelectorTask()).execute();
    }
}
