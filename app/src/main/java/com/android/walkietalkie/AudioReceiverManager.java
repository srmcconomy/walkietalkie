package com.android.walkietalkie;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Habeeb Ahmed on 11/11/2015.
 */
public class AudioReceiverManager implements ServerClient.IncomingAudioListener {
    private ServerClient mServerClient;
    private AudioTrack mAudioTrack;
    private List<Byte> mReceievedAudioBuffer;
    private static final int AUDIO_TRACK_BUFFER_SIZE = 80000;

    public AudioReceiverManager(ServerClient serverClient) {
        mServerClient = serverClient;
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, AUDIO_TRACK_BUFFER_SIZE, AudioTrack.MODE_STREAM);
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
        for(int i = 0; i < data.length; i++) {
            mReceievedAudioBuffer.add(data[i]);
        }
    }

    private void startSender() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                // loop infinitely and check if the mOutputAudioBuffer has any data. If so then flush it to the socket
                while(true) {
                    if (mReceievedAudioBuffer.size() > 0) {
                        int min = mReceievedAudioBuffer.size() < AUDIO_TRACK_BUFFER_SIZE/5 ? mReceievedAudioBuffer.size() : AUDIO_TRACK_BUFFER_SIZE/5;
                        byte [] buf = new byte[min];
                        synchronized (mReceievedAudioBuffer) {
                            for(int i = 0; i < min; i++) {
                                buf[i] = mReceievedAudioBuffer.get(0);
                                mReceievedAudioBuffer.remove(0);
                            }
                        }
                        mAudioTrack.write(buf, 0, buf.length);
                    }
                }
            }
        }.execute();
    }

}
