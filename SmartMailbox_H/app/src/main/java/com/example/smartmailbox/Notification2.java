package com.example.smartmailbox;

public class Notification2 {

    private String readed;
    private int id;
    private String machineName;
    private String time;

    public Notification2(int id, String machine, String time,String readed) {
        this.id = id;
        this.machineName= machine;
        this.time=time;
        this.readed = readed;
    }

    public String getMachineName() {
        return machineName;
    }

    public String getTime() {
        return time;
    }

    public boolean isReaded() {
        return readed.equals("yes");
    }
}
