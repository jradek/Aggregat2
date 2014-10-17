package com.jradek.aggregat2;

import java.util.List;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.jradek.aggregat2.ObjectBuilder.DrawCommand;
import com.jradek.aggregat2.ObjectBuilder.GeneratedData;
import com.jradek.aggregat2.data.VertexArray;
import com.jradek.aggregat2.programs.ColorShaderProgram;

public class Pendulum {
    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;

    float ropeColor[] = {255f / 255f, 219 / 255f, 88 / 255f, 0.0f};
    float massColor[] = {205 / 255f, 127 / 255f, 50 / 255f, 0.0f};

    private final ColorShaderProgram colorProgram;

    private final float[] mvpMatrix = new float[16];

    private final float xPos = 0.0f;
    private float yPos = 0.8f;
    private float zPos = 0.0f;

    // simulation
    private double initialAngleRad = 0.0;
    private double angleRad = Math.PI / 2.0;
    private double length = 1.6;

    private double time = 0.0;

    // draw data
    private final VertexArray vertexArray;
    private final List<DrawCommand> drawList;

    public Pendulum(Context context, double pLength, double angleDegree, float pyPos, float pzPos,
            float colorFade) {
        this.length = pLength;
        this.angleRad = angleDegree * Math.PI / 180.0;
        this.initialAngleRad = this.angleRad;
        this.yPos = pyPos;
        this.zPos = pzPos;

        { // shape calculation
            final float shapeLength = (float) pLength / 10.0f;
            final float massLength = 0.15f;
            final float massRadius = 0.08f;

            final float ropeLength = shapeLength - massLength;
            final float ropeRadius = massRadius / 4f;

            int numPoints = 30;

            GeneratedData data =
                    ObjectBuilder.buildPendulum(ropeLength, ropeRadius, massLength, massRadius,
                            numPoints);

            vertexArray = new VertexArray(data.vertexData);
            drawList = data.drawList;
        }

        this.colorProgram = new ColorShaderProgram(context);
    }

    public void updateSimulation2(double deltaT) {
        time += deltaT;
        //double arg = Math.sqrt(GRAVITY / this.length) * time;
        double arg = Math.sqrt(Constants.GRAVITY / this.length) * 2 * Math.PI * time;
        // start at highest position
        arg += Math.PI / 2;
        this.angleRad = this.initialAngleRad * Math.sin(arg);
    }

    public float getRotationAngleDegree() {
        double arg = this.angleRad * 180.0 / Math.PI;
        return (float) (arg);
    }

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     *
     * @param mvpMatrix - The Model View Project matrix in which to draw this shape.
     */
    public void draw(float[] vpMatrix, float[] fourMatrizes) {
        Matrix.setRotateM(fourMatrizes, 0, getRotationAngleDegree(), 0, 0, 1.0f);
        Matrix.setIdentityM(fourMatrizes, 16);
        Matrix.translateM(fourMatrizes, 16, xPos, yPos, zPos);

        Matrix.multiplyMM(fourMatrizes, 32, fourMatrizes, 16, fourMatrizes, 0);
        Matrix.multiplyMM(this.mvpMatrix, 0, vpMatrix, 0, fourMatrizes, 32);

        colorProgram.useProgram();

        vertexArray.setVertexAttribPointer(0,
                colorProgram.getPositionAttributeLocation(),
                COORDS_PER_VERTEX, 0);

        GLES20.glEnableVertexAttribArray(colorProgram.getPositionAttributeLocation());

        colorProgram.setUniforms(this.mvpMatrix, ropeColor);

        final int l = drawList.size();
        for (int i = 0; i < l; ++i) {

            // TODO: implicit knowledge of draw structure
            if (i == 2) {
                // switch to mass
                colorProgram.setUniforms(this.mvpMatrix, massColor);
            }
            drawList.get(i).draw();
        }

        GLES20.glDisableVertexAttribArray(colorProgram.getPositionAttributeLocation());
    }
}
