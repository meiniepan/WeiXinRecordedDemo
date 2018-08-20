package com.zhaoshuang.weixinrecorded.ImageUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.gifencoder.AnimatedGifEncoder;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by Solang on 2018/8/17.
 */

public class ImgUtil {
    public static String createGif(String filename, List<Bitmap> bitmaps, int fps, int width, int height) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AnimatedGifEncoder localAnimatedGifEncoder = new AnimatedGifEncoder();
        localAnimatedGifEncoder.start(baos);//start
        localAnimatedGifEncoder.setRepeat(0);//设置生成gif的开始播放时间。0为立即开始播放
        localAnimatedGifEncoder.setDelay(fps);
        if (bitmaps.size() > 0) {
            for (int i = 0; i < bitmaps.size(); i++) {
                Bitmap bitmap = bitmaps.get(i);
                Bitmap resizeBm = resizeImage(bitmap, width, height);
                localAnimatedGifEncoder.addFrame(resizeBm);
            }
        }
        localAnimatedGifEncoder.finish();//finish

        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/LiliNote");
        if (!file.exists()) {
            file.mkdir();
        }
        filename = System.currentTimeMillis()+"";
        String path = Environment.getExternalStorageDirectory().getPath() + "/LiliNote/" + filename + ".gif";
        File file2 = new File(path);
//        if (file2.isFile()){
//            file2.delete();
//        }
        FileOutputStream fos = new FileOutputStream(path);
        baos.writeTo(fos);
        baos.flush();
        fos.flush();
        baos.close();
        fos.close();

        return path;
    }

    //使用Bitmap加Matrix来缩放
    public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;
    }

    public static Bitmap createAsciiPic(final Bitmap bitmap, Context context) {
        final String base = "\"@#&$%*o!;.";// 字符串由复杂到简单
//        final String base = "#,.0123456789:;@ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";// 字符串由复杂到简单
        StringBuilder text = new StringBuilder();
        final Bitmap image = scale(bitmap, 150, 200);  //读取图片
        //输出到指定文件中
        for (int y = 0; y < image.getHeight(); y += 2) {
            for (int x = 0; x < image.getWidth(); x++) {
                final int pixel = image.getPixel(x, y);
                final int r = (pixel & 0xff0000) >> 16, g = (pixel & 0xff00) >> 8, b = pixel & 0xff;
                final float gray = 0.299f * r + 0.578f * g + 0.114f * b;
                final int index = Math.round(gray * (base.length() + 1) / 255);
                String s = index >= base.length() ? " " : String.valueOf(base.charAt(index));
                text.append(s);
            }
            text.append("\n");
        }
        return creatCodeBitmap(text, context);
    }

    public static Bitmap creatCodeBitmap(StringBuilder contents, Context context) {
//        contents = new StringBuilder().append("")
        float scale = context.getResources().getDisplayMetrics().scaledDensity;

        TextView tv = new TextView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(layoutParams);
        tv.setText(contents);
        tv.setTextSize(scale * 2);
        tv.setTypeface(Typeface.MONOSPACE);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setDrawingCacheEnabled(true);
        tv.setTextColor(Color.GRAY);
        tv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());


        tv.setBackgroundColor(Color.WHITE);

        tv.buildDrawingCache();
        Bitmap bitmapCode = tv.getDrawingCache();
        return bitmapCode;
    }

    public static Bitmap scale(Bitmap src, int newWidth, int newHeight) {
        Bitmap ret = Bitmap.createScaledBitmap(src, newWidth, newHeight, true);
        return ret;
    }
}
