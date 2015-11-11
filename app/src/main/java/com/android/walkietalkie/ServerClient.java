package com.android.walkietalkie;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Habeeb Ahmed on 11/11/2015.
 */
public class ServerClient {

    public interface IncomingAudioListener {
        void onIncomingAudio();
    }

    private List<IncomingAudioListener> mIncomingAudioListeners;

    public ServerClient(){
        mIncomingAudioListeners = new ArrayList<IncomingAudioListener>();
    }

    public void registerIncomingAudioListener(IncomingAudioListener listener){
        mIncomingAudioListeners.add(listener);
    }
}
