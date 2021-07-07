package com.wiryaimd.mangatranslator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptions;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    // TODO ================================ VV BACA CUKKZ
    // NOTE: harus punya google play services
    // biar bisa download module cv/textrecognition nya, biar ga crash
    // TODO =================================

    private static final String TAG = "MainActivity";

    private Button btn, save;
    private CustomImg customImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.main_btn);
        save = findViewById(R.id.main_save);
        customImg = findViewById(R.id.main_custom);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customImg.setDrawingCacheEnabled(true);
                Bitmap bitmap = customImg.getDrawingCache(true);
                customImg.saveImage(bitmap, "nognthjkolabiezz");
                customImg.destroyDrawingCache();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                String[] perm = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(perm, 0);
                Log.d(TAG, "onCreate: anjing tai bajsagjaksn dkjakont");
            }
        }
    }
}