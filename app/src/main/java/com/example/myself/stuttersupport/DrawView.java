package com.example.myself.stuttersupport;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by Myself on 6/9/2017.
 *
 * Heavily draws upon Sams Teach Yourself Android Game Programming in 24 Hours
 * by Jonathan Harbour
 */

public abstract class DrawView extends SurfaceView implements Runnable{
    private final int FRAME_LIMIT = 50;
    private final int FRAME_DELAY = 50;

    //TODO: Make these make sense
    private final int BUTTON_LEFT = 10;
    private final int BUTTON_TOP = 10;
    private final int BUTTON_RIGHT = 100;
    private final int BUTTON_BOTTOM = 100;

    private volatile boolean running = false;
    private Thread gameloop = null;
    private GameActivity gameActivity;
    private SurfaceHolder surface;
    private Paint whitePaint;
    private Paint yellowPaint;

    protected boolean buttonVisible = false;
    protected int frame = 0;
    protected Drawable background;
    protected Canvas canvas;

    public DrawView(Context context, GameActivity ga) {
        super(context);
        this.gameActivity = ga;
        this.setOnTouchListener(new ButtonListener());
        surface = getHolder();
        setUpPaints();
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
    }

    @Override
    public void run() {
        gameActivity.resetTimer();
        while(running){
            //if surface is not useable, try again
            if (!surface.getSurface().isValid()){
                continue;
            }

            //lock canvas to draw onto it
            canvas = surface.lockCanvas();

            //draw the picture
            doDrawing();

            //draw the start button if appropriate
            if (buttonVisible) drawButton();

            //unlock the canvas
            surface.unlockCanvasAndPost(canvas);

            //advance the frame
            frame++;

            //reset the frame if it's over our limit
            if (frame > FRAME_LIMIT){
                frame = 0;
            }

            //control speed of animation
            try{
                Thread.sleep(FRAME_DELAY);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    protected abstract void doDrawing();

    protected void drawButton(){
        canvas.drawRect(new Rect(BUTTON_LEFT, BUTTON_TOP, BUTTON_RIGHT, BUTTON_BOTTOM),
                yellowPaint);
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public void toggleButton(){
        buttonVisible = !buttonVisible;
    }

    public void setBackgroundImage(Drawable newBg){
        background = newBg;
    }

    private void setUpPaints(){
        whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
        yellowPaint = new Paint();
        yellowPaint.setColor(Color.YELLOW);
    }

    public void pause(){
        running = false;
        while(true){
            try{
                gameloop.join();
                break;
            } catch(InterruptedException e){
                //do nothing
            }
        }
    }

    public void resume(){
        running = true;
        gameloop = new Thread(this);
        gameloop.start();
    }

    private class ButtonListener implements View.OnTouchListener{

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                int touchX = (int)event.getX();
                int touchY = (int)event.getY();

                if(validButtonPress(touchX, touchY)){
                    toggleButton();
                    gameActivity.startButtonPressed();
                    return true;
                }
            }

            return false;
        }

        private boolean validButtonPress(int xCoordinate, int yCoordinate){
            return buttonVisible
                    && xCoordinate >= BUTTON_LEFT
                    && xCoordinate <= BUTTON_RIGHT
                    && yCoordinate >= BUTTON_TOP
                    && yCoordinate <= BUTTON_BOTTOM;
        }
    }
}
