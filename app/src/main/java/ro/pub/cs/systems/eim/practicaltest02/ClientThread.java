package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by master on 5/23/17.
 */

public class ClientThread extends Thread {


    private String address;
    private int port;
    private WebView webview;
    private String url;
    private Socket socket;

    public ClientThread(
            String address,
            int port,
            String url,
            WebView webview) {
        this.address = address;
        this.port = port;
        this.url = url;
        this.webview = webview;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            if (socket == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
            }

            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader != null && printWriter != null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Sending URL: " + url);
                printWriter.println(url);
                printWriter.flush();
                String readData;
                String data = "";
                while ((readData = bufferedReader.readLine()) != null) {
                    data += readData;
                }
                final String allData = data;
                webview.post(new Runnable() {
                    @Override
                    public void run() {
                        webview.loadDataWithBaseURL("", allData, "text/html", "UTF-8", "");
                    }
                });
            } else {
                Log.e(Constants.TAG, "[CLIENT THREAD] BufferedReader / PrintWriter are null!");
            }
            socket.close();
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            ioException.printStackTrace();
        }
    }
}
