package com.android.walkietalkie;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * Created by Habeeb Ahmed on 11/11/2015.
 */
public class AudioReceiverManager implements ServerClient.IncomingAudioListener {
    private ServerClient mServerClient;
    private AudioTrack mAudioTrack;

    public AudioReceiverManager(ServerClient serverClient) {
        mServerClient = serverClient;
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, 800000, AudioTrack.MODE_STREAM);
        registerIncomingAudioListener();
        mAudioTrack.play();
    }

    // register and incoming audio listener with the mServerClient.
    // as soon as audio is inbound output it to the speakers
    private void registerIncomingAudioListener(){
        mServerClient.registerIncomingAudioListener(this);
    }

    @Override
    public void onIncomingAudio(byte[] data) {
        mAudioTrack.write(data, 0, data.length);
    }
}
