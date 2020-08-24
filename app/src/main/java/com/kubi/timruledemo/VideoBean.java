package com.kubi.timruledemo;

public class VideoBean {
    public long startTime;
    public long endTime;
    public boolean isSos;

    public VideoBean(long startTime, long endTime, boolean isSos) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.isSos = isSos;
    }
}
