package com.test.csc543.csc543_final;

import android.graphics.Canvas;
import android.graphics.Point;

import java.util.ArrayList;

import static com.test.csc543.csc543_final.R.id.snakeView;

/**
 * Created by KW7359 on 10/24/2017.
 */

public class Snakebody extends GameElement {

    private boolean onScreen;
    private boolean head;  //is this element the head of the snake?
    private float velocity;  //the speed of this game element
    private int segment;
    ArrayList <Integer>  myArray = new ArrayList<>();


    public static int sensorX;  //used for the accelerometer
    public static int sensorY; //used for the accelerometer

    public boolean isHead() {
        return head;
    }

    public Snakebody(SnakeView view, int color, int x, int y, int radius, float velocity, boolean head, int segment)
    {
        super(view, color, x, y, 2 * radius, 2* radius, velocity);
        onScreen = true;
        this.head = head;
        this.velocity = velocity;
        this.segment = segment;

    }

    // get snake's body piece center
    private int getRadius() {
        int diameter = shape.right - shape.left;
        return diameter / 2;
    }

    // returns true if this snake body piece is on the screen
    public boolean isOnScreen() {
        return onScreen;
    }

//    // called repeatedly by the SnakeThread to update game elements
    public void update(double interval, int direction) {
        super.update(interval);  //checks for collisions

        //Section below is temporary just to get the snake movements correct
        radius = shape.right - shape.left;
        screenwidth = view.getScreenWidth();
        screenheight = view.getScreenHeight();

        //if(head == true) {  //only automatically move the head, not the body segments
            if (((int) shape.right > (int) (.9 * screenwidth + radius)) && ((int) shape.top >= (int) (.1 * screenheight - radius))) {
                //if the shape has traveled more than 90% of the way to the right AND isn't 90% to the top of the screen;
                // go up
                direction = 0;

            } else if (shape.top < (int) (.1 * screenheight - radius) && shape.left >= (int) (.1 * screenwidth - radius)) {
                //if the shape has traveled more than 90% of the way up AND isn't all the way to the left already;
                // go left
                direction = 2;
            } else if (shape.left < (int) (.1 * screenwidth - radius) && shape.bottom <= (int) (.9 * screenheight + radius)) {
                //if the shape has traveled more than 90% of the way to the left AND isn't all the way at the bottom already;
                // go down
                direction = 1;
            } else {
                //otherwise go right
                direction = 3;
            }

            switch (direction) {
                case 0: // up
                    shape.offset(0, (int) (velocity * interval) * -1);
                    break;
                case 1: // down
                    shape.offset(0, (int) (velocity * interval));
                    break;
                case 2: // left
                    shape.offset((int) (velocity * interval * -1), 0);
                    break;
                case 3: // right
                    shape.offset((int) (velocity * interval), 0);
                    break;
            }
        //
        // }
        //determine which accelerometer value is biggest
//        if( Math.abs(sensorX) > Math.abs(sensorY))
//            //X tilt is more severe
//            if(sensorX > 0)  //if tilt value is positive, add to x position
//                shape.offset((int) (interval + getRadius()), 0);
//            else  //  //subtract from x position
//                shape.offset((int) (interval - getRadius()), 0);
//        else{
//            //Y tilt is more severe
//            if(sensorY > 0)  //if tilt value is positive, add to Y position
//                shape.offset(0, (int) (interval + getRadius()));
//            else  //  //subtract from x position
//                shape.offset(0, (int) (interval - getRadius()));
//        }

        // if snake goes off the screen
        if (shape.top < 0 || shape.left < 0 ||
                shape.bottom > view.getScreenHeight() ||
                shape.right > view.getScreenWidth())
            onScreen = false; // set it to be removed
    }

/*
    public void move(int x, int y){

        if (Math.abs(sensorX) > Math.abs(sensorY)){
            //if x tilt is greater
            if(y > 1){
                shape.offset(0,-1);
            }
            else{
                shape.offset(0,1);
            }
        }else{
            //if y tilt is greater
            if(x > 1){
                shape.offset(1,0);
            }
            else{
                shape.offset(-1,0);
            }
        }
    }*/

/*    // starts the snake head moving and creates the initial body pieces
    public void startToGrow(Point startingDirection) {
        // Determine which way to move first


        int x = startingDirection.x;
        int y = startingDirection.y;
            if (x > shape.right)
                shape.offset(10, 0);
            else
                shape.offset(-10,0);

            if (y > shape.top)
                    shape.offset(0,10);
            else
                shape.offset(0,-10);

    }*/

    // draws the snakeBody piece
    @Override
    public void draw(Canvas canvas) {

        int radius = getRadius();
        canvas.drawCircle(shape.left + radius,
                shape.top + radius, radius, paint);
    }

}
