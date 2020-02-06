package com.hogen.binarybitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Random;


public class MainActivity extends AppCompatActivity {
    private Button mBt;
    private ImageView mImage;
    private  Bitmap bitmap;
    private EditText blkValue;
    private Button mReset;
    private EditText editText;
    private Button mStr;
    private EditText density;
    private final String TAG = getClass().getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBt = findViewById(R.id.bt_binary);
        mImage = findViewById(R.id.img);
        blkValue = findViewById(R.id.temp);
        mReset = findViewById(R.id.bt_reset);
        editText = findViewById(R.id.convertstring);
        mStr = findViewById(R.id.bt_str);
        density = findViewById(R.id.density);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test2);


        mBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test2);
                bitmap = BitmapAndStringUtils.createBlackWhiteImage(bitmap,null);
                final int threadshold = Integer.parseInt(blkValue.getText().toString());
                final int densy = Integer.parseInt(density.getText().toString());
                bitmap = BitmapAndStringUtils.convertToBMW(bitmap, threadshold, densy);
                mImage.setImageBitmap(bitmap);
            }
        });

        mReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test2);
                mImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.test2));
            }
        });

        mStr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "请至少输入一个字符",Toast.LENGTH_SHORT).show();
                }else{
                    editText.setCursorVisible(false);
                    bitmap= BitmapAndStringUtils.fromText(70,editText.getText().toString());
                    bitmap = BitmapAndStringUtils.bitMapZoom(bitmap, 1.0f,1.0f);
                    Log.d(TAG, "height:"+bitmap.getHeight()+"  width:"+bitmap.getWidth());
                    mImage.setImageBitmap(bitmap);
                    editText.setCursorVisible(true);
                }
            }
        });
    }



}
