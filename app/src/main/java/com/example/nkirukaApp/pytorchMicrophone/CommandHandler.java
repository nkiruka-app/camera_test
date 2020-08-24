package com.example.nkirukaApp.pytorchMicrophone;

public interface CommandHandler {
    enum CommandEvent {
        NOTHING,
        TAKE_PICTURE
    }

    public void onCommandEvent(CommandEvent event);
}
