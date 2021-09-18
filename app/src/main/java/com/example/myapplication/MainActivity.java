package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.myapplication.model.Message;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText edtSender, edtReceiver, edtMessage;
    private Button btnJoin, btnSend;
    private RecyclerView rcvChat;
    private ArrayList<Message> mMesseageList;
    private ChatAdapter chatAdapter;
    public static String SenderID = "";

    private Socket mSocket;

    {
        try {
            mSocket = IO.socket("http://192.168.1.64:3000");
        } catch (URISyntaxException e) {
            e.getMessage();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtSender = findViewById(R.id.edtSender);
        edtReceiver = findViewById(R.id.edtReceiver);
        edtMessage = findViewById(R.id.edtMessage);
        btnJoin = findViewById(R.id.btnJoin);
        btnSend = findViewById(R.id.btnSend);
        rcvChat = findViewById(R.id.rcvChat);

        mMesseageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(mMesseageList);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvChat.setLayoutManager(linearLayoutManager);
        rcvChat.setAdapter(chatAdapter);


        mSocket.connect();
        mSocket.on("receiver_message", onNewMessage);

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.emit("login", edtSender.getText().toString());
                SenderID = edtSender.getText().toString();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                Message message = new Message(edtMessage.getText().toString(), edtReceiver.getText().toString());
                mSocket.emit("send_message", gson.toJson(message));

                LoadMessage(edtMessage.getText().toString(), edtReceiver.getText().toString());
            }
        });
    }

    private final Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    LoadMessage(data.optString("content"), data.optString("user"));
                }
            });
        }
    };

    private void LoadMessage(String content, String user) {
        Message message = new Message(content, user);

        mMesseageList.add(message);
        chatAdapter.notifyItemInserted(mMesseageList.indexOf(message));
        rcvChat.smoothScrollToPosition(mMesseageList.size() - 1);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSocket.disconnect();
    }
}