package com.khirman.housecom;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import org.webrtc.webrtcdemo.NativeWebRtcContextRegistry;

public class MainActivity extends AppCompatActivity {

    private NativeWebRtcContextRegistry contextRegistry = null;
//    private MediaEngine mediaEngine = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Global settings.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // State.
        // Must be instantiated before MediaEngine.
        contextRegistry = new NativeWebRtcContextRegistry();
        contextRegistry.register(this);



        setContentView(R.layout.activity_main);
    }

    @Override
    public void onDestroy() {
//        disableTimedStartStop();
//        mediaEngine.dispose();
        contextRegistry.unRegister();
        super.onDestroy();
    }
}
