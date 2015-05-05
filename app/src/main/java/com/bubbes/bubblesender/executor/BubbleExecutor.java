package com.bubbes.bubblesender.executor;


import android.app.PendingIntent;
import android.support.annotation.NonNull;
import android.telephony.SmsManager;

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
    private final String phoneNumber;
    private final PendingIntent sentMessagesReceiver;

    public BubbleExecutor(String phoneNumber, PendingIntent sentMessagesReceiver) {
        this.sentMessagesReceiver = sentMessagesReceiver;
        this.executorService = Executors.newSingleThreadScheduledExecutor(FACTORY);
        this.phoneNumber = phoneNumber;
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
                smsManager.sendTextMessage(phoneNumber, null, " ", sentMessagesReceiver, null);
        }
    }
}
