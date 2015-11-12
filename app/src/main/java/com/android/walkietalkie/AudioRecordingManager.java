package com.android.walkietalkie;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;

import java.io.IOException;

/**
 * Created by Habeeb Ahmed on 11/11/2015.
 */
public class AudioRecordingManager {
    private static final int SAMPLERATE = 44100;
    private static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private ServerClient mServerClient;
    private Thread mRecordingThread;
    private AudioRecord mRecorder;
    private int mBufferSize;
    private boolean isRecording = false;

    public AudioRecordingManager(ServerClient serverClient) {
        mServerClient = serverClient;
    }

    public void startRecording() {
        mBufferSize = AudioRecord.getMinBufferSize(SAMPLERATE,
                CHANNEL,
                ENCODING);
        mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLERATE, CHANNEL, ENCODING, mBufferSize);
        mRecorder.startRecording();
        isRecording = true;
        mRecordingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                short[] buffer = new short[mBufferSize];
                while(isRecording) {
                    mRecorder.read(buffer, 0, mBufferSize);
                    mServerClient.sendBytes(shortToByte(buffer));
                }
            }
        }, "Audio Recording Thread");
        mRecordingThread.start();
    }

    private byte[] shortToByte(short[] sData) {
        int shortSize = sData.length;
        byte[] bytes = new byte[shortSize * 2];
        for (int i = 0; i < shortSize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;
    }

    public void stopRecording(){
        if (mRecorder != null) {
            isRecording = false;
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            mRecordingThread = null;
        }
    }
}

