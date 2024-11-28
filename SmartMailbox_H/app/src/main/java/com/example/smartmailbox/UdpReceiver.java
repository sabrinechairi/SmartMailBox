package com.example.smartmailbox;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpReceiver implements Runnable {
    private final int port = 377;
    private final byte[] buffer = new byte[1024];
    private final TextView messageTextView;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public UdpReceiver(TextView messageTextView) {
        this.messageTextView = messageTextView;
    }

    @Override
    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(port, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                handler.post(() -> messageTextView.setText("Received message: " + message));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
