package com.bubbes.bubblesender.executor;

public enum SendSpeed {
    NORMAL(5000);
    private final int delay;

    SendSpeed(int delay) {
        this.delay = delay;
    }

    public int getDelay(){
        return this.delay;
    }


}
