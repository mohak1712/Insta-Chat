package social.chat.whatsapp.fb.messenger.messaging;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;


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

    private int duplicateMsg;
    private NotificationWear notificationWear;

    private ArrayList<NotificationCompat.Action> actions;
    private ArrayList<PendingIntent> intentList;

    private String prev = "";


    private static final String TAG = "Lines";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Lines", "Created");
        msgs = new ArrayList<>();
        actions = new ArrayList<>();
        intentList = new ArrayList<>();
        EventBus.getDefault().register(this);
    }

    //TODO: edge cases - Exact difference btw 2 messages , single message called multiple times , recycler view update , scroller keys

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        if (!sbn.getPackageName().equals("com.whatsapp"))
            return;

        Bundle extras = sbn.getNotification().extras;
        CharSequence[] lines = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);

        if (lines != null && lines.length > 0) {

            String title = extras.getString("android.title");
            String text = extras.getString("android.text");
//
            if (title == null)
                return;


            Log.d(TAG, "title " + title + " text " + text);

            for (CharSequence k : lines)
                Log.d(TAG, "Char " + k);


            if (title.equals("WhatsApp")) {

                if (msgs.size() < lines.length) {
                    for (int i = msgs.size(); i < lines.length; i++)
                        multipleConversation(lines, i, sbn, extras);
                }
// else
//                    addWithDuplicateCheck(lines, sbn, extras);

            } else {

                if (msgs.size() < lines.length) {
                    for (int i = msgs.size(); i < lines.length; i++)
                        singleConversation(lines, i, title, sbn);
                }

            }


        } else {

            NotificationWearReader(sbn.getNotification());
            CharSequence singleMessage = extras.getCharSequence(Notification.EXTRA_TEXT);
            Log.d(TAG, " txt " + singleMessage);
            addSingleMessage(extras, lines, sbn);

        }

        Log.d(TAG, " size of list = " + msgs.size());
        Intent startChat = new Intent(this, FloatingBubble.class);
        startChat.putParcelableArrayListExtra(Constants.msgs, msgs);
        startService(startChat);


//        duplicateMsg = -1;
//
//        Log.d("Lines", "called ji");
//        Bundle extras = sbn.getNotification().extras;
//        CharSequence[] lines = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);
//
//        if (lines != null && lines.length > 0) {
//
//            String title = extras.getString("android.title");
//            String text = extras.getString("android.text");
//
//            if (title == null)
//                return;
//
//
//            Log.d("Lines", "title " + title + " text " + text);
//
//            for (CharSequence k : lines)
//                Log.d("Lines", "Char " + k);
//
//            if (title.equals("WhatsApp")) {
//
//                Log.d("Lines", "mul");
//
//                if (msgs.size() < lines.length) {
//                    for (int i = msgs.size(); i < lines.length; i++)
//                        multipleConversation(lines, i, sbn, extras);
//                }else
//                    addWithDuplicateCheck(lines, sbn, extras);
//
//            } else {
//
//                if (msgs.size() < lines.length) {
//                    pos = lines.length;
//                    for (int i = msgs.size(); i < lines.length; i++)
//                        singleConversation(lines, i, title, sbn);
//                } else{
//
//
//                }
////                    addWithDuplicateCheck2(lines, title, sbn);
//
//
//            }
//
//        } else {
//
//            NotificationWearReader(sbn.getNotification());
//
//            EventBus.getDefault().post(notificationWear);
//
//            CharSequence singleMessage = extras.getCharSequence(Notification.EXTRA_TEXT);
//            Log.d("Lines", " txt " + singleMessage);
//
//            if (!calledOnce) {
//                calledOnce = true;
//                addSingleMessage(extras, lines, sbn);
//
//            }
//
//        }
//
//        Intent startChat = new Intent(this, FloatingBubble.class);
//        startChat.putParcelableArrayListExtra(Constants.msgs, msgs);
//        startService(startChat);

    }

