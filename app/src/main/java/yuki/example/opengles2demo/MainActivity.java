package yuki.example.opengles2demo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends Activity
{
    public static final String TAG = "MainActivity";

    private GLSurfaceView glSurfaceView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // setContentView(R.layout.activity_main);
        initialize();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(glSurfaceView != null) {
            glSurfaceView.onResume();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if(glSurfaceView != null) {
            glSurfaceView.onPause();
        }
    }

    private void initialize()
    {
        if (isSurportGLES20()) {
            glSurfaceView = new GLSurfaceView(this);
            glSurfaceView.setEGLContextClientVersion(2);
            glSurfaceView.setPreserveEGLContextOnPause(true);
            glSurfaceView.setRenderer(new TriangleRenderer());
            setContentView(glSurfaceView);
        } else {
            // Time to get a new phone, OpenGL ES 2.0 not supported.
            AlertDialog.Builder alertDialoBuilderg = new AlertDialog.Builder(this);
            alertDialoBuilderg.setTitle("Error");
            alertDialoBuilderg.setMessage("Error: OpenGL ES 2.0 not supported");
            alertDialoBuilderg.setNegativeButton("关闭", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    MainActivity.this.finish();
                }
            });
            AlertDialog alertDialog = alertDialoBuilderg.create();
            alertDialog.show();
        }
    }

    private boolean isSurportGLES20()
    {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        return configurationInfo.reqGlEsVersion >= 0x20000;
    }
}

abstract class GLRenderer implements GLSurfaceView.Renderer
{
    private boolean mFirstDraw;
    private boolean mSurfaceCreated;
    private int mWidth;
    private int mHeight;
    private long mLastTime;
    private int mFPS;

    public GLRenderer()
    {
        mFirstDraw = true;
        mSurfaceCreated = false;
        mWidth = -1;
        mHeight = -1;
        mLastTime = System.currentTimeMillis();
        mFPS = 0;
    }

    @Override
    public void onSurfaceCreated(GL10 notUsed, EGLConfig config)
    {
        if (Util.DEBUG) {
            Log.i(MainActivity.TAG, "Surface created.");
        }
        mSurfaceCreated = true;
        mWidth = -1;
        mHeight = -1;
    }

    @Override
    public void onSurfaceChanged(GL10 notUsed, int width, int height)
    {
        if (!mSurfaceCreated && width == mWidth && height == mHeight) {
            if (Util.DEBUG) {
                Log.i(MainActivity.TAG, "Surface changed but already handled.");
            }
            return;
        }
        if (Util.DEBUG) {
            // Android honeycomb has an option to keep the
            // context.
            String msg = "Surface changed width:" + width + " height:" + height;
            if (mSurfaceCreated) {
                msg += " context lost.";
            } else {
                msg += ".";
            }
            Log.i(MainActivity.TAG, msg);
        }

        mWidth = width;
        mHeight = height;

        onCreate(mWidth, mHeight, mSurfaceCreated);
        mSurfaceCreated = false;
    }

    @Override
    public void onDrawFrame(GL10 notUsed)
    {
        onDrawFrame(mFirstDraw);

        if (Util.DEBUG) {
            mFPS++;
            long currentTime = System.currentTimeMillis();
            if (currentTime - mLastTime >= 1000) {
                mFPS = 0;
                mLastTime = currentTime;
            }
        }

        if (mFirstDraw) {
            mFirstDraw = false;
        }
    }

    public int getFPS()
    {
        return mFPS;
    }

    public abstract void onCreate(int width, int height, boolean contextLost);

    public abstract void onDrawFrame(boolean firstDraw);
}

class GLES20Renderer extends GLRenderer
{
    @Override
    public void onCreate(int width, int height, boolean contextLost)
    {
        GLES20.glClearColor(1f, 0f, 0f, 1f);
    }

    @Override
    public void onDrawFrame(boolean firstDraw)
    {
        if (Util.DEBUG) {
            Log.d(MainActivity.TAG, "onDrawFrame");
        }
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }
}

class MyGLRender implements GLSurfaceView.Renderer
{
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        // TODO Auto-generated method stub
        GLES20.glClearColor(0.9f, 0.2f, 0.2f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        // TODO Auto-generated method stub
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }
}
