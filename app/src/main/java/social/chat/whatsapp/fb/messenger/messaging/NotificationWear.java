package social.chat.whatsapp.fb.messenger.messaging;

import android.app.PendingIntent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by mohak on 19/3/17.
 */

public class NotificationWear {

    public ArrayList<PendingIntent> pendingIntent = new ArrayList<>();
    public ArrayList<RemoteInput> remoteInputs = new ArrayList<>();
    public Bundle bundle;

}

