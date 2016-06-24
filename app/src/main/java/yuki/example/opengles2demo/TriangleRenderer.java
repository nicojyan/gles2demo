package yuki.example.opengles2demo;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by yuki on 2016/5/30.
 */
public class TriangleRenderer implements GLSurfaceView.Renderer
{
    private static final String TAG = "TriangleRenderer";

    private int mProgram;

    private int mPosition;

    private FloatBuffer mVertexBuffer;

    private void init()
    {
        final String vertexShaderSrc =
            "attribute vec4 vPosition;\n" +
            "void main()\n" +
            "{\n" +
            "   gl_Position = vPosition;\n" +
            "}\n";

        final String fragmentShaderSrc =
            "precision mediump float;\n" +
            "void main()\n" +
            "{\n" +
            "   gl_FragColor = vec4(0.0, 1.0, 1.0, 1.0 );\n" +
            "}\n";

        mProgram = ShaderUtil.createProgram(vertexShaderSrc, fragmentShaderSrc);
        mPosition = GLES20.glGetAttribLocation(mProgram, "vPosition");

        float vertices[] = new float[]{
                +0.0f, +1.0f, +0.0f,
                -1.0f, -1.0f, +0.0f,
                +1.0f, -1.0f, +0.0f
        };

        mVertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);
    }

    private void drawTriangle()
    {
        GLES20.glUseProgram(mProgram);

        // 指定顶点位置数据
        GLES20.glVertexAttribPointer(
                mPosition,
                3,
                GLES20.GL_FLOAT,
                false,
                3 * 4,
                mVertexBuffer
        );
        // 允许顶点位置数据数组
        GLES20.glEnableVertexAttribArray(mPosition);
        // 绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        if (Util.DEBUG) {
            Log.d(TAG, "onSurfaceCreated");
        }
        GLES20.glClearColor(1.0f, 0.0f, 1.0f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        if (Util.DEBUG) {
            Log.d(TAG, "onSurfaceChanged");
        }
        // 设置视窗大小及位置
        GLES20.glViewport(0, 0, width, height);
        // 计算GLSurfaceView的宽高比
        float ratio = (float) width / height;
        // 调用此方法计算产生透视投影矩阵
        // 调用此方法产生摄像机9参数位置矩阵
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        if (Util.DEBUG) {
            Log.d(TAG, "onDrawFrame");
        }
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        drawTriangle();
    }
}
