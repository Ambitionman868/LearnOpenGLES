package com.opengles;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.opengles.renderer.AirHockey2Renderer;
import com.opengles.renderer.AirHockeyRenderer;
import com.opengles.renderer.FirstOpenGLProjectRenderer;
import com.opengles.renderer.TestRenderer;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView mGLSurfaceView;
    //记住GlSurfaceView是否处于有效状态
    private boolean mRendererSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLSurfaceView = new GLSurfaceView(this);
        if (isSupportsEs2()) {
            mGLSurfaceView.setEGLContextClientVersion(2);
            mGLSurfaceView.setRenderer(new AirHockey2Renderer(this));
            mRendererSet = true;
        } else {
            Toast.makeText(this, "不支持OpenGL ES 2.0", Toast.LENGTH_SHORT).show();
            return;
        }
        setContentView(mGLSurfaceView);

    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mRendererSet) {
            mGLSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRendererSet) {
            mGLSurfaceView.onResume();
        }
    }

    private boolean isSupportsEs2() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        return configurationInfo.reqGlEsVersion >= 0x20000;
    }
}
