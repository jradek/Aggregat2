package com.jradek.aggregat2;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
    /**
     * Hold a reference to our GLSurfaceView
     */
    private SurfaceView surfaceView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        surfaceView = new SurfaceView(this);
        setContentView(surfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();

        surfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        surfaceView.onResume();
    }
}
