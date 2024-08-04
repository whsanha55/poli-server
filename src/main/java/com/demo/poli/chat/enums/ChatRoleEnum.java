package com.demo.poli.chat.enums;

public enum ChatRoleEnum {

    USER,
    AI,

    SUMMARY,
    SUMMARY_ROOM_NAME,
    ;

    public boolean withoutRoomName() {
        return this != SUMMARY_ROOM_NAME;
    }

}
