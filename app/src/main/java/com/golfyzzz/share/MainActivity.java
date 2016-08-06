package com.golfyzzz.share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private AppCompatEditText mText;
    private RelativeLayout mImage;
    private File mDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mText = (AppCompatEditText) findViewById(R.id.text);
        mImage = (RelativeLayout) findViewById(R.id.image);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("save","start");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("save","resume");

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("save","save");
                saveImage();
            }
        }, 50);
    }

    private void saveImage() {
        mImage.setDrawingCacheEnabled(true);
        Bitmap bm = Bitmap.createBitmap(
                mImage.getDrawingCache());
        mImage.setDrawingCacheEnabled(false);

        try {
            Date d = new Date();
            String filename  = (String) DateFormat.format("MMddyyyy", d.getTime());
            mDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/Send file/" + filename  + ".png");
            if(!mDir.exists()){
                File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/Send file/");
                directory.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(mDir);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, bos);
            out.write(bos.toByteArray());
            updateImage();
            Toast.makeText(getApplicationContext(), "Save file", Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateImage() {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(mDir));
        sendBroadcast(intent);
    }

    public void sendText(View view) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_TEXT, mText.getText().toString());
        share.setType("text/*");
        startActivity(Intent.createChooser(share, "sent to"));
    }

    public void sentImage(View view) {
        Uri uri = Uri.fromFile(mDir);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(share, "Share to"));
    }
}
