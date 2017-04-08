package social.chat.whatsapp.fb.messenger.messaging;

import java.util.ArrayList;

/**
 * Created by mohak on 24/3/17.
 * Event for passing {@link NotificationModel} arraylist from {@link NotificationReader} to {@link FloatingBubble}
 */

public class postNotificationData {

    ArrayList<NotificationModel> msgs;
    public postNotificationData(ArrayList<NotificationModel> msgs){

        this.msgs=msgs;
    }
}
