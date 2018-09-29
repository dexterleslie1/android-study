package com.future.android.study.storage;

import android.content.Context;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.lang.reflect.Field;

/**
 * @author Dexterleslie.Chan
 */
@RunWith(AndroidJUnit4.class)
public class StorageApiTests {
    private final static String TAG=StorageApiTests.class.getSimpleName();

    @Test
    public void test() throws IllegalAccessException {
        Context context= InstrumentationRegistry.getTargetContext();
        String packageName=context.getPackageName();

        // 外部存储的下载文件的缓存路径
        File path=Environment.getDownloadCacheDirectory();
        Assert.assertEquals("/cache",path.getPath());

        path=Environment.getRootDirectory();
        Assert.assertEquals("/system",path.getPath());

        // 内部存储数据库目录路径
        path=Environment.getDataDirectory();
        Assert.assertEquals("/data",path.getPath());

        // 外部存储根目录
        path=Environment.getExternalStorageDirectory();
        Assert.assertEquals("/storage/emulated/0",path.getPath());
        String externalStorageDirectory=path.getPath();

        // 外部存储挂载状态
        String state=Environment.getExternalStorageState();
        Assert.assertEquals("mounted",state);

        // 设备的外存是否用内部存储模拟的，是则返回true
        boolean isEmulated=Environment.isExternalStorageEmulated();
        Assert.assertEquals(true,isEmulated);

        // 设备的外存是否是可以拆卸的，比如SD卡，是则返回true
        boolean isRemovable=Environment.isExternalStorageRemovable();
        Assert.assertEquals(false,isRemovable);

        // 内部存储data/data/包名/files目录
        path=context.getFilesDir();
        Assert.assertEquals("/data/data/"+packageName+"/files",path.getPath());
        // 内部存储data/data/包名/cache目录
        path=context.getCacheDir();
        Assert.assertEquals("/data/data/"+packageName+"/cache",path.getPath());

        // 外部存储公有目录
        String fieldPrefix="DIRECTORY_";
        Field []fields=Environment.class.getFields();
        for(int i=0;i<fields.length;i++){
            String fieldname=fields[i].getName();
            if(fieldname.startsWith(fieldPrefix)){
                String fieldValue=(String)fields[i].get(null);
                path=Environment.getExternalStoragePublicDirectory(fieldValue);
                Assert.assertEquals(externalStorageDirectory+"/"+fieldValue,path.getPath());
            }
        }

        // 外部存储私有目录storage/sdcard/Android/data/包名/cache
        path=context.getExternalCacheDir();
        Assert.assertEquals(externalStorageDirectory+"/Android/data/"+packageName+"/cache",path.getPath());

        // 外部存储私有目录storage/sdcard/Android/data/包名/files
        path=context.getExternalFilesDir(null);
        Assert.assertEquals(externalStorageDirectory+"/Android/data/"+packageName+"/files",path.getPath());
        String externalFileDirectory=path.getPath();

        // 外部存储私有目录storage/sdcard/Android/data/包名/files/目录类型
        fields=Environment.class.getFields();
        for(int i=0;i<fields.length;i++){
            String fieldname=fields[i].getName();
            if(fieldname.startsWith(fieldPrefix)){
                String fieldValue=(String)fields[i].get(null);
                path=context.getExternalFilesDir(fieldValue);
                Assert.assertEquals(externalFileDirectory+"/"+fieldValue,path.getPath());
            }
        }
    }
}
