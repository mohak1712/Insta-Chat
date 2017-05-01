package social.chat.whatsapp.fb.messenger.messaging.Events;

import social.chat.whatsapp.fb.messenger.messaging.FloatingBubble;
import social.chat.whatsapp.fb.messenger.messaging.NotificationReader;
import social.chat.whatsapp.fb.messenger.messaging.Models.NotificationWear;

/**
 * Created by mohak on 24/3/17.
 * Event for passing {@link NotificationWear} object from {@link NotificationReader} to {@link FloatingBubble}
 */

public class postEvent {

    public String receiver;
    public int size;

    public postEvent(String s, int size) {

        this.size = size;
        receiver = s;

    }
}
