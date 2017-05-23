package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerThread extends Thread {
    private int serverPort;
    private ServerSocket serverSocket;
    private ArrayList<String> blacklist = null;

    public ServerThread(int serverPort) {
        this.serverPort = serverPort;

        this.blacklist = new ArrayList<String>();
    }

    public void startServer() {
        Log.i(Constants.TAG, "startServer() was invoked");

        try {
            serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
        }
        start();
    }

    public void stopServer() {
        Log.i(Constants.TAG, "stopServer() was invoked");
        interrupt();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Log.i(Constants.TAG, "[SERVER THREAD] Waiting for a client invocation...");
                Socket socket = this.serverSocket.accept();
                Log.i(Constants.TAG, "[SERVER THREAD] A connection request was received from " + socket.getInetAddress() + ":" + socket.getLocalPort());
                CommunicationThread communicationThread = new CommunicationThread(this, socket);
                communicationThread.start();
            }
        } catch (IOException e) {
            Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public synchronized void addURL(String url) {
        this.blacklist.add(url);
    }

    public synchronized boolean isBlacklisted(String url) {
        return blacklist.contains(url);
    }
}
