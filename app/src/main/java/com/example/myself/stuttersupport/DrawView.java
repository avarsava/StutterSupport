package com.example.myself.stuttersupport;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Myself on 6/9/2017.
 *
 * Heavily draws upon Sams Teach Yourself Android Game Programming in 24 Hours
 * by Jonathan Harbour
 */

public class DrawView extends SurfaceView implements Runnable{
    private final int FRAME_LIMIT = 10;
    private final int FRAME_DELAY = 10;
    private Thread gameloop = null;
    private SurfaceHolder surface;
    private Paint whitePaint;
    protected Canvas canvas;
    private volatile boolean running = false;
    protected int frame = 0;

    public DrawView(Context context) {
        super(context);
        surface = getHolder();
        whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
    }

    @Override
    public void run() {
        while(running){
            //if surface is not useable, try again
            if (!surface.getSurface().isValid()){
                continue;
            }

            //lock canvas to draw onto it
            canvas = surface.lockCanvas();

            //draw the picture
            doDrawing();

            //unlock the canvas
            surface.unlockCanvasAndPost(canvas);

            //advance the frame
            frame++;

            //reset the frame if it's over our limit
            //TODO: Can advancing the frame and this check be done at the same time?
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

    protected void doDrawing(){
        //Don't hardcode the animation! Override this instead
        //draw black background
        canvas.drawColor(Color.BLACK);
        //Draw white circle of increasing size
        canvas.drawText("OVERRIDE doDrawing()!!", 200f, 200f, whitePaint);
    }

    public void resume(){
        running = true;
        gameloop = new Thread(this);
        gameloop.start();
    }

    public void pause(){
        running = false;
        while(true){
            try{
                gameloop.join();
            } catch(InterruptedException e){
                //do nothing
            }
        }
    }
}
