package com.jradek.aggregat2;

import android.opengl.Matrix;

import com.jradek.aggregat2.util.Geometry.Point;

public class Camera {
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];

    private float yAxisRotationDegree = 0f;

    private Point eye = new Point(0, 0, 1);
    private Point center = new Point(0, 0, 0);

    public Camera(int width, int height) {
        initFrustrum(width, height);
        //float ratio = (float) width / (float)height;
        //perspectiveM(projectionMatrix, 75, ratio, 0.1f, 60f);
        updateVPMatrix();
    }

    private void initFrustrum(int width, int height) {
        float ratio = (float) width / (float)height;

        //TODO: code is taken from "Learn OpenGL ES" page 88
        //and not fully understood

        float zNear = 0.1f;
        float zFar = 1000;
        float fov = 0.75f; // 0.2 to 1.0
        float size = (float) (zNear * Math.tan(fov / 2));
        Matrix.frustumM(projectionMatrix, 0, -size, size, -size / ratio, size / ratio, zNear, zFar);
    }

    /**
     * Defines a projection matrix in terms of a field of view angle, an aspect ratio, and z clip planes.
     *
     * Behaves like Matrix.perspectiveM which is available at API level 14
     *
     * @param m the float array that holds the perspective matrix
     * @param yFovInDegrees field of view in y direction, in degrees
     * @param aspect width to height aspect ratio of the viewport
     * @param near z clip planes
     * @param far z clip planes
     */
    @SuppressWarnings("unused")
    private static void perspectiveM(float[] m, float yFovInDegrees, float aspect, float near, float far) {
        // Description in: "OpenGL ES 2 for Android" page 104
        final float angleInRadians = (float) (yFovInDegrees * Math.PI / 180.0);
        final float a = (float) (1.0 / Math.tan(angleInRadians / 2.0));

        m[0] = a / aspect;
        m[1] = 0f;
        m[2] = 0f;
        m[3] = 0f;

        m[4] = 0f;
        m[5] = a;
        m[6] = 0f;
        m[7] = 0f;

        m[8] = 0f;
        m[9] = 0f;
        m[10] = -((far + near) / (far - near));
        m[11] = -1f;

        m[12] = 0f;
        m[13] = 0f;
        m[14] = -((2f * far * near) / (far - near));
        m[15] = 0f;
    }

    @SuppressWarnings("unused")
    private void initOrtho(int width, int height) {
        float ratio = (float) width / height;
        Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -1, 1, 1, 100);
    }

    public void setCamera(Point eye, Point center) {
        this.eye = eye;
        this.center = center;
    }

    public void setYAxisRotation(float angleDegree) {
        yAxisRotationDegree = angleDegree;
    }

    public float getYAxisRotation() {
        return yAxisRotationDegree;
    }

    public void updateVPMatrix() {
        // rotation around Y axis
        double radius = Math.sqrt(eye.x * eye.x + eye.z * eye.z);
        double angleRad = Math.toRadians(yAxisRotationDegree);
        float x = (float)(radius * Math.sin(angleRad));
        float z = (float)(radius * Math.cos(angleRad));

        // build view
        Matrix.setLookAtM(viewMatrix, 0, -x, eye.y, -z, center.x, center.y, center.z, 0, 1, 0);

        // build view projection
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
    }

    public float[] getVPMatrix() {
        return viewProjectionMatrix;
    }
}
