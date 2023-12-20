package com.codeiy.common.enums;

public enum DeviceType {
    /**
     * pc端
     */
    PC("pc"),

    /**
     * app端
     */
    APP("app");

    private final String device;

    DeviceType(String device) {
        this.device = device;
    }
}
