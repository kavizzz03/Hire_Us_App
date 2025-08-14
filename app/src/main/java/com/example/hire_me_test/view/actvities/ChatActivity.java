package com.example.hire_me_test.view.actvities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hire_me_test.view.adaptors.ChatAdapter;
import com.example.hire_me_test.R;
import com.example.hire_me_test.UserSession;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    EditText inputMessage;
    Button btnSend;
    ListView listViewMessages;
    ImageButton btnBack;

    ArrayList<String> messagesList;
    ChatAdapter chatAdapter;

    String USER_ID;

    final String START_CHAT_URL = "https://hireme.cpsharetxt.com/start_chat.php";
    final String SEND_URL = "https://hireme.cpsharetxt.com/send_message.php";
    final String GET_URL = "https://hireme.cpsharetxt.com/get_messages.php";

    Handler handler = new Handler();
    Runnable fetchMessagesRunnable;

    int lastMessageCount = 0; // total messages fetched
    int lastSeenMessageCount = 0; // last message user has seen

    private final String CHANNEL_ID = "hire_me_messages_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        USER_ID = UserSession.getUserId(this);

        inputMessage = findViewById(R.id.inputMessage);
        btnSend = findViewById(R.id.btnSend);
        listViewMessages = findViewById(R.id.listViewMessages);
        btnBack = findViewById(R.id.btnBack);

        messagesList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, messagesList);
        listViewMessages.setAdapter(chatAdapter);

        // Back button
        btnBack.setOnClickListener(v -> finish());

        createNotificationChannel();
        startChatSession();

        btnSend.setOnClickListener(v -> {
            String msg = inputMessage.getText().toString().trim();
            if (!TextUtils.isEmpty(msg)) {
                sendMessageToServer("user", msg);
                inputMessage.setText("");
            }
        });

        fetchMessagesRunnable = new Runnable() {
            @Override
            public void run() {
                getMessagesFromServer();
                handler.postDelayed(this, 3000); // fetch messages every 3 seconds
            }
        };
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "HireMe Messages";
            String description = "Notifications for new messages from HireMe";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification(String message) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("user_id", USER_ID);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.icon1) // your app icon
                .setContentTitle("New message from Admin")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(notificationSound)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) notificationManager.notify(1, builder.build());
    }

    private void startChatSession() {
        StringRequest request = new StringRequest(Request.Method.POST, START_CHAT_URL,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("success")) {
                            handler.post(fetchMessagesRunnable); // Start fetching messages
                        } else {
                            Toast.makeText(ChatActivity.this, "Failed to start chat session", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                },
                error -> Toast.makeText(ChatActivity.this, "Start chat failed: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public byte[] getBody() {
                try {
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("user_id", USER_ID);
                    return jsonBody.toString().getBytes("utf-8");
                } catch (Exception e) { return null; }
            }
            @Override
            public String getBodyContentType() { return "application/json; charset=utf-8"; }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void sendMessageToServer(String sender, String message) {
        StringRequest request = new StringRequest(Request.Method.POST, SEND_URL,
                response -> {},
                error -> Toast.makeText(ChatActivity.this, "Send failed: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public byte[] getBody() {
                try {
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("sender", sender);
                    jsonBody.put("message", message);
                    jsonBody.put("user_id", USER_ID);
                    return jsonBody.toString().getBytes("utf-8");
                } catch (Exception e) { return null; }
            }
            @Override
            public String getBodyContentType() { return "application/json; charset=utf-8"; }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void getMessagesFromServer() {
        StringRequest request = new StringRequest(Request.Method.POST, GET_URL,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("success")) {
                            JSONArray messages = jsonResponse.getJSONArray("messages");

                            // Show notification only if admin message is new and unseen
                            for (int i = lastSeenMessageCount; i < messages.length(); i++) {
                                JSONObject msgObj = messages.getJSONObject(i);
                                String sender = msgObj.getString("sender");
                                String msg = msgObj.getString("message");
                                if ("admin".equalsIgnoreCase(sender)) {
                                    showNotification(msg);
                                }
                            }

                            lastMessageCount = messages.length();
                            lastSeenMessageCount = lastMessageCount; // user sees all messages now

                            messagesList.clear();
                            for (int i = 0; i < messages.length(); i++) {
                                JSONObject msgObj = messages.getJSONObject(i);
                                String sender = msgObj.getString("sender");
                                String message = msgObj.getString("message");
                                messagesList.add(sender.toUpperCase() + ": " + message);
                            }
                            chatAdapter.notifyDataSetChanged();
                            listViewMessages.setSelection(messagesList.size() - 1);
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                },
                error -> Toast.makeText(ChatActivity.this, "Fetch failed: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public byte[] getBody() {
                try {
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("user_id", USER_ID);
                    return jsonBody.toString().getBytes("utf-8");
                } catch (Exception e) { return null; }
            }
            @Override
            public String getBodyContentType() { return "application/json; charset=utf-8"; }
        };

        Volley.newRequestQueue(this).add(request);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(fetchMessagesRunnable);
    }
}
