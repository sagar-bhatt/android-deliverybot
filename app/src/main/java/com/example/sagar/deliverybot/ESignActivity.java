package com.example.sagar.deliverybot;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileOutputStream;

public class ESignActivity extends RootActivity {

    private DrawingView mDrawingView ;
    private Paint mPaint;
    private Button mSignSave, mSignClear;
    private LinearLayout mCanvasViewHolder;
    private Bitmap mBitmap;
    private String jobId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_esign);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_esign, null, false);
        drawer.addView(contentView, 0);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();


        mCanvasViewHolder = (LinearLayout) findViewById(R.id.canvas_holder);
        initializeSignCanvas(mCanvasViewHolder);

        Intent intent = getIntent();
        jobId = intent.getStringExtra("job_id");

        mSignSave = (Button) findViewById(R.id.esign_save);
        mSignClear = (Button) findViewById(R.id.esign_clear);
        mSignSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveESign();
            }
        });
        mSignClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBitmap.eraseColor(Color.WHITE);
            }
        });
    }

    public void initializeSignCanvas(LinearLayout mCanvasHolder){
        mDrawingView = new DrawingView(this);
        mDrawingView.setDrawingCacheEnabled(true);
        mCanvasHolder.addView(mDrawingView);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
    }

    public void saveESign(){
        AlertDialog.Builder editalert = new AlertDialog.Builder(ESignActivity.this);
        editalert.setTitle(getResources().getString(R.string.esign_dialog_text));
        final EditText input = new EditText(ESignActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT);
        input.setLayoutParams(lp);
        editalert.setView(input);
        editalert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String name= input.getText().toString();
                Bitmap bitmap = mDrawingView.getDrawingCache();

                String path = Environment.getExternalStorageDirectory().getPath();
                File file = new File(path + "/Download/" + name + ".png");
                try
                {
                    if(!file.exists())
                    {
                        file.createNewFile();
                    }
                    FileOutputStream ostream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        final Uri contentUri = Uri.fromFile(file);
                        scanIntent.setData(contentUri);
                        sendBroadcast(scanIntent);
                    } else {
                        final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
                        sendBroadcast(intent);
                    }
                    //sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
                    ostream.close();
                    mBitmap.eraseColor(Color.WHITE);
                    mDrawingView.invalidate();
                    firebaseDatabaseReference.child("deliverybot").child("jobs").child(jobId).child("status").setValue("Delivered");
                    finish();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }finally
                {
                    mDrawingView.setDrawingCacheEnabled(false);
                }
            }
        });
        editalert.show();
    }

    public class DrawingView extends View {
        private static final float TOUCH_TOLERANCE = 4;

        private float mX, mY;
        private Canvas mCanvas;
        private Path mPath;
        private Paint mBitmapPaint;
        Context context;
        private Paint circlePaint;
        private Path circlePath;

        public DrawingView(Context c) {
            super(c);
            context = c;
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLUE);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(4f);
        }

        @Override
        protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
            super.onSizeChanged(width, height, oldWidth, oldHeight);

            mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            mCanvas.drawColor(Color.WHITE);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath(mPath, mPaint);
            canvas.drawPath(circlePath, circlePaint);
        }

        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;
                circlePath.reset();
                circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
            }
        }

        private void touch_up() {
            mPath.lineTo(mX, mY);
            circlePath.reset();
            mCanvas.drawPath(mPath, mPaint);
            mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }
}
