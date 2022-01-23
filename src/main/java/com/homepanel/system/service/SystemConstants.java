package com.homepanel.system.service;

public class SystemConstants {

    public enum GROUP {
        MEMORY,
        SWAP,
        STORAGE,
        DRIVE,
        DISPLAY,
        BATTERY,
        CPU,
        SENSORS,
        NETWORK,
        PROCESS
    }

    public enum CHANNEL {
        AVAILABLE,
        TOTAL,
        MEMORY_USED,
        AVAILABLE_PERCENT,
        USED_PERCENT,
        NAME,
        DESCRIPTION,
        TYPE,
        MODEL,
        SERIAL_NUMBER,
        INFORMATION,
        REMAINING_CAPACITY,
        REMAINING_TIME,
        LOAD1,
        LOAD5,
        LOAD15,
        UPTIME,
        CPU_TEMPERATURE,
        CPU_VOLTAGE,
        FAN_SPEED,
        IP_ADDRESS,
        MAC_ADDRESS,
        NETWORK_DISPLAY_NAME,
        NETWORK_NAME,
        PACKETS_SENT,
        PACKETS_RECEIVED,
        DATA_SENT,
        DATA_RECEIVED,
        LOAD,
        THREADS,
        PATH
    }
}