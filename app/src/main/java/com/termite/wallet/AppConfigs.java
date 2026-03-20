package com.termite.wallet;

public class AppConfigs {
    private static final AppConfigs instance = new AppConfigs();
    private String serverIp = "192.168.220.55";
    private int serverPort = 5009;
    private AppConfigs() {
    }
    public static AppConfigs getInstance() {
        return instance;
    }
    public String getBaseUrl() {
        return "http://" + serverIp + ":" + serverPort + "/api/main/";
    }
}

