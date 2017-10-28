package com.test.csc543.csc543_final;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by KW7359 on 10/24/2017.
 */

public class Snake {
    private int bodyWidth = 70;  //Snake's body segment width
    private int bodyHeight = 50;  //Snake's body segment height
    private int x;  //initial x coordinates
    private int y;  //initial y coordinate

    public int getNextX() {
        return nextX;
    }

    public void setNextX(int nextX) {
        this.nextX = nextX;
    }

    public int getNextY() {
        return nextY;
    }

    public void setNextY(int nextY) {
        this.nextY = nextY;
    }

    private int nextX;  //next x coordinate
    private int nextY;  //next y coordinate
    private Paint paint = new Paint();  // Paint used to draw the snake body
    private SnakeView view;  //view containing the snake
    private Snakebody body;
    private float velocity;  //speed of the snake
    private boolean onScreen;  //is the snake on screen?
    // dimension variables
    private int screenWidth;
    private int screenHeight;

    //constructor
    public Snake(SnakeView view, int x, int y, int bodyWidth, int bodyHeight) {
        this.view = view;
        this.x = x;
        this.y = y;
        this.bodyWidth = bodyWidth;
        this.bodyHeight = bodyHeight;
        paint.setColor(Color.RED);
    }


    public void draw(Canvas canvas) {
        int y = view.getScreenHeight() / 2;
        int x = view.getScreenWidth() / 2;

        //draw initial snake bodyblock
        canvas.drawRect(x, y, x + bodyWidth, y + bodyWidth, paint);
    }

    public Snakebody getSnakebody() {
        return body;
    }

    // returns true if this snake is on the screen
    public boolean isOnScreen() {
        return onScreen;
    }
}
