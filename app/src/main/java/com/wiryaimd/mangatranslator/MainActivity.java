package com.wiryaimd.mangatranslator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
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
import com.google.mlkit.vision.text.TextRecognizerOptionsInterface;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    // TODO ================================ VV BACA CUKKZ
    // NOTE: harus punya google play services
    // biar bisa download module cv/textrecognition nya, biar ga crash
    // TODO =================================


    private static final String TAG = "MainActivity";

    private Button btn, save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.main_btn);
        save = findViewById(R.id.main_save);

        Log.d(TAG, "onCreate: start process");

        TextRecognizer textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        // options.inJustDecodeBounds = true; // mencegah alokasi memory / ni bitmap ga di simpen di memory dan me return null tapi options width, height nya di set
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.jp2, options);

        Bitmap bitmapFinal = Bitmap.createScaledBitmap(bitmap, options.outWidth, options.outHeight, true);

        // untuk menghindari memory leaks
        // bitmap.recycle();

        Log.d(TAG, "onCreate: size bitmap: " + bitmap.getWidth() + " h: " + bitmap.getHeight());
        Log.d(TAG, "onCreate: size bitmapFinal: " + bitmapFinal.getWidth() + " h: " + bitmapFinal.getHeight());

        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);
        Log.d(TAG, "onCreate: size iimg: " + inputImage.getWidth() + " h: " + inputImage.getHeight());

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
                Task<Text> task = textRecognizer.process(inputImage).addOnCompleteListener(new OnCompleteListener<Text>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Text> task) {
                        String res = task.getResult().getText();

                        Log.d(TAG, "onComplete: boom bitch: " + res);

                        for (Text.TextBlock block : task.getResult().getTextBlocks()) {
                            String blockText = block.getText();
                            Point[] blockCornerPoints = block.getCornerPoints();
                            Rect blockFrame = block.getBoundingBox();
                            Log.d(TAG, "onComplete: blockText: " + blockText);
//                            canvas.drawRect(blockFrame, paint);
                            if (blockFrame != null) {
//                                canvas.drawText(blockText.toUpperCase(), blockFrame.left, blockFrame.top, paintText);
                                Log.d(TAG, "onComplete: top: " + blockFrame.top);
                                Log.d(TAG, "onComplete: bottom: " + blockFrame.bottom);
                                Log.d(TAG, "onComplete: left: " + blockFrame.left);
                                Log.d(TAG, "onComplete: right: " + blockFrame.right);
                                Log.d(TAG, "onComplete: \n");
                            }
                            for (Text.Line line : block.getLines()) {
                                String lineText = line.getText();
                                Log.d(TAG, "onComplete: lineText: " + lineText);
                                Log.d(TAG, "onComplete: \n");
                                Point[] lineCornerPoints = line.getCornerPoints();
                                Rect lineFrame = line.getBoundingBox();
                                canvas.drawRect(lineFrame, paint);

                                if (lineFrame != null){
                                    canvas.drawText(lineText.toUpperCase(), lineFrame.left, lineFrame.top, paintText);
                                }

                                for (Text.Element element : line.getElements()) {
                                    String elementText = element.getText();
                                    Log.d(TAG, "onComplete: elemntText: " + elementText);
                                    Log.d(TAG, "onComplete: \n");
                                    Point[] elementCornerPoints = element.getCornerPoints();
                                    Rect elementFrame = element.getBoundingBox();
                                }

                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Log.d(TAG, "onFailure: lahhh napa si anjg");
                    }
                });
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                save(bitmap, "ahhaahhh");

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
}