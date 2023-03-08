package com.warframefarm;

public interface CommunicationHandler {
    int CHECK_FOR_UPDATES = 0, CHECK_FOR_NEW_RELIC_DATA = 1, CHECK_FOR_NEW_USER_DATA = 2, CHECK_FOR_OFFLINE_CHANGES = 3;
    void startAction(Integer actionID);
    void finishAction(Integer actionID);
}
