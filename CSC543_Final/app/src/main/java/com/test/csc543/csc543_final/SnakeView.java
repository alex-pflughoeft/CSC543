package com.test.csc543.csc543_final;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;

import static android.content.Context.SENSOR_SERVICE;
import static com.test.csc543.csc543_final.Snakebody.sensorX;
import static com.test.csc543.csc543_final.Snakebody.sensorY;

/**
 * Created by KW7359 on 10/24/2017.
 */

public class SnakeView extends SurfaceView
        implements SurfaceHolder.Callback, SensorEventListener {

    private static final String TAG = "SnakeView"; // for logging errors

    // constants for the Snake Body
    public static final double SNAKE_RADIUS_PERCENT = 3.0 / 125;
    public static final double SNAKE_SPEED_PERCENT = 3.0 / 30;

    //used for accelerometer
    private SensorManager sensorManager;
    private Sensor accelerometer;

    // text size 1/18 of screen width
    public static final double TEXT_SIZE_PERCENT = 1.0 / 18;

    private SnakeThread snakeThread; // controls the game loop
    private Activity activity; // to display Game Over dialog in GUI thread
    private boolean dialogIsDisplayed = false;

    // game objects
    private SnakeView view;
    private Snakebody snakebody;
    private ArrayList<Snakebody> snakebodies;

    // dimension variables
    private int screenWidth;
    private int screenHeight;

    // variables for the game loop and tracking statistics
    private boolean gameOver; // is the game over?
    private double totalElapsedTime; // elapsed seconds

    // Paint variables used when drawing each item on the screen
    private Paint textPaint; // Paint used to draw text
    private Paint backgroundPaint; // Paint used to clear the drawing area

    // constructor
    public SnakeView(Context context, AttributeSet attrs) {
        super(context, attrs); // call superclass constructor
        activity = (Activity) context; // store reference to MainActivity

        // register SurfaceHolder.Callback listener
        getHolder().addCallback(this);
        textPaint = new Paint();
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.BLACK);

        //Register the accelerometer sensor
        sensorManager = (SensorManager) getContext().getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_GAME);
    }

    // called when the size of the SurfaceView changes,
    // such as when it's first added to the View hierarchy
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        screenWidth = w; // store snakeView's width
        screenHeight = h; // store snakeView's height

        // configure text properties
        textPaint.setTextSize((int) (TEXT_SIZE_PERCENT * screenHeight));
        textPaint.setAntiAlias(true); // smoothes the text
    }

    // get width of the game screen
    public int getScreenWidth() {
        return screenWidth;
    }

    // get height of the game screen
    public int getScreenHeight() {
        return screenHeight;
    }

    // reset all the screen elements and start a new game
    public void newGame() {
        int velocity = (int) (SnakeView.SNAKE_SPEED_PERCENT * getScreenWidth());
        int radius = (int) (SnakeView.SNAKE_RADIUS_PERCENT * getScreenHeight());
        int bodyX;
        int bodyY;
        int segmentNum;
        boolean head;
        int initialSnakeLength = 10;   //# of body segments

        snakebodies = new ArrayList<>();  //construct a new list of Snakebody pieces

        for (int s = 0; s < initialSnakeLength; s++) {
            int color;
            if (s == 0) {
                color = Color.RED;  //make first piece red
                head = true;
            }
            else {
                color = Color.GREEN; //body is green
                head = false;
            }
            segmentNum = s;  //assigns the segment number  to the bodypiece
            bodyX = (getScreenWidth() / 2) - (2 * radius * (s + 1) + s*10);  //adding s*10 to get a little gap between the body segments
            bodyY = (getScreenHeight() / 2) - (2 * radius) + 100;
            snakebodies.add(new Snakebody(this, color, (int) bodyX, (int) bodyY, (int) radius, (int) velocity, head, segmentNum));
        }

        if (gameOver) { // start a new game after the last game ended
            gameOver = false; // the game is not over
            snakeThread = new SnakeThread(getHolder()); // create thread
            snakeThread.start(); // start the game loop thread
        }

        hideSystemBars();
    }

    // called repeatedly by the CannonThread to update game elements
    private void updatePositions(double elapsedTimeMS) {
        double interval = elapsedTimeMS / 1000.0; // convert to seconds

        for (Snakebody snakebody : snakebodies) {
            if (!snakebody.isOnScreen()) {
                snakeThread.setRunning(false);
                //if any snakebody leaves the screen, game over
                showGameOverDialog(R.string.Game_Over);
            } else {

                snakebody.update(interval, 3);
            }
        }
    }


    // draws the game to the given Canvas
    public void drawGameElements(Canvas canvas) {
        // clear the background
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(),
                backgroundPaint);

        for (GameElement snakeBody : snakebodies)
            snakeBody.draw(canvas);
    }

    // checks if the snake collides with itself or the wall
    // and handles the collisions
    public void testForCollisions() {
        //TODO
    }

    // stops the game: called by SnakeGameFragment's onPause method
    public void stopGame() {
        if (snakeThread != null)
            snakeThread.setRunning(false); // tell thread to terminate
    }


    // release resources: called by SnakeGame's onDestroy method
    public void releaseResources() {
        //need to release sound resources if added
    }

    // called when surface changes size
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format,
                               int width, int height) {
    }

    // called when surface is first created
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!dialogIsDisplayed) {
            newGame(); // set up and start a new game
            snakeThread = new SnakeThread(holder); // create thread
            snakeThread.setRunning(true); // start game running
            snakeThread.start(); // start the game loop thread
        }
    }

    // called when the surface is destroyed
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // ensure that thread terminates properly
        boolean retry = true;
        snakeThread.setRunning(false); // terminate snakeThread

        while (retry) {
            try {
                snakeThread.join(); // wait for snakleThread to finish
                retry = false;
            } catch (InterruptedException e) {
                Log.e(TAG, "Thread interrupted", e);
            }
        }
    }

    // Thread subclass to control the game loop
    private class SnakeThread extends Thread {
        private SurfaceHolder surfaceHolder; // for manipulating canvas
        private boolean threadIsRunning = true; // running by default

        // initializes the surface holder
        public SnakeThread(SurfaceHolder holder) {
            surfaceHolder = holder;
            setName("SnakeThread");
        }

        // changes running state
        public void setRunning(boolean running) {
            threadIsRunning = running;
        }

        // controls the game loop
        @Override
        public void run() {
            Canvas canvas = null; // used for drawing
            long previousFrameTime = System.currentTimeMillis();

            while (threadIsRunning) {
                try {
                    // get Canvas for exclusive drawing from this thread
                    canvas = surfaceHolder.lockCanvas(null);

                    // lock the surfaceHolder for drawing
                    synchronized (surfaceHolder) {

                        long currentTime = System.currentTimeMillis();
                        double elapsedTimeMS = currentTime - previousFrameTime;
                        totalElapsedTime += elapsedTimeMS / 1000.0;

                        updatePositions(elapsedTimeMS); // update game state
                        testForCollisions(); // test for GameElement collisions
                        drawGameElements(canvas); // draw using the canvas
                        previousFrameTime = currentTime; // update previous time
                    }
                } finally {
                    // display canvas's contents on the SnakeView
                    // and enable other threads to use the Canvas
                    if (canvas != null)
                        surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    // display an AlertDialog when the game ends
    private void showGameOverDialog(final int messageId) {
        // DialogFragment to display game stats and start new game
        final DialogFragment gameResult =
                new DialogFragment() {
                    // create an AlertDialog and return it
                    @Override
                    public Dialog onCreateDialog(Bundle bundle) {
                        // create dialog displaying String resource for messageId
                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(getActivity());
                        builder.setTitle(getResources().getString(messageId));

                        return builder.create(); // return the AlertDialog
                    }
                };

        // in GUI thread, use FragmentManager to display the DialogFragment
        activity.runOnUiThread(
                new Runnable() {
                    public void run() {
                        showSystemBars();
                        dialogIsDisplayed = true;
                        gameResult.setCancelable(false); // modal dialog
                        gameResult.show(activity.getFragmentManager(), "results");
                    }
                }
        );
    }

    // hide system bars and app bar
    private void hideSystemBars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    // show system bars and app bar
    private void showSystemBars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            sensorX -= (int) event.values[0];  //get position of sensor X position
            sensorY += (int) event.values[1];  //get position of sensor Y position

            //if (Math.abs(sensorX) > 10 || Math.abs(sensorY)> 10)
            //snakeHead.move(sensorX, sensorY);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    // called when the user touches the screen in this activity
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // get int representing the type of action which caused this event
        int action = e.getAction();

        // the user touched the screen or dragged along the screen
        if (action == MotionEvent.ACTION_DOWN ||
                action == MotionEvent.ACTION_MOVE) {
            // moves the snake toward the touch point
            //startSnake(e);
        }
        return true;
    }

    // already on the screen
    public void startSnake(MotionEvent event) {
        // get the location of the touch in this view
        Point touchPoint = new Point((int) event.getX(),
                (int) event.getY());

        //snakebody.startToGrow(touchPoint);
    }

}
