package com.avarsava.stuttersupport;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * @author Alexis Varsava <av11sl@brocku.ca>
 * @version 1.0
 * @since 0.1
 *
 * Heavily draws upon Sams Teach Yourself Android Game Programming in 24 Hours
 * by Jonathan Harbour. Draws the screen during the game activities, and also manages the run loop,
 * though game logic belongs to the game activity rather than the game screen. Also is responsible
 * for drawing the start button if it has been toggled.
 */

public abstract class DrawView extends SurfaceView implements Runnable{
    /**
     * Used to control the speed of animation.
     */
    private final int FRAME_DELAY = 50;

    /**
     * How many pixels from the left of the screen the Start Button begins.
     */
    private final int BUTTON_LEFT = getScaled(25);

    /**
     * How many pixels from the top of the screen the Start Button begins.
     */
    private final int BUTTON_TOP = getScreenHeight() - getScaled(100);

    /**
     * How many pixels from the right of the screen the Start Button extends to.
     */
    private final int BUTTON_RIGHT = getScreenWidth() - getScaled(25);

    /**
     * How many pixels from the bottom of the screen the Start Button extends to.
     */
    private final int BUTTON_BOTTOM = getScreenHeight() - getScaled(50);

    /**
     * Boolean used to control whether the run loop goes.
     */
    private volatile boolean running = false;

    /**
     * Thread to run the game loop in, to not tie up the main (UI) thread
     */
    private Thread gameloop = null;

    /**
     * Game Activity used to access associated timer methods.
     */
    private GameActivity gameActivity;

    /**
     * Surface on which to draw the canvas.
     */
    private SurfaceHolder surface;

    /**
     * Image of Start Button
     */
    private Drawable startButtonImg;

    /**
     * Boolean used to control whether the Start Button is visible, defaults to false to give
     * the recognizer time to initialize. The game activity will set the button to be visible
     * when the recognizer is ready.
     */
    protected boolean buttonVisible = false;

    /**
     * The current background to draw below the game action.
     */
    protected Drawable background;

    /**
     * Canvas to draw the image to. Once the canvas is completely drawn, it is sent to the Surface
     * to be shown as the screen.
     */
    protected Canvas canvas;

    /**
     * Creates a new DrawView. Sets the View to listen for touch events on the start button and
     * gets the drawing surface from the system. Then initializes the Drawable for drawing the
     * Start Button.
     *
     * @param context The application context
     * @param ga Associated Game Activity, used to access timer methods.
     */
    public DrawView(Context context, GameActivity ga) {
        super(context);
        this.gameActivity = ga;
        this.setOnTouchListener(new ButtonListener());
        surface = getHolder();
        startButtonImg = getResources().getDrawable(R.drawable.ic_start_button);
    }

    /**
     * Defines technically how to draw the screen. Just calls super(), used internally, don't
     * call it yourself.
     *
     * @param canvas Canvas to draw to screen
     */
    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
    }

    /**
     * Puts the game loop in motion. Draws the picture to the canvas, draws the Start Button if
     * necessary, then draws the canvas to the screen.
     */
    @Override
    public void run() {
        gameActivity.resetTimer();
        while(running){
            //if surface is not usable, try again
            if (!surface.getSurface().isValid()){
                continue;
            }

            //lock canvas to draw onto it
            canvas = surface.lockCanvas();

            //draw the background
            if(background != null) {
                background.setBounds(0, 0, getScreenWidth(), getScreenHeight());
                background.draw(canvas);
            }

            //draw the picture
            doDrawing();

            //draw the start button if appropriate
            if (buttonVisible) drawButton();

            //unlock the canvas
            surface.unlockCanvasAndPost(canvas);

            //control speed of animation
            try{
                Thread.sleep(FRAME_DELAY);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Children of DrawView are required to define what the screen should look like for their
     * particular game. This method can also include some game logic, as the drawing loop doubles
     * as the game loop.
     */
    protected abstract void doDrawing();

    /**
     * Draws the start button to the screen.
     */
    protected void drawButton(){
        startButtonImg.setBounds(BUTTON_LEFT, BUTTON_TOP, BUTTON_RIGHT, BUTTON_BOTTOM);
        startButtonImg.draw(canvas);
    }

    /**
     * Gets a value scaled to the density of the device screen.
     *
     * @param i pixel position to scale
     * @return scaled pixel value
     */
    public int getScaled(int i){
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int)(i * scale + 0.5f);
        //return (int)(i*getResources().getDisplayMetrics().scaledDensity);
    }

    /**
     * Gets the height of the screen for whatever device the app is running on.
     *
     * @return height of the device screen in pixels
     */
    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    /**
     * Gets the width of the screen for whatever device the app is running on
     *
     * @return width of the device screen in pixels
     */
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    /**
     * Toggles the visibility of the start button.
     */
    public void toggleButton(){
        buttonVisible = !buttonVisible;
    }

    /**
     * Sets the background image to be drawn below all other objects.
     *
     * @param newBg new Drawable to set as background.
     */
    public void setBackgroundImage(Drawable newBg){
        background = newBg;
    }

    /**
     * Pauses the game loop and waits for a return to the game.
     */
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

    /**
     * On resuming the game, resumes the game loop from the start.
     */
    public void resume(){
        running = true;
        gameloop = new Thread(this);
        gameloop.start();
    }

    /**
     * Listens for screen touch events and reacts if the Start Button is pressed.
     */
    private class ButtonListener implements View.OnTouchListener{

        /**
         * Called when something happens regarding the touch screen.
         * Reacts if there is a touch on the Start Button when it's visible.
         * Not called directly.
         *
         * @param v The View object on which the event took place, not used
         * @param event MotionEvent containing details about the touch screen interaction
         * @return boolean regarding event processing: true if a valid action took place and was
         * handled successfully, false otherwise
         */
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

        /**
         * Calculates whether a press at a particular x,y coordinate is a press of the Start Button
         * based on where it happened and whether the Start Button is actually visible.
         *
         * @param xCoordinate x coordinate of the motion event
         * @param yCoordinate y coordinate of the motion event
         * @return true if a valid press of the Start Button, false otherwise.
         */
        private boolean validButtonPress(int xCoordinate, int yCoordinate){
            return buttonVisible
                    && xCoordinate >= BUTTON_LEFT
                    && xCoordinate <= BUTTON_RIGHT
                    && yCoordinate >= BUTTON_TOP
                    && yCoordinate <= BUTTON_BOTTOM;
        }
    }
}
