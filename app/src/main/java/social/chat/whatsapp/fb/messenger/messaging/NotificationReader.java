package social.chat.whatsapp.fb.messenger.messaging;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
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
    ArrayList<NotificationModel> msgs = new ArrayList<>();

    /**
     * split content to get username and the text received
     */
    String[] msgBifercator;


    /**
     * clear duplicate 1st element from list
     */
    private boolean clearDuplicate = false;
    private HashMap<String, ArrayList<NotificationModel>> dataSet = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();

//        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
//        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//        DisplayMetrics displaymetrics = new DisplayMetrics();
//        windowManager.getDefaultDisplay().getMetrics(displaymetrics);
//        heightOfDev = displaymetrics.heightPixels;
//        widthOfDev = displaymetrics.widthPixels;
//
//        addRemoveView();
//        addBubbleView();
    }


//    private void addRemoveView() {
//
//        removeView = (LinearLayout) inflater.inflate(R.layout.remove_bubble, null);
//        removeBubble = (ImageView) removeView.findViewById(R.id.removeImg);
//        WindowManager.LayoutParams paramRemove = new WindowManager.LayoutParams(
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.TYPE_PHONE,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                PixelFormat.TRANSLUCENT);
//        paramRemove.gravity = Gravity.TOP | Gravity.LEFT;
//        removeView.setVisibility(View.GONE);
//        windowManager.addView(removeView, paramRemove);
//    }


//    private void addBubbleView() {
//
//        bubbleView = (LinearLayout) inflater.inflate(R.layout.bubble_layout, null);
//        bubble = (ImageView) bubbleView.findViewById(R.id.bubble);
//        bubble.setImageResource(R.drawable.circle_cross);
//
//        WindowManager.LayoutParams paramAdd = new WindowManager.LayoutParams(
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.TYPE_PHONE,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                PixelFormat.TRANSLUCENT);
//        paramAdd.gravity = Gravity.TOP | Gravity.LEFT;
//        paramAdd.x = 10;
//        paramAdd.y = heightOfDev / 2;
//        windowManager.addView(bubbleView, paramAdd);
//    }


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {


        Bundle extras = sbn.getNotification().extras;
        CharSequence[] lines = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);

        if (lines != null) {

            if (clearDuplicate) {

                msgs.clear();
                clearDuplicate = false;

            }

            for (CharSequence singleLine : lines) {

                msgBifercator = singleLine.toString().split(":");
                NotificationModel model = new NotificationModel();
                model.setUserName(msgBifercator[0].trim());
                model.setMsg(msgBifercator[1].trim());
                Log.d("Lines", model.getUserName() + " : " + model.getMsg());
                msgs.add(model);
            }

        } else {

            /* for only single message notification */

            String title = extras.getString("android.title");
            String text = (String) extras.getCharSequence("android.text");

            if (text != null && title != null) {

                NotificationModel model = new NotificationModel();
                model.setUserName(title.trim());
                model.setMsg(text.trim());
                msgs.add(model);
                clearDuplicate = true;

            }


        }

        Log.d("Lines", "m " + msgs.size());

        Intent intent = new Intent(Constants.action);
        intent.putParcelableArrayListExtra(Constants.msgs,msgs);
        sendBroadcast(intent);

    }


    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }
}