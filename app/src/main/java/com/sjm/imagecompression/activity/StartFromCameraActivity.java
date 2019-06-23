package com.sjm.imagecompression.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.sjm.imagecompression.R;
import com.sjm.imagecompression.units.Comperssion;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.opencv.android.Utils.bitmapToMat;
import static org.opencv.android.Utils.matToBitmap;

public class StartFromCameraActivity extends AppCompatActivity implements View.OnClickListener{

    public static final int TAKE_PHOTO = 1;

    private ImageView picture;

    private Uri imageUri;


    private String imageName;

    private DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); //使用时间戳作为文件名
    private String fileName = format.format(new Date())+".JPEG";

    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_from_camera);


        picture = (ImageView) findViewById(R.id.picture);

        this.findViewById(R.id.str_btn1);

        Button takePhoto = (Button) findViewById(R.id.take_photo);

        takePhoto.setOnClickListener(this);
    }


    //操作选择框
    public void showSingleAlertDialog(View view){
        final String[] items = {this.getString(R.string.openCvComperssion),this.getString(R.string._openCvComperssion)};
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle(this.getString(R.string.comperssionTitle));
        alertBuilder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //压缩操作
                Bitmap bitmap;
                if (imageUri!=null){
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        switch (i){
                            case 0:
                                Comperssion.openCvComperssion(Comperssion.rotateImage(bitmap,90),fileName,StartFromCameraActivity.this);
                                break;
                            case 1:
                                Comperssion.openCvComperssionSetter(Comperssion.rotateImage(bitmap,90),fileName,StartFromCameraActivity.this);
                                break;
                        }


                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
                alertDialog.dismiss();
            }
        });


        alertDialog = alertBuilder.create();
        alertDialog.show();
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.take_photo://拍照
                // 创建File对象，用于存储拍照后的图片
                File outputImage = new File(getExternalCacheDir(), format.format(new Date())+".JPEG");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                    Log.i("233","" + outputImage.toString());
                    Bitmap bitMBitmap = BitmapFactory.decodeFile(outputImage.getPath());
                    if(bitMBitmap == null)Log.i("233","bitMBitmap=null");

                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    imageUri = FileProvider.getUriForFile(StartFromCameraActivity.this, "com.example.cameraalbumtest.fileprovider", outputImage);
                } else {
                    imageUri = Uri.fromFile(outputImage);
                }

                // 启动相机程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);
                break;



        }



    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try{

                        // 将拍摄的照片显示出来,因为直接拍出来的照片方向是错的 所以需要顺时针旋转90度
                        Bitmap bitmap = Comperssion.rotateImage(BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri)),90);
                        Log.i("233",imageUri.toString());
                        picture.setImageBitmap(bitmap);

                        //保存图片
                        imageName = format.format(new Date())+".JPEG";
                        Comperssion.saveBitmap(bitmap,imageName,this);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                Toast.makeText(this,"error",Toast.LENGTH_LONG).show();
                break;
        }
    }


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            super.onManagerConnected(status);
            switch (status){
                case BaseLoaderCallback.SUCCESS:
                    Log.d("233","加载成功");
                    break;
                default:
                    super.onManagerConnected(status);
                    Log.d("233","加载失败");
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()){
            Log.d("233","可以");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_10,this,mLoaderCallback);
        }else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

    }

}