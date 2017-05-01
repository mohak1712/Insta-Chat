package social.chat.whatsapp.fb.messenger.messaging.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mohak on 2/3/17.
 */

public class NotificationModel implements Parcelable {

    public static final Creator<NotificationModel> CREATOR = new Creator<NotificationModel>() {
        @Override
        public NotificationModel createFromParcel(Parcel in) {
            return new NotificationModel(in);
        }

        @Override
        public NotificationModel[] newArray(int size) {
            return new NotificationModel[size];
        }
    };
    private String group;
    private String userName;
    private String msg;
    private long time;


    public NotificationModel() {
    }

    protected NotificationModel(Parcel in) {
        group = in.readString();
        userName = in.readString();
        msg = in.readString();
        time = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(group);
        dest.writeString(userName);
        dest.writeString(msg);
        dest.writeLong(time);
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }


}


