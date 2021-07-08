package com.wiryaimd.mangatranslator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    // TODO ================================ VV BACA CUKKZ
    // NOTE: harus punya google play services
    // biar bisa download module cv/textrecognition nya, biar ga crash
    // TODO =================================


    private static final String TAG = "MainActivity";

    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.main_btn);

        Log.d(TAG, "onCreate: start process");

        TextRecognizer textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.testimg3);

        // untuk menghindari memory leaks
        // bitmap.recycle();

        Log.d(TAG, "onCreate: size bitmap: " + bitmap.getWidth() + " h: " + bitmap.getHeight());

        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);
        Log.d(TAG, "onCreate: size iimg: " + inputImage.getWidth() + " h: " + inputImage.getHeight());

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
                            if (blockFrame != null) {
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
    }
}