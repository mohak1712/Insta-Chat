package social.chat.whatsapp.fb.messenger.messaging;

import android.app.Notification;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
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

        Log.d("Lines", "called ji");
        Bundle extras = sbn.getNotification().extras;
        CharSequence[] lines = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);

        if (lines != null && lines.length > 0) {

            String title = extras.getString("android.title");
            String text = extras.getString("android.text");

            if (title == null)
                return;

            Log.d("Lines", "title " + title + " text " + text);

            for (CharSequence k : lines)
                Log.d("Lines", "Char " + k);

            if (title.equals("WhatsApp")) {

                Log.d("Lines", "mul");

                if (msgs.size() >= 7) {

                    Log.d("Lines", "More than = 7");

                    if (lines.length == 7)
                        addToList(lines, lines.length - 1, sbn, extras);
                    else
                        for (int i = 0; i < lines.length; i++)
                            addToList(lines, i, sbn, extras);


                } else {

                    Log.d("Lines", "Less than 7");

                    if (msgs.size() > lines.length)
                        for (int i = 0; i < lines.length; i++)
                            addToList(lines, i, sbn, extras);
                    else if (msgs.size() < lines.length)
                        for (int i = msgs.size(); i < lines.length; i++)
                            addToList(lines, i, sbn, extras);
                    else
                        addToList(lines, lines.length - 1, sbn, extras);

                }

            } else {

                Log.d("Lines", "single");

                if (msgs.size() >= 7) {

                    Log.d("Lines", "More than = 7 " + lines[lines.length - 1]);

                    if (lines.length == 7)
                        addToList2(lines, lines.length - 1, title, sbn);
                    else
                        for (int i = 0; i < lines.length; i++)
                            addToList2(lines, i, title, sbn);

                } else {

                    Log.d("Lines", "Less than 7 lol");

                    if (msgs.size() > lines.length)
                        for (int i = 0; i < lines.length; i++)
                            addToList2(lines, i, title, sbn);
                    else if (msgs.size() < lines.length)
                        for (int i = msgs.size(); i < lines.length; i++)
                            addToList2(lines, i, title, sbn);
                    else
                        addToList2(lines, lines.length - 1, title, sbn);

                }

            }

        } else {

            if (calledOnce)
                return;

            calledOnce = true;
            addSingleMessage(extras, lines, sbn);

        }

        Intent intent = new Intent(Constants.action);
        intent.putParcelableArrayListExtra(Constants.msgs, msgs);
        sendBroadcast(intent);

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