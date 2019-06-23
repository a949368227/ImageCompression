package com.sjm.imagecompression.activity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.sjm.imagecompression.R;
import com.sjm.imagecompression.units.Comperssion;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import static org.opencv.imgproc.Imgproc.resize;

public class StartFromAlbumActivity extends AppCompatActivity implements View.OnClickListener {

    private Uri fileUri;



    private DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); //使用时间戳作为文件名

    private String fileName = format.format(new Date())+".JPEG";

    private AlertDialog alertDialog;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_from_album);
        this.findViewById(R.id.str_btn);
        imageView = (ImageView)this.findViewById(R.id.imageView);


        fileUri = (Uri)this.getIntent().getParcelableExtra("PICTURE-URL");
        if(fileUri != null){
            displaySelectedImage(fileUri);
        }
    }
    private void displaySelectedImage(Uri bitmapUri) { //展示已选图片
        imageView.setImageURI(bitmapUri);

    }


    //操作选择框
    public void showSingleAlertDialog(View view){

        final String[] items = {"顺时针旋转90°并保存(附带90%质量压缩)",this.getString(R.string.openCvComperssion),this.getString(R.string.qualityComperssion),this.getString(R.string._openCvComperssion),this.getString(R.string._qualityComperssion)};
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle(this.getString(R.string.comperssionTitle));

        alertBuilder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //压缩操作
                final Bitmap bitmap;
                if (fileUri!=null){
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);
                        Bitmap bm;
                        switch (i){
                            case 0:
                                bm = Comperssion.rotateImage(bitmap,90);
                                Comperssion.saveBitmap(bm,fileName,StartFromAlbumActivity.this);
                                Toast.makeText(StartFromAlbumActivity.this,"保存成功！",Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Comperssion.openCvComperssion(bitmap,fileName,StartFromAlbumActivity.this);
                                break;
                            case 2:
                                Comperssion.qualityComperssion(bitmap,fileName,StartFromAlbumActivity.this);
                                break;
                            case 3:
                                Comperssion.openCvComperssionSetter(bitmap,fileName,StartFromAlbumActivity.this);
                                break;
                            case 4:
                                Comperssion.qualityComperssionSetter(bitmap,fileName,StartFromAlbumActivity.this);
                                break;
                        }

                        } catch (FileNotFoundException e) {
                        e.printStackTrace();

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
