package social.chat.whatsapp.fb.messenger.messaging;

/**
 * Created by mohak on 24/3/17.
 * Event for passing {@link NotificationWear} object from {@link NotificationReader} to {@link FloatingBubble}
 */

public class postEvent {

    String receiver;
    int size;

    public postEvent(String s, int size) {

        this.size = size;
        receiver = s;

    }
}
