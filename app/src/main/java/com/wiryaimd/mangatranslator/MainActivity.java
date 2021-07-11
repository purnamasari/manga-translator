package com.wiryaimd.mangatranslator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptions;
import com.google.mlkit.vision.text.TextRecognizerOptionsInterface;
import com.googlecode.tesseract.android.TessBaseAPI;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    // TODO ================================ VV BACA CUKKZ
    // NOTE: harus punya google play services
    // biar bisa download module cv/textrecognition nya, biar ga crash
    // TODO =================================


    private static final String TAG = "MainActivity";

    private Button btn, save;

    private TesseractOCR tesseractOCR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.main_btn);
        save = findViewById(R.id.main_save);

        tesseractOCR = new TesseractOCR(MainActivity.this, "jpn");

        Log.d(TAG, "onCreate: start process");

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        // options.inJustDecodeBounds = true; // mencegah alokasi memory / ni bitmap ga di simpen di memory dan me return null tapi options width, height nya di set
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.asdasdas2, options);

        Bitmap bitmapFinal = Bitmap.createScaledBitmap(bitmap, options.outWidth, options.outHeight, true);

        // untuk menghindari memory leaks
        // bitmap.recycle();

        Log.d(TAG, "onCreate: size bitmap: " + bitmap.getWidth() + " h: " + bitmap.getHeight());
        Log.d(TAG, "onCreate: size bitmapFinal: " + bitmapFinal.getWidth() + " h: " + bitmapFinal.getHeight());

        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);

        Paint paintText = new Paint();
        paint.setColor(Color.RED);
        paintText.setTextSize(40);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                String[] perm = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(perm, 1);
            }
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tesseractOCR.doOCR(bitmap, canvas, paint);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                save(bitmap, "ahhaassshhh");

            }
        });
    }

    public void save(Bitmap bitmap, String image_name){

        File dir = new File(Environment.getExternalStorageDirectory().toString());
        dir.mkdirs();
        String filename = "image-" + image_name + ".jpg";
        File file = new File(dir, filename);
        if (file.exists()){
            file.delete();
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tesseractOCR.onDestroy();
    }
}