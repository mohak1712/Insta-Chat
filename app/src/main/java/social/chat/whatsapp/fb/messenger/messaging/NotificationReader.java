package social.chat.whatsapp.fb.messenger.messaging;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by mohak on 28/2/17.
 */

public class NotificationReader extends NotificationListenerService {

    /**
     * Store notifications in a list
     */
    ArrayList<NotificationModel> msgs;

    /**
     * split content to get username and the text received
     */
    String[] msgBifercator;


    /**
     * clear duplicate 1st element from list
     */
    private boolean clearDuplicate;


    private HashMap<String, ArrayList<NotificationModel>> dataSet = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Lines", "Created");
        msgs = new ArrayList<>();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        clearDuplicate = false;

        if (!sbn.getPackageName().equals("com.whatsapp"))
            return;

        Log.d("Lines", "called ji");
        Bundle extras = sbn.getNotification().extras;
        CharSequence[] lines = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);


        if (lines != null) {

            /* app is already running and new notification pops up */

            if (msgs.size() != 0) {

                if (msgs.size() >= 7) {

                    msgBifercator = lines[lines.length - 1].toString().split(":");
                    NotificationModel model = new NotificationModel();
                    model.setUserName(msgBifercator[0].trim());
                    model.setMsg(msgBifercator[1].trim());
                    model.setTime(sbn.getPostTime());
                    Log.d("Lines", " 7 " + model.getUserName() + " : " + model.getMsg());
                    msgs.add(model);

                } else {

                    for (int i = msgs.size(); i < lines.length; i++) {

                        msgBifercator = lines[i].toString().split(":");
                        NotificationModel model = new NotificationModel();
                        model.setUserName(msgBifercator[0].trim());
                        model.setMsg(msgBifercator[1].trim());
                        model.setTime(sbn.getPostTime());
                        Log.d("Lines", " 1 " + model.getUserName() + " : " + model.getMsg());
                        msgs.add(model);
                    }

                }

            } else {

                /* build notification list from scratch */

                for (CharSequence singleLine : lines) {

                    msgBifercator = singleLine.toString().split(":");
                    NotificationModel model = new NotificationModel();
                    model.setUserName(msgBifercator[0].trim());
                    model.setMsg(msgBifercator[1].trim());
                    model.setTime(sbn.getPostTime());
                    Log.d("Lines", " 0 " + model.getUserName() + " : " + model.getMsg());
                    msgs.add(model);
                }

            }

        }

//        if (extras.containsKey(Notification.EXTRA_PICTURE)) {
//            // this nf contain the picture attachment
//            Bitmap bmp = (Bitmap) extras.get(Notification.EXTRA_PICTURE);
//            Log.d("Lines", "true");
//        }

        Log.d("Lines", "m s " + msgs.size());
        Intent intent = new Intent(Constants.action);
        intent.putParcelableArrayListExtra(Constants.msgs, msgs);
        sendBroadcast(intent);

    }


    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        msgs.clear();
    }

}