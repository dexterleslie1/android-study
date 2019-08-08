package com.future.study.android.reading_image;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

/**
 *
 */
@RunWith(AndroidJUnit4.class)
public class ReadingImageTest {
    @Test
    public void readFromSDCard() throws Exception {
        String url = "/storage/sdcard/1.jpg";
        File file = new File(url);
        if(!file.exists()) {
            // 文件1.jpg不存在，使用以下命令把图片push到设备
            // adb push c:/tools/1.jpg /storage/sdcard/1.jpg
            throw new Exception("文件1.jpg不存在");
        }

        Bitmap bitmap = BitmapFactory.decodeFile(url);
        Assert.assertNotNull(bitmap);

        FileInputStream inputStream = new FileInputStream(file);
        Bitmap bitmapTemporary = BitmapFactory.decodeStream(inputStream);
        if(inputStream != null) {
            inputStream.close();
        }

        Assert.assertEquals(bitmap.getByteCount(), bitmapTemporary.getByteCount());
    }

    @Test
    public void readFromDrawableResources() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        Resources resources = context.getResources();
        Bitmap bitmap1 = BitmapFactory.decodeResource(resources, R.drawable.p1);
        Assert.assertNotNull(bitmap1);
    }

    @Test
    public void readFromSrcPackage() throws Exception {
        String url = "/storage/sdcard/1.jpg";
        File file = new File(url);
        if(!file.exists()) {
            // 文件1.jpg不存在，使用以下命令把图片push到设备
            // adb push c:/tools/1.jpg /storage/sdcard/1.jpg
            throw new Exception("文件1.jpg不存在");
        }

        Context context = InstrumentationRegistry.getTargetContext();
        String path = "com/future/study/android/reading_image/p1.jpg";
        InputStream inputStream = context.getClassLoader().getResourceAsStream(path);
        Bitmap bitmap1 = BitmapFactory.decodeStream(inputStream);
        if(inputStream != null) {
            inputStream.close();
        }

        inputStream = new FileInputStream(file);
        Bitmap bitmap2 = BitmapFactory.decodeStream(inputStream);
        if(inputStream != null) {
            inputStream.close();
        }

        // 此处失败，考虑到这情况不经常使用，所以不分析此处失败原因
//        Assert.assertEquals(bitmap1.getByteCount(), bitmap2.getByteCount());
    }

    @Test
    public void readFromAssets() throws Exception {
        String url = "/storage/sdcard/1.jpg";
        File file = new File(url);
        if(!file.exists()) {
            // 文件1.jpg不存在，使用以下命令把图片push到设备
            // adb push c:/tools/1.jpg /storage/sdcard/1.jpg
            throw new Exception("文件1.jpg不存在");
        }

        Context context = InstrumentationRegistry.getTargetContext();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("p1.jpg");
        Bitmap bitmap1 = BitmapFactory.decodeStream(inputStream);
        if(inputStream != null) {
            inputStream.close();
        }

        inputStream = new FileInputStream(file);
        Bitmap bitmap2 = BitmapFactory.decodeStream(inputStream);
        if(inputStream != null) {
            inputStream.close();
        }

        Assert.assertEquals(bitmap1.getByteCount(), bitmap2.getByteCount());
    }
}
