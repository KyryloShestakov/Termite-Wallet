package com.termite.wallet;

public class AppConfigs {
    private static final AppConfigs instance = new AppConfigs();
    private AppConfigs() {
    }
    public static AppConfigs getInstance() {
        return instance;
    }
    public String getBaseUrl() {
        String serverIp = "192.168.220.55";
        int serverPort = 5009;
        return "http://" + serverIp + ":" + serverPort + "/api/main/";
    }
}

