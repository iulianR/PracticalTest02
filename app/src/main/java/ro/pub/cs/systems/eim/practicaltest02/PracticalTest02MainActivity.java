package ro.pub.cs.systems.eim.practicaltest02;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PracticalTest02MainActivity extends AppCompatActivity {

    private ServerThread serverThread = null;
    private int serverPort = -1;
    private ClientThread clientThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        MyButtonClickListener listener = new MyButtonClickListener();
        ((Button) findViewById(R.id.start)).setOnClickListener(listener);
        ((Button) findViewById(R.id.go)).setOnClickListener(listener);
        ((Button) findViewById(R.id.add)).setOnClickListener(listener);
    }

    @Override
    protected void onDestroy() {
        if (serverThread != null) {
            serverThread.stopServer();
            serverThread = null;
            serverPort = -1;
        }
        super.onDestroy();
    }

    private class MyButtonClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            String port = ((EditText)findViewById(R.id.serverPort)).getText().toString();
            switch (v.getId()) {
                case R.id.start:
                    if (serverThread != null) {
                        Toast.makeText(getApplicationContext(), "Server is already running", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    serverPort = Integer.parseInt(port);
                    serverThread = new ServerThread(serverPort);
                    serverThread.startServer();

                    break;
                case R.id.go:
                    if (serverThread == null || !serverThread.isAlive()) {
                        Log.e(Constants.TAG, "[MAIN ACTIVITY] There is no server to connect to!");
                        return;
                    }

                    String url = ((EditText)findViewById(R.id.url)).getText().toString();
                    if (url == null || url.isEmpty()) {
                        Toast.makeText(
                                getApplicationContext(),
                                "No url!",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    WebView webview = (WebView)findViewById(R.id.webview);
                    clientThread = new ClientThread(
                            "127.0.0.1",
                            Integer.parseInt(port),
                            url,
                            webview);
                    clientThread.start();
                    break;
                case R.id.add:
                    String blacklistURL = ((EditText)findViewById(R.id.blacklistUrl)).getText().toString();
                    serverThread.addURL(blacklistURL);
            }
        }
    }
}
