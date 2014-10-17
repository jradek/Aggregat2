package com.jradek.aggregat2.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.jradek.aggregat2.util.ShaderHelper;
import com.jradek.aggregat2.util.TextResourceReader;

public abstract class ShaderProgram {
    // Uniform constants
    protected static final String U_MVPMATRIX = "u_MVPMatrix";
    protected static final String U_COLOR = "u_Color";

    // Attribute constants
    protected static final String A_POSITION = "a_Position";

    // Shader program
    protected final int program;

    protected ShaderProgram(Context context, int vertexShaderResourceId,
            int fragmentShaderResourceId) {
        // Compile the shaders and link the program.
        program =
                ShaderHelper.buildProgram(TextResourceReader.readTextFileFromResource(context,
                        vertexShaderResourceId), TextResourceReader.readTextFileFromResource(
                        context, fragmentShaderResourceId));
    }

    public void useProgram() {
        // Set the current OpenGL shader program to this program.
        GLES20.glUseProgram(program);
    }
}
