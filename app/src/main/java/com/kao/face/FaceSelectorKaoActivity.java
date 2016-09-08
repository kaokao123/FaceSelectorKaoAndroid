package com.kao.face;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class FaceSelectorKaoActivity extends AppCompatActivity {
    private final ImageLoader IMAGE_LOADER = new ImageLoaderAndroid();
    private static final String LOG_TAG = "[FaceSelectorKao]";

    ArrayList<Integer> mIndices, mLabels;
    AnImage[] mImgs;
    int mIndex;
    int mNumImages = 6;


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

    private class FaceSelectorTask extends AsyncTask<ArrayList<Integer>, Void, ArrayList<ClassificationResult>> {
        @Override
        protected ArrayList<ClassificationResult> doInBackground(ArrayList<Integer>... params) {
            FavoriteFaceSelector selecotor = new FavoriteFaceSelector();
            selecotor.trainWith(mImgs, params[0], params[1]);
            ArrayList<ClassificationResult> results = selecotor.getSortedFavorites(mImgs, 6);
            return results;
        }

        @Override
        protected void onPostExecute(ArrayList<ClassificationResult> classificationResults) {
            int numResults = classificationResults.size();
            LinearLayout layout2 = (LinearLayout)findViewById(R.id.layout2);
            LinearLayout layout3 = (LinearLayout)findViewById(R.id.layout3);
            if (layout3 == null || layout2 == null) {
                Log.e(LOG_TAG, "ERROR: cant find ID for the view");
                return;
            }
            layout2.setVisibility(View.GONE);
            layout3.setVisibility(View.VISIBLE);

            int [] resultImageIds = new int[]{
                    R.id.resultImage1, R.id.resultImage2, R.id.resultImage3,
                    R.id.resultImage4, R.id.resultImage5, R.id.resultImage6,
            };

            for (int i=0; i<numResults; i++) {
                ClassificationResult result = classificationResults.get(i);
                Log.i(LOG_TAG, "["+result.index+"] "+result.score+" ("+result.label+")");

                ImageView imgView = (ImageView)findViewById(resultImageIds[i]);
                AnImage anImage = mImgs[result.index];
                Bitmap bmp = Bitmap.createBitmap(anImage.getColorPixels(), anImage.getWidth(), anImage.getHeight(), Bitmap.Config.ARGB_8888);
                if (imgView == null) {
                    Log.e(LOG_TAG, "ERROR: cant find ID for the view");
                    return;
                }
                imgView.setImageBitmap(bmp);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_selector_kao);

        mImgs = IMAGE_LOADER.loadImage("faces.jpg");

        Random random = new Random();
        mIndices = new ArrayList<Integer>();
        for (int i=0; i<mNumImages; i++) {
            mIndices.add(random.nextInt(400));
        }
        mLabels = new ArrayList<Integer>();
        mIndex = 0;

        updateFaceImage();
        final RadioButton radioButton1 = (RadioButton)findViewById(R.id.RadioButton1);
        final RadioButton radioButton2 = (RadioButton)findViewById(R.id.RadioButton2);
        final Button nextButton = (Button)findViewById(R.id.button1);
        if (nextButton == null || radioButton1 == null || radioButton2 == null) {
            Log.e(LOG_TAG, "ERROR: cant find ID for the view");
            return;
        }
        radioButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                nextButton.setEnabled(true);
            }
        });
        radioButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                nextButton.setEnabled(true);
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup radioGroup = (RadioGroup)findViewById(R.id.RadioGroup);
                if (radioGroup == null || radioGroup.getCheckedRadioButtonId() < 0) {
                    Log.e(LOG_TAG, "ERROR: radio button error");
                    return;
                }
                int id = radioGroup.getCheckedRadioButtonId();
                int like = 1;
                RadioButton radioButton = (RadioButton)findViewById(id);
                if (radioButton == null || radioButton.getText().toString().equals("Dislike")) {
                    like = 0;
                }
                mLabels.add(like);
                mIndex = mLabels.size();

                if (mIndex < mNumImages) {
                    updateFaceImage();
                    radioGroup.clearCheck();
                    nextButton.setEnabled(false);
                    return;
                }

                // Finish
                String logstr = "Indices: [";
                for (int i=0; i<mNumImages; i++) {
                    logstr += "" + mIndices.get(i) + ", ";
                }
                Log.i(LOG_TAG, logstr + "]");
                logstr = "Like: [";
                for (int i=0; i<mNumImages; i++) {
                    logstr += "" + mLabels.get(i) + ", ";
                }
                Log.i(LOG_TAG, logstr + "]");
                LinearLayout layout1 = (LinearLayout)findViewById(R.id.layout1);
                LinearLayout layout2 = (LinearLayout)findViewById(R.id.layout2);
                if (layout1 == null || layout2 == null) {
                    Log.e(LOG_TAG, "ERROR: cant find ID for the view");
                    return;
                }
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);
                Log.i(LOG_TAG, "== start training");
                (new FaceSelectorTask()).execute(mIndices, mLabels);
            }
        });
    }

    private void updateFaceImage() {
        ImageView imgView = (ImageView)findViewById(R.id.imageView1);
        AnImage anImage = mImgs[mIndices.get(mIndex)];
        Bitmap bmp = Bitmap.createBitmap(anImage.getColorPixels(), anImage.getWidth(), anImage.getHeight(), Bitmap.Config.ARGB_8888);
        if (imgView == null) {
            Log.e(LOG_TAG, "ERROR: cant find ID for the view");
            return;
        }
        imgView.setImageBitmap(bmp);
    }
}
