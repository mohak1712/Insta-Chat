package social.chat.whatsapp.fb.messenger.messaging.Events;

import java.util.ArrayList;

import social.chat.whatsapp.fb.messenger.messaging.FloatingBubble;
import social.chat.whatsapp.fb.messenger.messaging.Models.NotificationModel;
import social.chat.whatsapp.fb.messenger.messaging.NotificationReader;

/**
 * Created by mohak on 24/3/17.
 * Event for passing {@link NotificationModel} arraylist from {@link NotificationReader} to {@link FloatingBubble}
 */

public class postNotificationData {

    public ArrayList<NotificationModel> msgs;
    public postNotificationData(ArrayList<NotificationModel> msgs){

        this.msgs=msgs;
    }
}
