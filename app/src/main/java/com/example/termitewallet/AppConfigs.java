package com.example.termitewallet;

public class AppConfigs {
    private static final AppConfigs instance = new AppConfigs();

    private String serverIp = "192.168.220.56";
    private int serverPort = 5009;

    private AppConfigs() {
    }

    public static AppConfigs getInstance() {
        return instance;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getBaseUrl() {
        return "http://" + serverIp + ":" + serverPort + "/api/main/";
    }
}

