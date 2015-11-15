package com.khirman.housecom;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import org.webrtc.webrtcdemo.MediaEngine;
import org.webrtc.webrtcdemo.NativeWebRtcContextRegistry;

public class MainActivity extends AppCompatActivity {

    private final String TAG="HOUSECOM";

    private NativeWebRtcContextRegistry contextRegistry = null;
    private MediaEngine mediaEngine = null;

    private LinearLayout llSurface;
    private Button  btnCall;

    private boolean inTransmitMode = false;
    private boolean inReceiveMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Global settings.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Hint that voice call audio stream should be used for hardware volume
        // controls.
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

        /*
         * Initialis Multimedia subsystem
         */

        // State.
        // Must be instantiated before MediaEngine.
        contextRegistry = new NativeWebRtcContextRegistry();
        contextRegistry.register(this);

        // Load all settings dictated in xml.
        mediaEngine = new MediaEngine(this);
        mediaEngine.setRemoteIp(getResources().getString(R.string.loopbackIp));
        mediaEngine.setTrace(getResources().getBoolean(R.bool.trace_enabled_default));

//        mediaEngine.setAudio(getResources().getBoolean(
//                R.bool.audio_enabled_default));
        mediaEngine.setReceiveAudio(getResources().getBoolean(
                R.bool.audio_receive_enabled_default));
        mediaEngine.setSendAudio(getResources().getBoolean(
                R.bool.audio_send_enabled_default));

        mediaEngine.setAudioCodec(mediaEngine.getIsacIndex());
        mediaEngine.setAudioRxPort(getResources().getInteger(
                R.integer.aRxPortDefault));
        mediaEngine.setAudioTxPort(getResources().getInteger(
                R.integer.aTxPortDefault));
        mediaEngine.setSpeaker(getResources().getBoolean(
                R.bool.speaker_enabled_default));
        mediaEngine.setDebuging(getResources().getBoolean(
                R.bool.apm_debug_enabled_default));

        mediaEngine.setReceiveVideo(getResources().getBoolean(
                R.bool.video_receive_enabled_default));
        mediaEngine.setSendVideo(getResources().getBoolean(
                R.bool.video_send_enabled_default));
        mediaEngine.setVideoCodec(getResources().getInteger(
                R.integer.video_codec_default));
        // TODO(hellner): resolutions should probably be in the xml as well.
        mediaEngine.setResolutionIndex(MediaEngine.numberOfResolutions() - 2);
        mediaEngine.setVideoTxPort(getResources().getInteger(
                R.integer.vTxPortDefault));
        mediaEngine.setVideoRxPort(getResources().getInteger(
                R.integer.vRxPortDefault));
        mediaEngine.setNack(getResources().getBoolean(R.bool.nack_enabled_default));
        mediaEngine.setViewSelection(getResources().getInteger(
                R.integer.defaultView));


        /*
         * End of Initialisation
         */

        setContentView(R.layout.activity_main);
        llSurface = (LinearLayout) findViewById(R.id.llSurfaceView);
        btnCall = (Button) findViewById(R.id.btnCall);

        // have to be called after setContentView
        // mediaEngine.setViewSelection(getResources().getInteger(R.integer.surfaceView));
        mediaEngine.setRemoteIp(getResources().getString(R.string.multicastIp));


        btnCall.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG, "ACTION_DOWN");
                        startTransmit();
                        return false;
                    //break;
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG, "ACTION_UP");
                        stopTransmit();
                        return false;
                    //break;
                }
                Log.d(TAG, "Touch Action = " + event.getAction());
                return true;
            }
        });






    }

    private void startTransmit() {
        if(inReceiveMode){
            Log.e(TAG,"Attempt to transmit while in receive mode");
            return;
        }
        if(inTransmitMode){
            Log.e(TAG,"Already in transmit mode");
            return;
        }
        // switch into transmit mode
        mediaEngine.setReceiveVideo(false);
        mediaEngine.setSendVideo(true);
        mediaEngine.setReceiveAudio(false);
        mediaEngine.setSendAudio(true);
        // start engine receive
        mediaEngine.start();

        // map view to engine

        SurfaceView localSurfaceView = mediaEngine.getLocalSurfaceView();
        llSurface.addView(localSurfaceView);

        inTransmitMode = true;
        Log.i(TAG,"Transmit mode START");

    }

    private void stopTransmit() {
        if(!inTransmitMode){
            Log.e(TAG,"Not in transmit mode");
            return;
        }

        SurfaceView localSurfaceView = mediaEngine.getLocalSurfaceView();
        llSurface.removeView(localSurfaceView);

        mediaEngine.stop();

        inTransmitMode = false;
        Log.i(TAG, "Transmit mode STOP");
    }

    private void startReceive() {
        if(inTransmitMode){
            Log.e(TAG,"Attempt to receive while in transmit mode");
            return;
        }
        if(inReceiveMode){
            Log.e(TAG,"Already in receive mode");
            return;
        }

        mediaEngine.setReceiveVideo(true);
        mediaEngine.setSendVideo(false);

        mediaEngine.setReceiveAudio(true);
        mediaEngine.setSendAudio(false);
        // start engine receive
        mediaEngine.start();

        // map view to engine

        SurfaceView remoteSurfaceView = mediaEngine.getRemoteSurfaceView();
        llSurface.addView(remoteSurfaceView);

        inReceiveMode = true;
        Log.i(TAG,"Receive mode START");
    }

    private void stopReceive() {

        mediaEngine.stop();

        SurfaceView remoteSurfaceView = mediaEngine.getRemoteSurfaceView();
        llSurface.removeView(remoteSurfaceView);

        inReceiveMode = false;
        Log.i(TAG, "Receive mode STOP");
    }

    @Override
    public void onDestroy() {
        mediaEngine.dispose();
        contextRegistry.unRegister();
        super.onDestroy();
    }
}
