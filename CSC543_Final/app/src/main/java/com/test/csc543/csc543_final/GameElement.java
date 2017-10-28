package com.test.csc543.csc543_final;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by KW7359 on 10/24/2017.
 */

class GameElement {
    protected SnakeView view;
    protected Paint paint = new Paint(); //Paint to draw the Game element
    protected Rect shape; //the game elements rectangular bounds
    private float velocity;  //the speed of this game element
    int radius;
    int screenwidth;
    int screenheight;

    // public constructor
    public GameElement(SnakeView view, int color, int x,
                       int y, int width, int height, float velocity) {
        this.view = view;
        paint.setColor(color);
        shape = new Rect(x, y, x + width, y + height); // set bounds
        this.velocity = velocity;
    }

    // update GameElement position and check for wall collisions
    public void update(double interval) {

        // if this GameElement collides with the wall, game over
        if (shape.top < 0
                || shape.bottom > view.getScreenHeight()
                || shape.right > view.getScreenWidth()
                || shape.left < 0 )
            velocity *= -1;  //reverse direction if wall is hit (just for testing)
        //TODO need to add a game stop command here rather than just stop movement (by setting velocity to 0
    }

    // draws this GameElement on the given Canvas
    public void draw(Canvas canvas) {
        canvas.drawRect(shape, paint);
    }
}
