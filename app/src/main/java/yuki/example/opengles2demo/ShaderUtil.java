package yuki.example.opengles2demo;

import android.opengl.GLES20;
import android.util.Log;

/**
 * Created by yuki on 2016/5/31.
 */
public class ShaderUtil
{
    private static final String TAG = "ShaderUtil";

    // 检查每一步操作是否有错误的方法
    public static void checkGlError(String operation)
    {
        // error 变量
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR)
        {
            // 后台打印错误
            Log.e("ES20_ERROR", operation + ": glError " + error);
            // 抛出异常
            throw new RuntimeException(operation + ": glError " + error);
        }
    }

    private static int createShader(int shaderType, String shaderSource)
    {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, shaderSource);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader " + shaderType + ":");
                Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    public static int createProgram()
    {
        final String vertexShaderCode =
            "attribute vec4 vPosition;\n" +
            "void main() {\n" +
            "  gl_Position = vPosition;\n" +
            "}\n";

        final String fragmentShaderCode =
            "precision mediump float;\n" +
            "uniform vec4 vColor;\n" +
            "void main() {\n" +
            "  gl_FragColor = vColor;\n" +
            "}\n";

        return createProgram(vertexShaderCode, fragmentShaderCode);
    }

    public static int createProgram(String vertexSource, String fragmentSource)
    {
        int vertexShader = createShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }

        int fragmentShader = createShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (fragmentShader == 0) {
            return 0;
        }

        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            GLES20.glAttachShader(program, fragmentShader);
            checkGlError("glAttachShader");
            GLES20.glLinkProgram(program);
            int[] linked = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linked, 0);
            if (linked[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "Could not link program: ");
                Log.e(TAG, GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }
}
