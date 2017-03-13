package social.chat.whatsapp.fb.messenger.messaging;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by mohak on 2/3/17.
 */

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent data) {

        /* Create new notification and start sevice */

        Intent startChat = new Intent(context, FloatingBubble.class);
        startChat.putParcelableArrayListExtra(Constants.msgs, data.getParcelableArrayListExtra(Constants.msgs));
        context.startService(startChat);

    }
}
