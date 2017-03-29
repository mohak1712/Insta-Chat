package social.chat.whatsapp.fb.messenger.messaging;

/**
 * Created by mohak on 25/3/17.
 */

public class reply {

    private int pos;
    private String message;
    private String key;
    private long time;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getPos() {
        return pos;
    }

    void setPos(int pos) {
        this.pos = pos;
    }

    public String getMessage() {
        return message;
    }

    void setMessage(String message) {
        this.message = message;
    }
}
