package com.example.PhotoClassification;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

//import com.example.PhotoClassification.R;

import me.nereo.multi_image_selector.*;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE = 2;
    protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    protected static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 102;

    private TextView mResultText;
    private RadioGroup mChoiceMode, mShowCamera;
    private EditText mRequestNum;
    private ImageView icon;
    private ArrayList<String> mSelectPath;
    private Button button;
    private Button detect;
    private Button clear;
    private TextView text;

    private Handler handler = new Handler();

    private AdvancedGeneral advancedGeneral;


    private static final String TAG = "MainActivity";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate" );
        setContentView(R.layout.activity_main);
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        mResultText = (TextView) findViewById(R.id.result);
        detect = findViewById(R.id.detect);
        text = findViewById(R.id.result);
        icon = findViewById(R.id.icon);
        clear = findViewById(R.id.clear);
        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });


        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResultText.setText(advancedGeneral.result);
            }
        });


        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResultText.setText("");
            }
        });
    }


    private void pickImage() {
        Log.e(TAG, "pickImage");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.mis_permission_rationale),
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        }else {

            /**permission request 权限请求
             */
            final int REQUEST_EXTERNAL_STORAGE = 1;
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, REQUEST_STORAGE_READ_ACCESS_PERMISSION);
            } else {
                /**
                 *这里写你要执行的代码
                 */
                //cite lovetuzitong.MultiImageSelector API
                MultiImageSelector selector = MultiImageSelector.create(MainActivity.this);
                selector.showCamera(true);
//                selector.count(1);
                selector.single();
                selector.origin(mSelectPath);
                selector.start(MainActivity.this, REQUEST_IMAGE);
                mResultText.setText("");
            }
        }
        Log.e(TAG, "pickImage: finish" );
    }

    private void requestPermission(final String permission, String rationale, final int requestCode){
        Log.e(TAG, "requestPermission" );
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission)){
            new AlertDialog.Builder(this)
                    .setTitle(R.string.mis_permission_dialog_title)
                    .setMessage(rationale)
                    .setPositiveButton(R.string.mis_permission_dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
                        }
                    })
                    .setNegativeButton(R.string.mis_permission_dialog_cancel, null)
                    .create().show();
        }else{
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.e(TAG, "onRequestPermissionsResult" );
        if(requestCode == REQUEST_STORAGE_READ_ACCESS_PERMISSION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                pickImage();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            Toast.makeText(this,"You denied the permission" , Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult" );
        if(requestCode == REQUEST_IMAGE){
            if(resultCode == RESULT_OK){
                mSelectPath = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                String p = mSelectPath.get(0);
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver()
                            .openInputStream(Uri.fromFile(new File(p))));
                    icon.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                advancedGeneral = new AdvancedGeneral(p,handler);
                advancedGeneral.start();


                Log.e(TAG, "onActivityResult: finished");

            }
        }
    }

    //menu description//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        switch(item.getItemId()){
//            case R.id.action_settings:
//                Toast.makeText(this,"Click setting", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.sign_in:
//                Toast.makeText(this, "Click sign in", Toast.LENGTH_SHORT).show();
//                break;
//            default:
//        }
//        //noinspection SimplifiableIfStatement
//        return true;
//    }



}
