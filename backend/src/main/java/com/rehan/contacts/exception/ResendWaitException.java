package com.rehan.contacts.exception;

public class ResendWaitException extends RuntimeException {

    private final int secondsLeft;

    public ResendWaitException(int secondsLeft) {
        super("Wait before asking for a new code");
        this.secondsLeft = secondsLeft;
    }

    public int getSecondsLeft() {
        return secondsLeft;
    }
}
