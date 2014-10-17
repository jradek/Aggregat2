package com.jradek.aggregat2;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;


public class SurfaceView extends GLSurfaceView {
    private final SceneRenderer mRenderer;

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;

    public SurfaceView(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // extra
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new SceneRenderer(context);
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        // setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                // reverse direction of rotation above the mid-line
                if (y > getHeight() / 2) {
                    dx = dx * -1;
                }

                // reverse direction of rotation to left of the mid-line
                if (x < getWidth() / 2) {
                    dy = dy * -1;
                }

                final float value = dx + dy;
                queueEvent(new Runnable() {

                    @Override
                    public void run() {
                        float angle = mRenderer.getCameraYAxisRotationDegree();
                        angle += value * TOUCH_SCALE_FACTOR;
                        mRenderer.setCameraYAxisRotationDegree(angle);
                    }
                });
        }

        mPreviousX = x;
        mPreviousY = y;
        return true;
    }
}
