package com.bubbes.bubblesender;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bubbes.bubblesender.executor.BubbleExecutor;
import com.bubbes.bubblesender.executor.SendSpeed;
import com.squareup.picasso.Picasso;

public class SendBubblesActivity extends ActionBarActivity {
    //==============================================================================================
    // Constants
    //==============================================================================================
    public static final String EXTRA_PHONE_ENTRY = "PHONE_ENTRY";

    private static final String STATE_NB_MESSAGE_SENT = "nbMessageSent";

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.send_bubble_activity);
        findViewById(R.id.bt_stop_bubbles).getBackground().
                setColorFilter(this.getResources().getColor(R.color.button_stop_background), PorterDuff.Mode.MULTIPLY);

        PhoneEntry phoneEntry = (PhoneEntry) this.getIntent().getSerializableExtra(EXTRA_PHONE_ENTRY);
        ((TextView) findViewById(R.id.contact_name)).setText(phoneEntry.getName());
        ((TextView) findViewById(R.id.contact_phone_number)).setText(phoneEntry.getPhone());
        ((TextView) findViewById(R.id.contact_phone_type)).setText(phoneEntry.getType());
        ImageView imageView = (ImageView) findViewById(R.id.contact_image);
        Picasso.with(this).load(phoneEntry.getImageUri()).placeholder(R.drawable.ic_contact_picture).into(imageView);

        PendingIntent sentMessagesReceiver = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
        this.registerReceiver(this.sendMessageReceiver, new IntentFilter("SMS_SENT"));
        this.executor = new BubbleExecutor(phoneEntry, sentMessagesReceiver,this);
        this.executor.start(SendSpeed.NORMAL);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.unregisterReceiver(this.sendMessageReceiver);
        this.executor.stop();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                    updateMessageCount(nbMessageSent + 1);
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
        this.updateMessageCount(nbMessageSent);
    }

    public void stopBubbleHandler(View view) {
        this.finish();
    }

    //==============================================================================================
    // Private
    //==============================================================================================
    private void updateMessageCount(int newCount) {
        this.nbMessageSent = newCount;
        TextView viewById = (TextView) findViewById(R.id.nb_bubbles_sent);
        viewById.setText(this.nbMessageSent+" messages sent"); //TODO i18n
    }
}
