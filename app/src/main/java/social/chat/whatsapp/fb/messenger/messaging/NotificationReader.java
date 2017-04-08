package social.chat.whatsapp.fb.messenger.messaging;

import android.Manifest;
import android.app.Notification;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by mohak on 28/2/17.
 */

public class NotificationReader extends NotificationListenerService {

    private static final String TAG = "Lines";
    /**
     * Store notifications in a list
     */
    ArrayList<NotificationModel> msgs;
    /**
     * split content to get username and the text received
     */
    String[] msgBifercator;
    /**
     * show only last one of te duplicate msgs
     */
    private int duplicateMsg;
    /**
     * using wear to get actions and intent for notification
     */
    private NotificationWear notificationWear;
    /**
     * list of notification actions
     */
    private ArrayList<NotificationCompat.Action> actions;
    /**
     * true when user sends a message
     */
    private boolean replied;
    /**
     * list  of the person / group to whom the msg is send
     */

    private boolean calledOnce;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Created");
        calledOnce = false;
        replied = false;
        msgs = new ArrayList<>();
        actions = new ArrayList<>();
        EventBus.getDefault().register(this);
    }


    // TODO: add images to notifications , add functionality for other apps , improve algorithm


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        if (!sbn.getPackageName().equals("com.whatsapp") || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED)
            return;
