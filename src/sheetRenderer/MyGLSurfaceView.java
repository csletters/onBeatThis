package sheetRenderer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyGLSurfaceView extends GLSurfaceView {
	
	
	public MyRenderer renderer;
	private GestureDetector mGestureDetector;
	
    public MyGLSurfaceView(Context context){
        super(context);

        // Set the Renderer for drawing on the GLSurfaceView
        setEGLContextClientVersion(2);
        renderer = new MyRenderer(context);
        setRenderer(renderer);
        //gesture listener
        mGestureDetector = new GestureDetector(context, new GestureListener());

    }
    public void stuff()
    {}
    
    private class GestureListener extends  GestureDetector.SimpleOnGestureListener
   	{
       	/*private static final int SWIPE_THRESHOLD = 10;
           private static final int SWIPE_VELOCITY_THRESHOLD = 10;
           float xVelocity;
           float diffX;
           float diffY;
      
       	  @Override
             public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                 boolean result = false;
                 xVelocity = velocityX;
                 try {
                     diffY = e2.getY() - e1.getY();
                     diffX = e2.getX() - e1.getX();
                     if (Math.abs(diffX) > Math.abs(diffY)) {
                         if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                             if (diffX > 0) {
                                 onSwipeRight();
                                 requestRender();
                             } else {
                                 onSwipeLeft();
                                 requestRender();
                             }
                         }
                     }
                     else
                     {
                   	  if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                             if (diffY > 0) {
                           	  onSwipeBottom();
                           	  requestRender();
                             } else {
                           	  onSwipeTop();
                           	  requestRender();
                             }
                         }
                     }
                 } catch (Exception exception) {
                     exception.printStackTrace();
                 }
                 return result;
             }*/
             
       	@Override  
       	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
       	{
       		float displacementX =(float)  (e1.getX() - e2.getX())*1.5f;
       		float displacementY = (float) (e2.getY() - e1.getY())*1.5f;
   			return true;
       		
       	}
       	  
       	// event when double tap occurs
           @Override
           public boolean onDoubleTapEvent(MotionEvent e) {

           	if(e.getAction() == e.ACTION_UP)
           	{
           		//renderer.mapTapped(e.getX(),e.getY());
           	}

                 return true;
             }
       	
           @Override
           public void onLongPress(MotionEvent e) {

           }
           
           @Override
       	public boolean onSingleTapUp(MotionEvent e)
           {
           	return true;
           }
       	/*@Override
           public boolean onDown(MotionEvent e) {
               return true;
           }
       	public void onSwipeRight() {
       		renderer.updateCameraX(Math.abs(diffX));
           }

           public void onSwipeLeft() {
           	renderer.updateCameraX(-Math.abs(diffX));
           }

           public void onSwipeTop() {
           	renderer.updateCameraY(Math.abs(diffY));
           }

           public void onSwipeBottom() {
           	renderer.updateCameraY(-Math.abs(diffY));
           }*/
           
           @Override
           public boolean onDown(MotionEvent e) {
               return true;
           }
   	}
       
       
       @Override
       public boolean onTouchEvent(MotionEvent e) {

       	
       	if(mGestureDetector.onTouchEvent(e))	
       		return true;
       	 if(e.getAction() == MotionEvent.ACTION_UP)
       	 {

       	 }
       	return false;
       }
}