//    private void attachImages() {
//
//        for (int i = 0; i < msgs.size(); i++) {
//            msgs.get(i).setIcon(icons.get(i));
//        }
//    }


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
            intentList.add(notification.contentIntent);
        }

        Log.d("Lines", "wearableExtender: " + wearableExtender.getPages().size());
        Log.d("Lines", "actions: " + actions.size());


        for (NotificationCompat.Action act : actions) {
            if (act != null && act.getRemoteInputs() != null) {
                notificationWear.remoteInputs.addAll(Arrays.asList(act.getRemoteInputs()));
                notificationWear.pendingIntent.add(act.actionIntent);

                Log.d("Lines", " L " + act.getRemoteInputs()[0].getLabel());
                Log.d("Lines", "Bundle " + act.getRemoteInputs()[0].getResultKey());
            }
        }


        notificationWear.bundle = notification.extras;
    }


    private void addWithDuplicateCheck(CharSequence[] lines, StatusBarNotification sbn, Bundle extras) {

        Log.d("Lines", "dup");

        NotificationModel model = msgs.get(msgs.size() - 1);

        for (int i = 0; i < lines.length; i++) {

            msgBifercator = lines[i].toString().split(":", 2);

            Log.d("Lines", "dup " + msgBifercator[1] + " " + model.getMsg());

            if (model.getGroup().equals("-null_123") && model.getUserName().equals(msgBifercator[0].trim()) &&
                    model.getMsg().equals(msgBifercator[1].trim())) {

                duplicateMsg = i;
                break;

            } else if (model.getGroup().equals(msgBifercator[0].split("@")[1].trim()) && model.getUserName().equals(msgBifercator[0].split("@")[0].trim())
                    && model.getMsg().equals(msgBifercator[1].trim())) {

                duplicateMsg = i;
                break;

            }
        }

        if (duplicateMsg == lines.length - 1)
            multipleConversation(lines, lines.length - 1, sbn, extras);
        else
            for (int i = duplicateMsg + 1; i < lines.length; i++)
                multipleConversation(lines, i, sbn, extras);

    }

    private void addWithDuplicateCheck2(CharSequence[] lines, String title, StatusBarNotification sbn) {

        Log.d("Lines", "dup");
        NotificationModel model = msgs.get(msgs.size() - 1);

        for (int i = 0; i < lines.length; i++) {

            msgBifercator = lines[i].toString().split(":", 2);

            if (model.getMsg().equals(msgBifercator[1].trim())) {

                duplicateMsg = i;
                break;
            }

        }

        if (duplicateMsg == lines.length - 1)
            singleConversation(lines, lines.length - 1, title, sbn);
        else
            for (int i = duplicateMsg + 1; i < lines.length; i++)
                singleConversation(lines, i, title, sbn);

    }


    private void addSingleMessage(Bundle extras, CharSequence[] lines, StatusBarNotification sbn) {

        CharSequence singleMessage = extras.getCharSequence(Notification.EXTRA_TEXT);
        CharSequence title = extras.getCharSequence(Notification.EXTRA_TITLE);

        Log.d(TAG, "sm " + singleMessage);

        if (title == null || singleMessage == null || prev.equals(singleMessage.toString()))
            return;

        if (lines == null || lines.length == 0) {

            NotificationModel model = new NotificationModel();

            if (isaValidContact(title.toString())) {

                model.setGroup("-null_123");
                model.setUserName(title.toString());
                model.setMsg(singleMessage.toString());
                model.setTime(sbn.getPostTime());
                prev = singleMessage.toString();

            } else {

                msgBifercator = singleMessage.toString().split(":", 2);
                model.setGroup(title.toString());
                model.setUserName(msgBifercator[0].trim());
                model.setTime(sbn.getPostTime());
                model.setMsg(msgBifercator[1].trim());
                prev = msgBifercator[1].trim();

            }

//            setImageBitmap(sbn,model);
            msgs.add(model);

        }


        Log.d("Lines", "else called");

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
        if (isaValidContact(title.trim())) {

            Log.d("Lines", " valid = true");
            model.setGroup("-null_123");
            model.setUserName(title);
            model.setMsg(lines[pos].toString());

        } else {

            Log.d("Lines", "valid = false");
            msgBifercator = lines[pos].toString().split(":", 2);
            model.setGroup(title);
            model.setUserName(msgBifercator[0].trim());
            model.setMsg(msgBifercator[1].trim());
        }

        model.setTime(sbn.getPostTime());
//        setImageBitmap(sbn,model);
        msgs.add(model);

    }

    /**
     * add messages for multiple conversation
     *
     * @param lines  array of messages
     * @param i      position
     * @param sbn    statusBarNotification
     * @param extras bundle
     */
    private void multipleConversation(CharSequence[] lines, int i, StatusBarNotification sbn, Bundle extras) {

        msgBifercator = lines[i].toString().split(":", 2);

        NotificationModel model = new NotificationModel();
        if (msgBifercator[0].trim().contains("@")) {
            model.setGroup(msgBifercator[0].split("@")[1].trim());
            model.setUserName(msgBifercator[0].split("@")[0].trim());
        } else {

            model.setGroup("-null_123");
            model.setUserName(msgBifercator[0].trim());
        }

        model.setMsg(msgBifercator[1].trim());
//        setImageBitmap(sbn,model);
        model.setTime(sbn.getPostTime());
        msgs.add(model);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);

    }

    /**
     * check if sender is a contact or a group
     *
     * @param name name of the sender
     * @return true if valid else false
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

//    /**
//     * get notification image from notification
//     *
//     * @param notification Notification
//     */
//    void setImageBitmap(Notification notification) {
//
//        Bitmap id = notification.largeIcon;
//        Bitmap bmp = null;
//
//        if (id != null) {
//
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            id.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            byte[] byteArray = stream.toByteArray();
//
//            if (byteArray != null) {
//
//                bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//            }
//
//        } else
//            bmp = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher);
//
//        icons.add(bmp);
//
//    }

    @Subscribe
    public void PostWear(postEvent event) {

        Log.d(TAG, "Event called ");
        EventBus.getDefault().post(notificationWear);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}