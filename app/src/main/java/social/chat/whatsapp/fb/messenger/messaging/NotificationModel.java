package social.chat.whatsapp.fb.messenger.messaging;

import android.app.PendingIntent;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mohak on 2/3/17.
 */

public class NotificationModel implements Parcelable{

    private String group;
    private String userName;
    private String msg;
    private long time;
    private int icon;
    private String phnNo;


    public NotificationModel() {
    }

    protected NotificationModel(Parcel in) {
        userName = in.readString();
        msg = in.readString();
        time = in.readLong();
        icon = in.readInt();
        group = in.readString();

    }

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

    public String getPhnNo() {
        return phnNo;
    }

    public void setPhnNo(String phnNo) {
        this.phnNo = phnNo;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
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

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userName);
        parcel.writeString(msg);
        parcel.writeLong(time);
        parcel.writeInt(icon);
        parcel.writeString(group);
    }
}
