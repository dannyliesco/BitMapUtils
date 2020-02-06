package com.hogen.binarybitmap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ThumbnailUtils;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.Random;

public class BitmapAndStringUtils {

    private static final String TAG = "BitmapAndStringUtils";

    public static String convertBitmapToString(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();
        return Base64.encodeToString(appicon, Base64.DEFAULT);
    }


    public static Bitmap fromText(float textSize, String text) {
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(Color.BLACK);

        Paint.FontMetricsInt fm = paint.getFontMetricsInt();

        int width = (int)paint.measureText(text);
        int height = fm.descent - fm.ascent;
        Log.d(TAG, "fromText: height = "+height);
        Log.d(TAG, "fromText: width = "+width);


        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(text, 0, fm.leading - fm.ascent, paint);

        Log.d(TAG, "drawText fm.leading = "+fm.leading );
        Log.d(TAG, "drawText: fm.ascent = "+fm.ascent);
        canvas.save();

        return bitmap;
    }


    public static Bitmap convertToBMW(Bitmap bmp,int threshold,int densy) {
        int width = bmp.getWidth(); // 获取位图的宽
        int height = bmp.getHeight(); // 获取位图的高
        int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数

        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        int alpha = 0xFF << 24;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int k = 0;
                int grey = pixels[width * i + j];
                // 分离三原色
                alpha = ((grey & 0xFF000000) >> 24);
                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);
                if (red > threshold) {
                    red = 255;
                } else {
                    red = 0;
                }
                if (blue > threshold) {
                    blue = 255;
                } else {
                    blue = 0;
                }
                if (green > threshold) {
                    green = 255;
                } else {
                    green = 0;
                }
                pixels[width * i + j] = alpha << 24 | red << 16 | green << 8
                        | blue;

                if (pixels[width * i + j] == 0xffffffff) {
                    pixels[width * i + j] = 0xffffffff;//白色
                } else {
                    pixels[width * i + j] = 0xff000000;//-16777216;//黑色
                }
            }
        }
        pixels = setDensity(densy,pixels,width,height);
        Bitmap newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        newBmp.setPixels(pixels, 0, width, 0, 0, width, height);
        Bitmap resizeBmp = ThumbnailUtils.extractThumbnail(newBmp, width, height);
        return resizeBmp;
    }

    public static  int[] setDensity(int density,int[] pixels,int width,int height){
        int[] result = new int[width*height];
        Random random = new Random();
        int myDensity = 0;

        if( density > 10 || density < 1){
            return pixels;
        }

        for (int i = 0; i < width*height; i++) {
            if(pixels[i] == 0xff000000){
                myDensity = random.nextInt(10);
                if( 0 <= myDensity && myDensity <= density){
                    result[i] = 0xff000000;//黑色
                }
            }else{
                result[i] = pixels[i];
            }
        }
        return result;
    }

    public static Bitmap createBlackWhiteImage(Bitmap image, float[] radios) {

        int width = image.getWidth();   //获取位图的宽
        int height = image.getHeight();  //获取位图的高

        Bitmap result = Bitmap.createBitmap(width, height, image.getConfig());

        int alpha = 0xff;
        int r = 0;
        int g = 0;
        int b = 0;
        int max = 0;
        int min = 0;
        int mid = 0;
        int gray = 0;

        float radioMax = 0;
        float radioMaxMid = 0;

        if (radios == null) {
            // 红        黄         绿         青         蓝        紫
            radios = new float[]{0.4f, 0.6f, 0.4f, 0.6f, 0.2f, 0.8f};
        }
        int[] resultPixle = new int[width * height];
        image.getPixels(resultPixle, 0, width, 0, 0, width, height);

        for (int i = 0; i < width; i++) {//一行行扫描
            for (int j = 0; j < height; j++) {

                gray = resultPixle[j * width + i];
//                gray = image.getPixel(i,j);//此方法效率极低，不要出现在循环体中，否则将导致极度耗时

                alpha = gray >>> 24;
                r = (gray >> 16) & 0x000000ff;
                g = (gray >> 8) & 0x000000ff;
                b = gray & 0x000000ff;


                if (r >= g && r >= b) {
                    max = r;
                    radioMax = radios[0];
                }
                if (g >= r && g >= b) {
                    max = g;
                    radioMax = radios[2];
                }
                if (b >= r && b >= g) {
                    max = b;
                    radioMax = radios[4];
                }
                if (r <= g && r <= b) { // g+ b = cyan 青色
                    min = r;
                    radioMaxMid = radios[3];
                }

                if (b <= r && b <= g) {//r+g = yellow 黄色
                    min = b;
                    radioMaxMid = radios[1];
                }
                if (g <= r && g <= b) {//r+b = m 洋红
                    min = g;
                    radioMaxMid = radios[5];
                }

                mid = r + g + b - max - min;

                //公式：gray= (max - mid) * ratio_max + (mid - min) * ratio_max_mid + min

                gray = (int) ((max - mid) * radioMax + (mid - min) * radioMaxMid + min);

                gray = (alpha << 24) | (gray << 16) | (gray << 8) | gray;

                //                2000x3500大图，耗时相差2~5倍左右
                //bitmap在循环中设置像素点，这个操作会导致耗时严重，耗时7秒。4096x4096图耗时22秒
                //                result.setPixel(i, j, gray);
                resultPixle[j * width + i] = gray;//直接改变数组，最后bitmap再设像素
            }
        }

        result.setPixels(resultPixle, 0, width, 0, 0, width, height);//最后bitmap再设像素

        return result;
    }


    public static Bitmap bitMapZoom(Bitmap bitmap,float horizlzoom, float vertialzoom) {
        Matrix matrix = new Matrix();
        if(horizlzoom <= 0 ||vertialzoom <= 0){
            return bitmap;
        }
        matrix.postScale(horizlzoom,vertialzoom); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return resizeBmp;
    }
}
