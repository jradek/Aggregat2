package com.jradek.aggregat2;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.os.SystemClock;

import com.jradek.aggregat2.util.CheckerBoard;
import com.jradek.aggregat2.util.Geometry.Point;

public class SceneRenderer implements Renderer {
    private final Context context;

    private CheckerBoard mCheckerBoard;

    private Pendulum[] mPendel = null;
    private long lastRenderTime = 0;

    float[] fourMatrizes = new float[16 * 4];

    private Camera mCamera;

    public SceneRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        mPendel = new Pendulum[27];

        double T_effect = 30;
        double periods = 20;

        float angleDegree = 30f;
        float yPos = 0f;
        // float zPos = 0f;
        float zPos = ((mPendel.length)) / 2.0f * 0.2f;
        float colorFade = 1.0f;
        for (int i = 0; i < mPendel.length; ++i) {
            double factor = T_effect / periods;
            double length = Constants.GRAVITY * factor * factor;

            mPendel[i] = new Pendulum(this.context, length, angleDegree, yPos, zPos, colorFade);
            periods += 1.0;

            zPos -= 0.2f;
            colorFade -= 0.02f;
        }

        mCheckerBoard = new CheckerBoard(10, 1.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface.
        GLES20.glViewport(0, 0, width, height);

        mCamera = new Camera(width, height);
        //mCamera.setCamera(0, -1.5f, 10.0f, 0, -1.5f, 0f);
        mCamera.setCamera(new Point(-2.0f, -1.5f, 8f), new Point(0, -1.5f, 0f));
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        // Clear the rendering surface.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        mCamera.updateVPMatrix();

        double deltaT = 0.0;
        long now = SystemClock.uptimeMillis();
        if (lastRenderTime == 0) {
            lastRenderTime = now;
        } else {
            deltaT = (now - lastRenderTime) / 1000.0;
            lastRenderTime = now;
        }

        for (int i = 0; i < mPendel.length; ++i) {
            mPendel[i].updateSimulation2(deltaT);
            mPendel[i].draw(mCamera.getVPMatrix(), fourMatrizes);
        }

        mCheckerBoard.draw(mCamera.getVPMatrix());
    }

    public void setCameraYAxisRotationDegree(float value) {
        if (mCamera != null) {
            mCamera.setYAxisRotation(value);
        }
    }

    public float getCameraYAxisRotationDegree() {
        if (mCamera != null) {
            return mCamera.getYAxisRotation();
        }

        return 0f;
    }
}