/*
        TODO : check overlay permission in service( not working currently)
        if (android.os.Build.VERSION.SDK_INT >= 23) {

            if (!Settings.canDrawOverlays(this)) {
                Log.d(TAG, "err");
                return;
            } else
                Log.d(TAG, "err4");
        }
*/
        Bundle extras = sbn.getNotification().extras;
        CharSequence[] lines = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);

        String title = extras.getString("android.title");

        if (lines != null && lines.length > 0) {

            if (title == null)
                return;

            if (title.equals("WhatsApp")) {

                if (msgs.size() < lines.length) {
                    for (int i = msgs.size(); i < lines.length; i++)
                        multipleConversation(lines, i, sbn);
                } else
                    multipleConversation(lines, lines.length - 1, sbn);

            } else {

                if (msgs.size() < lines.length) {
                    for (int i = msgs.size(); i < lines.length; i++)
                        singleConversation(lines, i, title, sbn);
                } else
                    singleConversation(lines, lines.length - 1, title, sbn);
            }


        } else {

            addSingleMessage(extras, lines, sbn);
            NotificationWearReader(sbn.getNotification());
        }


        if (!FloatingBubble.isServiceRunning) {

            Intent startChat = new Intent(this, FloatingBubble.class);
            startChat.putParcelableArrayListExtra(Constants.msgs, msgs);
            startService(startChat);

        } else {

            Log.d(TAG, "service event");
            EventBus.getDefault().post(new postNotificationData(msgs));
        }

    }

    /**
     * add initial message to list when there is no EXTRA_LINES
     *
     * @param extras Bundle
     * @param lines  array of data to be checked for null
     * @param sbn    status bar notification
     */

    private void addSingleMessage(Bundle extras, CharSequence[] lines, StatusBarNotification sbn) {

        CharSequence singleMessage = extras.getCharSequence(Notification.EXTRA_TEXT);
        CharSequence title = extras.getCharSequence(Notification.EXTRA_TITLE);

        Log.d(TAG, "called single m");

        if (title == null || singleMessage == null || calledOnce)
            return;

        Log.d(TAG, "add single m");

        if (lines == null || lines.length == 0) {

            NotificationModel model = new NotificationModel();

            if (isaValidContact(title.toString())) {

                model.setGroup("-null_123");
                model.setUserName(title.toString());
                model.setMsg(singleMessage.toString());

            } else {

                if (title.toString().contains("@")) {

                    /* video messages behave differently */
                    msgBifercator = title.toString().split("@");
                    model.setGroup(msgBifercator[1]);
                    model.setUserName(msgBifercator[0]);
                    model.setMsg(singleMessage.toString());

                } else {

                    msgBifercator = singleMessage.toString().split(":", 2);
                    model.setGroup(title.toString());
                    model.setUserName(msgBifercator[0]);
                    model.setMsg(msgBifercator[1]);

                }
            }

            model.setTime(sbn.getPostTime());
            msgs.add(model);
            calledOnce = true;
        }


    }

    /**
     * add all pending intents and remote inputs from notification for later use when replying to a message
     *
     * @param notification Notification
     */
    public void NotificationWearReader(Notification notification) {

        int flag = 0;

        notificationWear = new NotificationWear();

        NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender(notification);

        if (actions.size() == 0) {
            actions.addAll(wearableExtender.getActions());
        } else
            for (int i = 0; i < actions.size(); i++) {

                if (actions.get(i).getRemoteInputs()[0] == null) {
                    flag = 0;
                    break;
                }

                if (!actions.get(i).getRemoteInputs()[0].getLabel().equals(wearableExtender.getActions().get(0).getRemoteInputs()[0].getLabel())) {
                    flag = 1;
                } else {
                    flag = 0;
                    break;
                }
            }

        if (flag == 1) {
            actions.addAll(wearableExtender.getActions());
        }

        Log.d("Lines", "actions: " + actions.size());

        for (NotificationCompat.Action act : actions) {
            if (act != null && act.getRemoteInputs() != null) {
                notificationWear.remoteInputs.addAll(Arrays.asList(act.getRemoteInputs()));
                notificationWear.pendingIntent.add(act.actionIntent);

                Log.d(TAG, " Label " + act.getRemoteInputs()[0].getLabel());
                Log.d(TAG, "Bundle " + act.getRemoteInputs()[0].getResultKey());
            }
        }


        notificationWear.bundle = notification.extras;
    }


    /**
     * add messages for single conversation
     *
     * @param lines array of messages
     * @param pos   position
     * @param title sender's name
     * @param sbn   statusBarNotification
     */
    private void singleConversation(CharSequence[] lines, int pos, String title, StatusBarNotification sbn) {

        NotificationModel model = new NotificationModel();
        if (isaValidContact(title)) {

            model.setGroup("-null_123");
            model.setUserName(title);
            model.setMsg(lines[pos].toString());

        } else {

            msgBifercator = lines[pos].toString().split(":", 2);
            model.setGroup(title);
            model.setUserName(msgBifercator[0]);
            model.setMsg(msgBifercator[1]);
        }

        model.setTime(sbn.getPostTime());
        msgs.add(model);

    }

    /**
     * add messages for multiple conversation
     *
     * @param lines array of messages
     * @param i     position
     * @param sbn   statusBarNotification
     */
    private void multipleConversation(CharSequence[] lines, int i, StatusBarNotification sbn) {

        Log.d(TAG, "called mc");

        /* prevents multiple addition of last notification to the list*/
        if (replied) {
            replied = false;
            return;
        }

        msgBifercator = lines[i].toString().split(":", 2);

        NotificationModel model = new NotificationModel();
        if (msgBifercator[0].contains("@")) {
            model.setGroup(msgBifercator[0].split("@")[1]);
            model.setUserName(msgBifercator[0].split("@")[0]);
        } else {
            model.setGroup("-null_123");
            model.setUserName(msgBifercator[0]);
        }

        model.setMsg(msgBifercator[1]);
        model.setTime(sbn.getPostTime());
        msgs.add(model);

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }

    /**
     * check if sender is a contact or not
     *
     * @param name name of the sender
     * @return true if sender is a contact else false
     */
    public boolean isaValidContact(String name) {

        Cursor cursor = getContentResolver().query(
                ContactsContract.RawContacts.CONTENT_URI,
                new String[]{ContactsContract.RawContacts.CONTACT_ID},
                ContactsContract.RawContacts.ACCOUNT_TYPE + "= ?" + " AND " + ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY + "= ?",
                new String[]{"com.whatsapp", name},
                null);

        return !(cursor == null || cursor.getCount() == 0);
    }

    @Subscribe
    public void PostWear(postEvent event) {

        if (event.size == 1) {
            calledOnce = false;
        } else {
            replied = true;
        }

        EventBus.getDefault().post(notificationWear);

    }

    @Subscribe
    public void ClearList(clearListEvent event) {

        if (msgs != null)
            msgs.clear();

        if (actions != null)
            actions.clear();

        calledOnce = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}