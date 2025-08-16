package com.demo.poli.chat.enums;

public enum ChatRoleEnum {

    USER,
    USER_IMAGE,
    AI,

    SUMMARY,
    SUMMARY_ROOM_NAME,
    ;

    public boolean withoutRoomName() {
        return this != SUMMARY_ROOM_NAME;
    }

}
