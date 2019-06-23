package com.sjm.imagecompression.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.sjm.imagecompression.R;

import java.io.File;

public class MainActivity extends AppCompatActivity {



    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }


    private Uri fileUri;


    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 1;
    private int REQUEST_CAPTURE_IMAGE = 1;//请求状态码


    //读写权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //申请权限
        ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE,REQUEST_PERMISSION_CODE);


    }

    public void onClick_str(View v) {
        int id = v.getId();
        switch (id){
            case R.id.from_camera:   //拍摄
                Intent intent = new Intent(MainActivity.this,StartFromCameraActivity.class);
                startActivity(intent);
                break;
            case R.id.from_album: //打开本地相册
                openAlbum();
                break;
        }
    }






    private void openAlbum() {

        Intent intent = new Intent();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            intent =  new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(Intent.createChooser(intent,"图像选择..."),REQUEST_CAPTURE_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(data == null){
                Log.i("233","data == null:" + data.toString());
                Intent intent = new Intent(getApplicationContext(),StartFromAlbumActivity.class);
                intent.putExtra("PICTURE-URL",fileUri);
                startActivity(intent);

            }else {
                Log.i("233","data != null:" + data.toString());
                Log.i("233","data.getData():" + data.getData());
                Uri uri = data.getData();
                Intent intent = new Intent(getApplicationContext(),StartFromAlbumActivity.class);
                File f = new File(getRealPath(getApplicationContext(),uri));
                intent.putExtra("PICTURE-URL",Uri.fromFile(f));
                startActivity(intent);

            }
        }
    }




    private String getRealPath(Context context,Uri uri) {   //排除版本影响，获取实际的uri
        String filePath = null;
        if (TextUtils.equals("file", uri.getScheme())) {// 小米云相册处理方式
            filePath = uri.getPath();
        }else if(TextUtils.equals("content", uri.getScheme())){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)){ //4.4及以上
                if(DocumentsContract.isDocumentUri(context, uri)) Log.i("233","DocumentsContract.isDocumentUri(context, uri) = "+DocumentsContract.isDocumentUri(context, uri));
                String wholeID = DocumentsContract.getDocumentId(uri);
                String id = wholeID.split(":")[1];
                String[] column = { MediaStore.Images.Media.DATA };
                String sel = MediaStore.Images.Media._ID + "=?";
                Cursor cursor = getApplicationContext().getContentResolver()
                        .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,column,sel,new String[]{id},null);
                int columnIndex = cursor.getColumnIndex(column[0]);
                if(cursor.moveToFirst()){
                    filePath = cursor.getString(columnIndex);
                }
                cursor.close();

            }else { //4.4以下
                String[] projection = { MediaStore.Images.Media.DATA };
                Cursor cursor = getApplicationContext().getContentResolver().query(uri,projection,null,null,null);
                int clumn_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                filePath = cursor.getString(clumn_index);
            }
        }


        Log.i("CV_TAG","selected image path : " + filePath);
        return filePath;

    }

}
