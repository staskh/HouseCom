package com.khirman.housecom;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.LinearLayout;

import org.webrtc.webrtcdemo.MediaEngine;
import org.webrtc.webrtcdemo.NativeWebRtcContextRegistry;

public class MainActivity extends AppCompatActivity {

    private final String TAG="HOUSECOM";

    private NativeWebRtcContextRegistry contextRegistry = null;
    private MediaEngine mediaEngine = null;

    private LinearLayout llRemoteSurface;

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

        mediaEngine.setViewSelection(getResources().getInteger(
                R.integer.surfaceView));
        mediaEngine.setRemoteIp(getResources().getString(R.string.multicastIp));

        mediaEngine.setReceiveVideo(true);
        mediaEngine.setSendVideo(false);

        mediaEngine.setReceiveAudio(true);
        mediaEngine.setSendAudio(false);
        // start engine receive
        mediaEngine.start();

        // map view to engine
        llRemoteSurface = (LinearLayout) findViewById(R.id.llRemoteView);
        SurfaceView remoteSurfaceView = mediaEngine.getRemoteSurfaceView();
        llRemoteSurface.addView(remoteSurfaceView);

        Log.i(TAG,"All Systems Run");
    }

    @Override
    public void onDestroy() {
//        disableTimedStartStop();
        SurfaceView remoteSurfaceView = mediaEngine.getRemoteSurfaceView();
        llRemoteSurface.removeView(remoteSurfaceView);

        mediaEngine.stop();

        mediaEngine.dispose();
        contextRegistry.unRegister();
        super.onDestroy();
    }
}
