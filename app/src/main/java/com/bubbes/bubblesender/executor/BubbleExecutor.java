package com.bubbes.bubblesender.executor;


import android.app.PendingIntent;
import android.content.Context;
import android.support.annotation.NonNull;
import android.telephony.SmsManager;

import com.bubbes.bubblesender.PhoneEntry;
import com.bubbes.bubblesender.history.HistoryManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class BubbleExecutor {

    private final static ThreadFactory FACTORY = new ThreadFactory() {
        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, "Bubble-sending-thread");
        }
    };

    private final ScheduledExecutorService executorService;
    private final PhoneEntry phoneEntry;
    private final PendingIntent sentMessagesReceiver;
    private Context context;

    public BubbleExecutor(PhoneEntry phoneEntry, PendingIntent sentMessagesReceiver,Context context) {
        this.sentMessagesReceiver = sentMessagesReceiver;
        this.context = context;
        this.executorService = Executors.newSingleThreadScheduledExecutor(FACTORY);
        this.phoneEntry = phoneEntry;
    }

    public void start(SendSpeed speedPolicy) {
        this.executorService.scheduleWithFixedDelay(new BubbleSendingRunnable(), 0, speedPolicy.getDelay(), TimeUnit.MILLISECONDS);
    }

    public void stop() {
        this.executorService.shutdownNow();
    }

    private class BubbleSendingRunnable implements Runnable {
        @Override
        public void run() {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneEntry.getPhone(), null, " ", sentMessagesReceiver, null);
                final HistoryManager instance = HistoryManager.getInstance(context);
                instance.notifySender(phoneEntry);
        }
    }
}
