package com.test.csc543.csc543_final;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class keep4reference extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastUpdate;
    private Bitmap bitmap;
    private Canvas snakeCanvas;

    AnimatedView animatedView = null;
    ShapeDrawable mDrawable = new ShapeDrawable();
    public static int x;
    public static int y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lastUpdate = System.currentTimeMillis();

        animatedView = new AnimatedView(this);

        setContentView(animatedView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            x -= (int) event.values[0];
            y += (int) event.values[1];

            Paint myPaint = new Paint();
            myPaint.setColor(0xffffA500);
            myPaint.setStrokeWidth(10);
            snakeCanvas.drawRect(x, y, x + 50, y + 50, myPaint);
        }
    }

    public class AnimatedView extends android.support.v7.widget.AppCompatImageView {

        static final int width = 50;
        static final int height = 50;

        public AnimatedView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
            bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            snakeCanvas = new Canvas(bitmap);
            mDrawable = new ShapeDrawable(new RectShape());
            mDrawable.getPaint().setColor(0xffffA500);
            mDrawable.setBounds(x, y, x + width, y + height);

        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            //mDrawable.setBounds(x, y, x + width, y + height);

            //mDrawable.draw(canvas);

//            Paint myPaint = new Paint();
//            myPaint.setColor(0xffffA500);
//            myPaint.setStrokeWidth(10);
//            canvas.drawRect(x, y, x + width, y + height, myPaint);
            invalidate();  //invalidate means redraw the whole canvas
        }
    }

}
