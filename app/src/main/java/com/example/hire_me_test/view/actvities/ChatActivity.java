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

    String USER_ID; // effective ID for chat

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

        inputMessage = findViewById(R.id.inputMessage);
        btnSend = findViewById(R.id.btnSend);
        listViewMessages = findViewById(R.id.listViewMessages);
        btnBack = findViewById(R.id.btnBack);

        messagesList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, messagesList);
        listViewMessages.setAdapter(chatAdapter);

        btnBack.setOnClickListener(v -> finish());

        // Determine effective ID
        USER_ID = getEffectiveId();

        createNotificationChannel();

        // Start chat session
        startChatSession();

        // Fetch messages and mark existing as seen
        markMessagesAsSeen();

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
                handler.postDelayed(this, 3000); // fetch every 3 seconds
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Activity is in foreground
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Activity is paused
    }

    /** Get effective ID: id_number > employer_id > session */
    private String getEffectiveId() {
        String id = getIntent().getStringExtra("id_number");
        if (!TextUtils.isEmpty(id)) return id;

        id = getIntent().getStringExtra("employer_id");
        if (!TextUtils.isEmpty(id)) return id;

        return UserSession.getUserId(this); // fallback to session ID
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
        intent.putExtra("id_number", USER_ID);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.icon1)
                .setContentTitle("New message from Admin")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(notificationSound)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void startChatSession() {
        StringRequest request = new StringRequest(Request.Method.POST, START_CHAT_URL,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("success")) {
                            handler.post(fetchMessagesRunnable);
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

    /** Notify user if a new message arrives */
    private void notifyIfNewMessage(JSONObject msgObj, int index) {
        try {
            String sender = msgObj.getString("sender");
            String message = msgObj.getString("message");
            // Notify if admin and message is beyond last seen
            if ("admin".equalsIgnoreCase(sender) && index >= lastSeenMessageCount) {
                showNotification(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getMessagesFromServer() {
        StringRequest request = new StringRequest(Request.Method.POST, GET_URL,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("success")) {
                            JSONArray messages = jsonResponse.getJSONArray("messages");

                            for (int i = lastSeenMessageCount; i < messages.length(); i++) {
                                JSONObject msgObj = messages.getJSONObject(i);
                                notifyIfNewMessage(msgObj, i); // always notify
                            }

                            lastMessageCount = messages.length();

                            messagesList.clear();
                            for (int i = 0; i < messages.length(); i++) {
                                JSONObject msgObj = messages.getJSONObject(i);
                                String sender = msgObj.getString("sender");
                                String message = msgObj.getString("message");
                                messagesList.add(sender.toUpperCase() + ": " + message);
                            }
                            chatAdapter.notifyDataSetChanged();
                            listViewMessages.setSelection(messagesList.size() - 1);

                            // Update last seen if activity is open
                            lastSeenMessageCount = lastMessageCount;

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

    /** Mark all existing messages as seen on activity start */
    private void markMessagesAsSeen() {
        StringRequest request = new StringRequest(Request.Method.POST, GET_URL,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("success")) {
                            JSONArray messages = jsonResponse.getJSONArray("messages");
                            lastSeenMessageCount = messages.length();
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                },
                error -> { /* ignore */ }
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
