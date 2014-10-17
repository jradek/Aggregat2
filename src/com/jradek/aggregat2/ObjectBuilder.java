package com.jradek.aggregat2;

import java.util.ArrayList;
import java.util.List;

import android.opengl.GLES20;

import com.jradek.aggregat2.util.Geometry.Circle;
import com.jradek.aggregat2.util.Geometry.Cylinder;
import com.jradek.aggregat2.util.Geometry.Point;

public class ObjectBuilder {
    private static final int FLOATS_PER_VERTEX = 3;

    static interface DrawCommand {
        void draw();
    }

    public static class GeneratedData {
        final float[] vertexData;
        final List<DrawCommand> drawList;

        GeneratedData(float[] vertexData, List<DrawCommand> drawList) {
            this.vertexData = vertexData;
            this.drawList = drawList;
        }
    }

    static GeneratedData buildPendulum(float ropeLength, float ropeRadius, float massLength,
            float massRadius, int numPoints) {
        final int size =
                sizeOfCircleInVertices(numPoints) * 3 + sizeOfOpenCylinderInVertices(numPoints) * 2;

        ObjectBuilder builder = new ObjectBuilder(size);

        // rope
        Circle baseCircle = new Circle(new Point(0, 0, 0), ropeRadius);
        Cylinder ropeCylinder =
                new Cylinder(baseCircle.center.translateY(-ropeLength / 2f), ropeRadius, ropeLength);

        // mass
        Circle massTopCircle = new Circle(baseCircle.center.translateY(-ropeLength), massRadius);
        Cylinder massCylinder =
                new Cylinder(massTopCircle.center.translateY(-massLength / 2f), massRadius,
                        massLength);
        Circle massBottomCircle =
                new Circle(massTopCircle.center.translateY(-massLength), massRadius);

        // rope
        builder.appendCircle(baseCircle, numPoints);
        builder.appendOpenCylinder(ropeCylinder, numPoints);
        // mass
        builder.appendCircle(massTopCircle, numPoints);
        builder.appendOpenCylinder(massCylinder, numPoints);
        builder.appendCircle(massBottomCircle, numPoints);

        return builder.build();
    }

    private final float[] vertexData;
    private final List<DrawCommand> drawList = new ArrayList<DrawCommand>();
    private int offset = 0;

    private ObjectBuilder(int sizeInVertices) {
        vertexData = new float[sizeInVertices * FLOATS_PER_VERTEX];
    }

    private static int sizeOfCircleInVertices(int numPoints) {
        return 1 + (numPoints + 1);
    }

    private static int sizeOfOpenCylinderInVertices(int numPoints) {
        return (numPoints + 1) * 2;
    }

    private void appendCircle(Circle circle, int numPoints) {
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfCircleInVertices(numPoints);

        // Center point of fan
        vertexData[offset++] = circle.center.x;
        vertexData[offset++] = circle.center.y;
        vertexData[offset++] = circle.center.z;

        // Fan around center point. <= is used because we want to generate
        // the point at the starting angle twice to complete the fan.
        for (int i = 0; i <= numPoints; i++) {
            float angleInRadians = ((float) i / (float) numPoints) * ((float) Math.PI * 2f);

            vertexData[offset++] =
                    circle.center.x + circle.radius * (float) Math.cos(angleInRadians);
            vertexData[offset++] = circle.center.y;
            vertexData[offset++] =
                    circle.center.z + circle.radius * (float) Math.sin(angleInRadians);
        }

        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, startVertex, numVertices);
            }
        });
    }

    private void appendOpenCylinder(Cylinder cylinder, int numPoints) {
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfOpenCylinderInVertices(numPoints);
        final float yStart = cylinder.center.y - (cylinder.height / 2f);
        final float yEnd = cylinder.center.y + (cylinder.height / 2f);

        // Generate strip around center point. <= is used because we want to
        // generate the points at the starting angle twice, to complete the
        // strip.
        for (int i = 0; i <= numPoints; i++) {
            float angleInRadians = ((float) i / (float) numPoints) * ((float) Math.PI * 2f);

            float xPosition =
                    cylinder.center.x + cylinder.radius * (float) Math.cos(angleInRadians);

            float zPosition =
                    cylinder.center.z + cylinder.radius * (float) Math.sin(angleInRadians);

            vertexData[offset++] = xPosition;
            vertexData[offset++] = yStart;
            vertexData[offset++] = zPosition;

            vertexData[offset++] = xPosition;
            vertexData[offset++] = yEnd;
            vertexData[offset++] = zPosition;
        }
        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, startVertex, numVertices);
            }
        });
    }

    private GeneratedData build() {
        return new GeneratedData(vertexData, drawList);
    }
}
