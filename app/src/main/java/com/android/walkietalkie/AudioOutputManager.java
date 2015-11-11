package com.android.walkietalkie;

/**
 * Created by Habeeb Ahmed on 11/11/2015.
 */
public class AudioOutputManager implements ServerClient.IncomingAudioListener {
    private ServerClient mServerClient;

    public AudioOutputManager(ServerClient serverClient) {
        mServerClient = serverClient;
    }

    // register and incoming audio listener with the mServerClient.
    // as soon as audio is inbound output it to the speakers
    private void registerIncomingAudioListener(){
        mServerClient.registerIncomingAudioListener(this);
    }

    @Override
    public void onIncomingAudio() {

    }
}
