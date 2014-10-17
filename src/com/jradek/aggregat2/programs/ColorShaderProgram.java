package com.jradek.aggregat2.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.jradek.aggregat2.R;

public class ColorShaderProgram extends ShaderProgram {
    // Uniform locations
    private final int uMVPMatrixLocation;
    private final int uColorLocation;

    // Attribute locations
    private final int aPositionLocation;

    public ColorShaderProgram(Context context) {
        super(context, R.raw.simple_vertex_shader, R.raw.simple_fragment_shader);

        // Retrieve uniform locations for the shader program.
        uMVPMatrixLocation = GLES20.glGetUniformLocation(program, U_MVPMATRIX);
        uColorLocation = GLES20.glGetUniformLocation(program, U_COLOR);

        // Retrieve attribute locations for the shader program.
        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
    }

    public void setUniforms(float[] matrix, float r, float g, float b) {
        GLES20.glUniformMatrix4fv(uMVPMatrixLocation, 1, false, matrix, 0);
        GLES20.glUniform4f(uColorLocation, r, g, b, 1f);
    }

    public void setUniforms(float[] matrix, float[] color) {
        GLES20.glUniformMatrix4fv(uMVPMatrixLocation, 1, false, matrix, 0);
        GLES20.glUniform4fv(uColorLocation, 1, color, 0);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }
}
