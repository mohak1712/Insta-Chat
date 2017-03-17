package social.chat.whatsapp.fb.messenger.messaging;

import android.app.Notification;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;


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

    private boolean calledOnce = false;

    private int duplicateMsg;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Lines", "Created");
        msgs = new ArrayList<>();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        if (!sbn.getPackageName().equals("com.whatsapp"))
            return;

        duplicateMsg = -1;

//        notificationCleared = false;

        Log.d("Lines", "called ji");
        Bundle extras = sbn.getNotification().extras;
        CharSequence[] lines = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);

        if (lines != null && lines.length > 0) {

            String title = extras.getString("android.title");
            String text = extras.getString("android.text");

            if (title == null)
                return;

//            Log.d("Lines", "title " + title + " text " + text);
//
//            for (CharSequence k : lines)
//                Log.d("Lines", "Char " + k);

            if (title.equals("WhatsApp")) {

                Log.d("Lines", "mul");

//                if (msgs.size() >= 7) {
//
//                    Log.d("Lines", "More than = 7");
//
////                    if (lines.length == 7)
////                        addToList(lines, lines.length - 1, sbn, extras);
////                    else
//                        addWithDuplicateCheck(lines, sbn, extras);
//
//                } else {

                Log.d("Lines", "Less than 7");


                if (msgs.size() < lines.length)
                    for (int i = msgs.size(); i < lines.length; i++)
                        addToList(lines, i, sbn, extras);
//                if (msgs.size() >= lines.length)
                else if (lines.length == 7)
                    addToList(lines, lines.length - 1, sbn, extras);
                else
                    addWithDuplicateCheck(lines, sbn, extras);
//                    else
//                        addToList(lines, lines.length - 1, sbn, extras);

            } else {

//                Log.d("Lines", "single");
//
//                if (msgs.size() >= 7) {
//
//                    Log.d("Lines", "More than = 7 " + lines[lines.length - 1]);
////
////                    if (lines.length == 7)
////                        addToList2(lines, lines.length - 1, title, sbn);
////                    else
//                        addWithDuplicateCheck2(lines, title, sbn);
//                } else {

                Log.d("Lines", "Less than 7 lol");


                if (msgs.size() < lines.length)
                    for (int i = msgs.size(); i < lines.length; i++)
                        addToList2(lines, i, title, sbn);
//                if (msgs.size() > lines.length) {
                else if (lines.length == 7)
                    addToList2(lines, lines.length - 1, title, sbn);
                else
                    addWithDuplicateCheck2(lines, title, sbn);
//                    else
//                        addToList2(lines, lines.length - 1, title, sbn);

//                }

            }

        } else {

            if (calledOnce)
                return;

            calledOnce = true;
//            NotificationModel model = msgs.get(msgs.size() - 1);

            addSingleMessage(extras, lines, sbn);

        }

//        Log.d("Lines", " dc " + sbn.getNotification().contentIntent.getIntentSender().toString()+ " " + sbn.getNotification().fullScreenIntent.getIntentSender().toString());
        Intent intent = new Intent(Constants.action);
        intent.putExtra("p",sbn.getNotification().contentIntent);
        intent.putParcelableArrayListExtra(Constants.msgs, msgs);
        sendBroadcast(intent);

    }

    private void addWithDuplicateCheck(CharSequence[] lines, StatusBarNotification sbn, Bundle extras) {

        NotificationModel model = msgs.get(msgs.size() - 1);

        for (int i = 0; i < lines.length; i++) {

            msgBifercator = lines[i].toString().split(":");
            if (model.getGroup().equals("-null_123") && model.getUserName().equals(msgBifercator[0].trim()) && model.getMsg().equals(msgBifercator[1].trim())) {

                duplicateMsg = i;
                break;

            } else if (model.getGroup().equals(msgBifercator[0].split("@")[1].trim()) && model.getUserName().equals(msgBifercator[0].split("@")[0].trim())
                    && model.getMsg().equals(msgBifercator[1].trim())) {

                duplicateMsg = i;
                break;

            }
        }

        for (int i = duplicateMsg + 1; i < lines.length; i++) {

            addToList(lines, i, sbn, extras);
        }

    }

    private void addWithDuplicateCheck2(CharSequence[] lines, String title, StatusBarNotification sbn) {

        NotificationModel model = msgs.get(msgs.size() - 1);

        for (int i = 0; i < lines.length; i++) {

            if (model.getMsg().equals(lines[i].toString())) {
                duplicateMsg = i;
                break;
            }

        }

        for (int i = duplicateMsg + 1; i < lines.length; i++) {
            addToList2(lines, i, title, sbn);
        }

    }

    private void addSingleMessage(Bundle extras, CharSequence[] lines, StatusBarNotification sbn) {

        CharSequence singleMessage = extras.getCharSequence(Notification.EXTRA_TEXT);
        CharSequence title = extras.getCharSequence(Notification.EXTRA_TITLE);


        if (title == null || singleMessage == null)
            return;

        if (lines == null || lines.length == 0) {

            NotificationModel model = new NotificationModel();

            if (isaValidContact(title.toString())) {

                model.setGroup("-null_123");
                model.setUserName(title.toString());
                model.setMsg(singleMessage.toString());
                model.setTime(sbn.getPostTime());
                msgs.add(model);

            } else {

                msgBifercator = singleMessage.toString().split(":");
                model.setGroup(title.toString());
                model.setUserName(msgBifercator[0].trim());
                model.setMsg(msgBifercator[1].trim());
                model.setTime(sbn.getPostTime());
                msgs.add(model);

            }

        }


        Log.d("Lines", "else called");
    }

    private void addToList2(CharSequence[] lines, int pos, String title, StatusBarNotification sbn) {

        NotificationModel model = new NotificationModel();
        if (isaValidContact(title.trim())) {

            Log.d("Lines", " valid = true");
            model.setGroup("-null_123");
            model.setUserName(title);
            model.setMsg(lines[pos].toString());

        } else {

            Log.d("Lines", "valid = false");
            msgBifercator = lines[pos].toString().split(":");
            model.setGroup(title);
            model.setUserName(msgBifercator[0].trim());
            model.setMsg(msgBifercator[1].trim());
        }

        model.setTime(sbn.getPostTime());
        msgs.add(model);

    }

    private void addToList(CharSequence[] lines, int i, StatusBarNotification sbn, Bundle extras) {

        msgBifercator = lines[i].toString().split(":");
        NotificationModel model = new NotificationModel();
        if (msgBifercator[0].trim().contains("@")) {
            model.setGroup(msgBifercator[0].split("@")[1].trim());
            model.setUserName(msgBifercator[0].split("@")[0].trim());
        } else {
            model.setGroup("-null_123");
            model.setUserName(msgBifercator[0].trim());
        }

        model.setMsg(msgBifercator[1].trim());
        model.setTime(sbn.getPostTime());
        msgs.add(model);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        Log.d("Lines", "Notification Cleared");

    }


    public boolean isaValidContact(String name) {

        Cursor cursor = getContentResolver().query(
                ContactsContract.RawContacts.CONTENT_URI,
                new String[]{ContactsContract.RawContacts.CONTACT_ID},
                ContactsContract.RawContacts.ACCOUNT_TYPE + "= ?" + " AND " + ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY + "= ?",
                new String[]{"com.whatsapp", name},
                null);


        return !(cursor == null || cursor.getCount() == 0);
    }
}