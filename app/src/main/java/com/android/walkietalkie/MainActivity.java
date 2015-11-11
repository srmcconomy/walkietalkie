package com.android.walkietalkie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private View mLobbyScreen;
    private View mPushToTalkScreen;
    private View mLoadingScreen;

    private ServerClient mServerClient;
    private AudioReceiverManager mAudioOutputManager;
    private AudioRecordingManager mAudioRecordingManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mServerClient = new ServerClient();
        mAudioOutputManager = new AudioReceiverManager(mServerClient);
        mAudioRecordingManager = new AudioRecordingManager(mServerClient);

        mLobbyScreen = findViewById(R.id.lobby_screen);
        mPushToTalkScreen = findViewById(R.id.push_to_talk_screen);
        mLoadingScreen = findViewById(R.id.loading_screen);

        showLobbyScreen();
        setClickListeners();
    }

    private void setClickListeners(){
        mLobbyScreen.findViewById(R.id.join_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show dialog of potential peers and let user select one
                // then show the push to talk screen
                Log.d(TAG, "onclick join_btn");
            }
        });

        mLobbyScreen.findViewById(R.id.create_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show dialog of group creation
                // then show the push to talk screen
                Log.d(TAG, "onclick create_btn");

            }
        });

        mPushToTalkScreen.findViewById(R.id.push_to_talk_btn).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // notify server class to start recording and sending
                        Log.d(TAG, "ACTION_DOWN push_to_talk_btn");
                        mAudioRecordingManager.startRecording();
                        break;
                    case MotionEvent.ACTION_UP:
                        // notify server class to stop recording and sending
                        Log.d(TAG, "ACTION_UP push_to_talk_btn");
                        mAudioRecordingManager.stopRecording();
                        break;
                }
                return false;
            }
        });
    }

    private void showLobbyScreen(){
        mLobbyScreen.setVisibility(View.VISIBLE);
        mLoadingScreen.setVisibility(View.GONE);
        mPushToTalkScreen.setVisibility(View.GONE);
    }

    private void showPushToTalkScreen(){
        mLobbyScreen.setVisibility(View.GONE);
        mLoadingScreen.setVisibility(View.GONE);
        mPushToTalkScreen.setVisibility(View.VISIBLE);
    }

    private void showLoadingOverlay(){
        mLoadingScreen.setVisibility(View.VISIBLE);
    }

    private void hideLoadingOverlay(){
        mLoadingScreen.setVisibility(View.GONE);
    }
}
