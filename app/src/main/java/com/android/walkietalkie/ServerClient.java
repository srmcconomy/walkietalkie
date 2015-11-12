package com.android.walkietalkie;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Habeeb Ahmed on 11/11/2015.
 */
public class ServerClient {
    private static final int BROADCAST_PORT = 5000;
    public static final int MAX_BROADCAST_SIZE_BYTES = 65508;

    private Context mContext;
    private DatagramSocket mSocket;

    private List<Byte> mOutputAudioBuffer;

    public interface IncomingAudioListener {
        void onIncomingAudio(byte[] data);
    }

    private List<IncomingAudioListener> mIncomingAudioListeners;

    public ServerClient(Context context){
        mContext = context;
        mOutputAudioBuffer = new ArrayList<Byte>();
        mIncomingAudioListeners = new ArrayList<IncomingAudioListener>();
        try {
            mSocket = new DatagramSocket(BROADCAST_PORT);
            mSocket.setBroadcast(true);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        startSender();
        startReceiver();
    }

    private void startSender() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                // loop infinitely and check if the mOutputAudioBuffer has any data. If so then flush it to the socket
                while(true) {
                    if (mOutputAudioBuffer.size() > MAX_BROADCAST_SIZE_BYTES) {
                        byte [] outgoingBytes = new byte[MAX_BROADCAST_SIZE_BYTES];
                        synchronized (mOutputAudioBuffer) {
                            Iterator<Byte> it = mOutputAudioBuffer.iterator();
                            int count = 0;
                            while (it.hasNext()) {
                                outgoingBytes[count] = it.next();
                                it.remove();
                                count++;
                                if (count == MAX_BROADCAST_SIZE_BYTES) {
                                    break;
                                }
                            }
                        }
                        try {
                            DatagramPacket packet = new DatagramPacket(outgoingBytes, MAX_BROADCAST_SIZE_BYTES, getBroadcastAddress(), BROADCAST_PORT);
                            mSocket.send(packet);
                        } catch (SocketException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.execute();
    }

    private void startReceiver() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                // loop infinitely and check if the mOutputAudioBuffer has any data. If so then flush it to the socket
                while(true) {
                    byte[] buf = new byte[MAX_BROADCAST_SIZE_BYTES];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    try {
                        mSocket.receive(packet);
                        for (IncomingAudioListener listener : mIncomingAudioListeners) {
                            listener.onIncomingAudio(buf);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
    }

    public void sendAudioBytes (byte [] audio) {
        synchronized (mOutputAudioBuffer) {
            for (Byte b : audio) {
                mOutputAudioBuffer.add(b);
            }
        }
    }

    public void registerIncomingAudioListener(IncomingAudioListener listener){
        mIncomingAudioListeners.add(listener);
    }

    InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }



}
