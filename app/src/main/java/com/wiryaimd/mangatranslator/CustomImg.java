package com.wiryaimd.mangatranslator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;

public class CustomImg extends View implements View.OnTouchListener {

    /**
     *
     * Canvas: sebagai alat buat ngegambar
     * Paint: Bagaimana menggambarnya, style nya mau gambar kek gimana
     *
     * Canvas menyediakan frame/canva maybe
     * Paint menentukan warna, gaya, font, dan sebagainya dari setiap bentuk yang Anda gambar.
     *
     */

    private static final String TAG = "CustomImg";

    private Paint paint, paintShadow, paintText;
    private Rect rect;

    private float cx, cy;
    private float radius;

    private Bitmap bitmap;

    public CustomImg(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // untuk bentuk yg lebih kompleks (default: rect, oval, tri)
        Path path = new Path();

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.testimg1);

        // observe kalo layout nya udh ke buat, biar bisa getwidth (width & height nya ga 0)
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // observe nya bisa saja terpanggil beberapa kali
                // oleh karena itu, kita remove observe nya agar jalan sekali
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                bitmap = Bitmap.createScaledBitmap(rawbitmap, getWidth(), getHeight(), true);
//                bitmap = resizeBitmap(rawbitmap, getWidth(), getHeight());
            }
        });

        paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(Color.RED);
        paintText.setTextSize(80);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);

        paintShadow = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintShadow.setColor(Color.GREEN);
        paintShadow.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));

        rect = new Rect(); // start nya di left-top
        rect.left = 80; // posisi left nya
        rect.top = 80; // posisi top
        rect.right = rect.left + 120; // posisi right
        rect.bottom = rect.top + 150; // posisi bottom

        radius = 120;

        setOnTouchListener(this);

        /**
         *         . = LEFT, TOP (20, 25) misal
         *        . * * * * * * *
         *        *             *
         *        *             *
         *        * * * * * * * . = RIGHT, BOTTOM (180, 150)
         *
         */

    }

    public void saveImage(Bitmap finalBitmap, String image_name) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = "Image-" + image_name+ ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        Log.i("LOAD", root + fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Log.d(TAG, "saveImage: crott bozzz");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public Bitmap resizeBitmap(Bitmap bitmap, int viewWidth, int viewHeight){

        Matrix matrix = new Matrix();

        RectF source = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF destination = new RectF(0, 0, viewWidth, viewHeight);
        matrix.setRectToRect(source, destination, Matrix.ScaleToFit.CENTER);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (cx == 0 && cy == 0){
            cx = getWidth() / 2f;
            cy = getHeight() / 2f;
        }

        canvas.drawBitmap(bitmap, 0, 0, null);

        canvas.drawRect(rect, paintShadow);
        canvas.drawCircle(cx, cy, radius, paint);
        canvas.drawText("mengont abiez", 500, 120, paintText);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        boolean val = super.onTouchEvent(motionEvent);

        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onTouch: di pencrot");

                cx = motionEvent.getX();
                cy = motionEvent.getY();
                postInvalidate();

                return true;
            case MotionEvent.ACTION_MOVE:

                float x = motionEvent.getX();
                float y = motionEvent.getY();

                // detect lingkaran nya ke pencet maybe
                double dx = Math.pow(x - cx, 2);
                double dy = Math.pow(y - cy, 2);

                if (dx + dy < Math.pow(radius, 2)){
                    cx = x;
                    cy = y;

                    postInvalidate();

                    return true;
                }

                return val;
        }
        return val;
    }
}
