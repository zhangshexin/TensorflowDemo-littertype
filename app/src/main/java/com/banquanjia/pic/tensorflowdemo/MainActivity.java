package com.banquanjia.pic.tensorflowdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import org.tensorflow.demo.Classifier;
import org.tensorflow.demo.TensorFlowImageClassifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TensorFlowImageClassifier classifier;
    private List<Classifier.Recognition> results;
    private TextView textView;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String text = (String) msg.obj;
                    textView.setText(text);
                    button.setText("换下一个试试");
                    button.setClickable(true);
                    break;
            }
        }
    };
    private FileOutputStream out;

    private int[] res = {R.drawable.people, R.drawable.fengjing, R.drawable.rili, R.drawable.keybord,R.drawable.cat,R.drawable.fuza,R.drawable.motuo,R.drawable.people2,R.drawable.xuni};

    private Handler mHandler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ((ImageView) findViewById(R.id.resImg)).setImageResource(res[msg.what]);
            if(i>=8)
                i=-1;
        }
    };
    Button button;
    private int i=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
          button = findViewById(R.id.btn);

        textView = findViewById(R.id.tv);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setClickable(false);
                textView.setText("等待两秒……");
                button.setText("等待两秒……");
                new Thread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
                    @Override
                    public void run() {
                        long curTime = System.currentTimeMillis();
                        i++;
                        mHandler2.sendEmptyMessage(i);
                        dealPics();
                        Log.e("lwd", "时间间隔:" + (System.currentTimeMillis() - curTime) + "毫秒");
                    }
                }).start();
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void dealPics() {
//        Bitmap bitmap = BitmapFactory.decodeResource(MainActivity.this.getResources(), R.mipmap.dog);
        // init tensorflow
        Bitmap bitmap = getSdBitmap();
        if (classifier == null) {
            // get permission
            classifier = new TensorFlowImageClassifier();
            try {
                classifier.initializeTensorFlow(
                        getAssets(), Config.MODEL_FILE, Config.LABEL_FILE,
                        Config.NUM_CLASSES, Config.INPUT_SIZE, Config.IMAGE_MEAN,
                        Config.IMAGE_STD, Config.INPUT_NAME, Config.OUTPUT_NAME);
            } catch (final IOException e) {

            }
        }
        // resize bitmap
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) Config.INPUT_SIZE) / width;
        float scaleHeight = ((float) Config.INPUT_SIZE) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbm = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        // get classifier information
        results = classifier.recognizeImage(newbm);
        StringBuilder sb = new StringBuilder();
        if (results != null) {
            for (Classifier.Recognition recognition : results) {
                sb.append(recognition.getTitle() + ":" + recognition.getConfidence() + "," + recognition.getLocation() + "\n");
            }
            Message msg = new Message();
            msg.what = 1;
            msg.obj = sb.toString();
            mHandler.sendMessage(msg);
        }
    }


    private Bitmap getSdBitmap() {
//        FileInputStream fis = null;
//        try {
//            fis = new FileInputStream("/sdcard/test.jpg");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), res[i]);
        return bitmap;

    }

}
