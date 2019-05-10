package com.muk.sami.model;

import lombok.Getter;

/**
 * Class containing a message that decides the type of notification created in MyFirebaseMessagingService
 */
public class SimpleNotification {

    @Getter
    private String typeOfChange;

    public SimpleNotification() {

    }

    public SimpleNotification(String typeOfChange) {
        this.typeOfChange = typeOfChange;
    }
}
