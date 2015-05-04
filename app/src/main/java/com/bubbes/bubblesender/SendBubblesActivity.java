package com.bubbes.bubblesender;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.bubbes.bubblesender.executor.BubbleExecutor;

public class SendBubblesActivity extends Activity {
    //==============================================================================================
    // Constants
    //==============================================================================================
    public static final String EXTRA_CONTACT_NUMBER = "PhoneNumberKey";
    public static final String STATE_NB_MESSAGE_SENT = "nbMessageSent";

    //==============================================================================================
    // Attributes
    //==============================================================================================
    /**
     * The handler of the main looper
     */
    private Handler handler;
    /**
     * The receiver for getting the feedback of the message
     */
    private SendMessageReceiver sendMessageReceiver;
    /**
     * The number of message which have been sent
     */
    private int nbMessageSent = 0;
    /**
     * The executor which send the messages
     */
    private BubbleExecutor executor;

    //==============================================================================================
    // Androids public
    //==============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.handler = new Handler(Looper.getMainLooper());
        this.sendMessageReceiver = new SendMessageReceiver();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_bubble_activity);
        String phoneNumber = this.getIntent().getStringExtra(EXTRA_CONTACT_NUMBER);

        PendingIntent sentMessagesReceiver = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
        this.registerReceiver(sendMessageReceiver, new IntentFilter("SMS_SENT"));
        this.executor = new BubbleExecutor(phoneNumber, sentMessagesReceiver);
        this.executor.start(SendSpeed.NORMAL);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.unregisterReceiver(this.sendMessageReceiver);
        this.executor.stop();
    }

    //==============================================================================================
    // Inner classes
    //==============================================================================================
    private class SendMessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    TextView viewById = (TextView) findViewById(R.id.nb_message_sent);
                    viewById.setText(String.valueOf(++nbMessageSent));
                }
            });
        }
    }

    //==============================================================================================
    // State save/restore
    //==============================================================================================
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_NB_MESSAGE_SENT, nbMessageSent);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int nbMessageSent = savedInstanceState.getInt(STATE_NB_MESSAGE_SENT);
        this.setSendMessageCount(nbMessageSent);
    }

    //==============================================================================================
    // Private
    //==============================================================================================
    private void setSendMessageCount(int newCount) {
        this.nbMessageSent = newCount;
        TextView viewById = (TextView) findViewById(R.id.nb_message_sent);
        viewById.setText(nbMessageSent);
    }



}
