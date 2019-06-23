package com.sjm.imagecompression.units;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.sjm.imagecompression.R;
import com.sjm.imagecompression.activity.StartFromAlbumActivity;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import static org.opencv.imgproc.Imgproc.resize;

public class Comperssion {




    /**
     * 对图片进行旋转，拍照后应用老是显示图片横向，而且是逆时针90度，现在给他设置成显示顺时针90度
     *
     * @param bitmap    图片
     * @param degree 顺时针旋转的角度
     * @return 返回旋转后的位图
     */
    public static Bitmap rotateImage(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bmp;
    }



    //保存图片方法
    public static void  saveBitmap(Bitmap bitmap, String bitName,Activity activity){
        String fileName ;
        File file ;
        if(Build.BRAND .equals("Xiaomi") ){ // 小米手机
            fileName = Environment.getExternalStorageDirectory().getPath()+"/DCIM/"+bitName ;
        }else{ // Meizu 、Oppo
            fileName = Environment.getExternalStorageDirectory().getPath()+"/DCIM/"+bitName ;
        }
        file = new File(fileName);
        if(file.exists()){
            file.delete();
        }
        FileOutputStream out;
        try{
            out = new FileOutputStream(file);
            // 格式为 JPEG，照相机拍出的图片为JPEG格式的
            if(bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out))
            {
                out.flush();
                out.close();
// 插入图库
                MediaStore.Images.Media.insertImage(activity.getContentResolver(), file.getAbsolutePath(), bitName, null);
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        // 发送广播，通知刷新图库的显示
        activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));
    }

    //质量压缩

    public static void qualityComperssion(Bitmap bitmap1,String fileName,Activity activity){
        Bitmap bitmap = bitmap1;
        try {
            //保存压缩图片到本地
            File file = new File(Environment.getExternalStorageDirectory(), "temp.JPEG");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fs);
            saveBitmap(bitmap,fileName,activity);
            fs.flush();
            fs.close();
            Toast.makeText(activity,"质量压缩操作成功！",Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void qualityComperssion(Bitmap bitmap1,String fileName,Activity activity,int quality){
        Bitmap bitmap = bitmap1;
        try {
            //保存压缩图片到本地
            File file = new File(Environment.getExternalStorageDirectory(), "temp.JPEG");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fs);
            saveBitmap(bitmap,fileName,activity);
            fs.flush();
            fs.close();
            Toast.makeText(activity,"以" + quality + "为参数质量压缩操作成功！",Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void qualityComperssionSetter(final Bitmap bitmap, final String fileName, final Activity activity){
        LayoutInflater factory = LayoutInflater.from(activity);
        final View numView = factory.inflate(R.layout.dialog_comperssion, null);
        new AlertDialog.Builder(activity)
                .setTitle("请输入压缩参数")
                .setView(numView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int whichButton){
                        EditText et = (EditText)numView.findViewById(R.id.num);
                        int quality = Integer.parseInt(et.getText().toString());
                        Comperssion.qualityComperssion(bitmap,fileName,activity,quality);

                    }
                }).setNegativeButton("取消", null).show();
    }



    public static void openCvComperssion(Bitmap bitmap,String fileName,Activity activity){
        //基于opencv的尺寸压缩
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap,mat);
        Mat mat1 = new Mat();
        resize(mat,mat1,new Size(mat.cols()/2, mat.rows()/2));
        Bitmap bm = Bitmap.createBitmap(mat1.cols(),mat1.rows(),bitmap.getConfig());
        Utils.matToBitmap(mat1,bm);
        Comperssion.saveBitmap(bm,fileName,activity);
        Toast.makeText(activity,"尺寸压缩操作成功！",Toast.LENGTH_SHORT).show();
    }

    public static void openCvComperssion(Bitmap bitmap,String fileName,Activity activity,int num){
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap,mat);
        Mat mat1 = new Mat();
        double pro = 100.0/num;
        resize(mat,mat1,new Size(mat.cols()/pro, mat.rows()/pro));
        Bitmap bm = Bitmap.createBitmap(mat1.cols(),mat1.rows(),bitmap.getConfig());
        Utils.matToBitmap(mat1,bm);
        Comperssion.saveBitmap(bm,fileName,activity);
        Toast.makeText(activity,"按照边长" + num/100.0 +"比例尺寸压缩操作成功！",Toast.LENGTH_SHORT).show();
    }

    public static void openCvComperssionSetter(final Bitmap bitmap, final String fileName, final Activity activity){
        LayoutInflater factory = LayoutInflater.from(activity);
        final View numView = factory.inflate(R.layout.dialog_comperssion, null);
        new AlertDialog.Builder(activity)
                .setTitle("请输入压缩参数（按照边长计算压缩比例）")
                .setView(numView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int whichButton){
                        EditText et = (EditText)numView.findViewById(R.id.num);
                        int pro = Integer.parseInt(et.getText().toString());
                        Comperssion.openCvComperssion(bitmap,fileName,activity,pro);

                    }
                }).setNegativeButton("取消", null).show();
    }



}
