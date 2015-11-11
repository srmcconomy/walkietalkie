package com.android.walkietalkie;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Habeeb Ahmed on 11/11/2015.
 */
public class ServerClient {
    private static final int BROADCAST_PORT = 5000;

    private Context mContext;
    private Socket mSocket;

    public interface IncomingAudioListener {
        void onIncomingAudio(byte[] data);
    }

    private List<IncomingAudioListener> mIncomingAudioListeners;

    public ServerClient(Context context){
        mContext = context;
        mIncomingAudioListeners = new ArrayList<IncomingAudioListener>();
        mSocket = new DatagramSocket(BROADCAST_PORT);
        socket.setBroadcast(true);
    }

    public void sendAudioByes (byte [] audio) {
        try {
            DatagramPacket packet = new DatagramPacket(audio, audio.length, getBroadcastAddress(), BROADCAST_PORT);
            socket.send(packet);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
