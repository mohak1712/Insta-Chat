package social.chat.whatsapp.fb.messenger.messaging;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import org.greenrobot.eventbus.EventBus;

import java.util.regex.Matcher;

/**
 * Created by mohak on 7/3/17.
 */
public class DataHolder extends RecyclerView.ViewHolder {

    public TextView message, time;
    public ImageView imageView;
    public LinearLayout dataLinear;

    public DataHolder(View view) {
        super(view);
        time = (TextView) view.findViewById(R.id.time);
        message = (TextView) view.findViewById(R.id.message);
        imageView = (ImageView) view.findViewById(R.id.icon);
        dataLinear = (LinearLayout) itemView.findViewById(R.id.linear_data);
    }


    /**
     * bind notification messages to recycler view
     *
     * @param notificationModel {@link NotificationModel} object
     */
    public void bindData(NotificationModel notificationModel) {


        if (!notificationModel.getGroup().equals("-null_123"))
            message.setText(notificationModel.getUserName() + " :\n" + notificationModel.getMsg());
        else
            message.setText(notificationModel.getMsg());

        TextDrawable drawable;

        /* charAt(0) not working*/
        if (notificationModel.getUserName().length() > 1 && notificationModel.getUserName().charAt(1) == '+')
            drawable = TextDrawable.builder()
                    .buildRound("U", Color.parseColor("#065E52"));
        else
            drawable = TextDrawable.builder()
                    .buildRound("" + notificationModel.getUserName().charAt(0), Color.parseColor("#065E52"));

        imageView.setImageDrawable(drawable);
        addClickToLinks(message.getText().toString());

    }

    /**
     * bind reply message to recycler view
     *
     * @param replymessage {@link replyModel} object
     */
    public void bindData2(replyModel replymessage) {

        message.setText(replymessage.getMessage());
        addClickToLinks(replymessage.getMessage());
        imageView.setImageDrawable(null);
    }


    /**
     * extracts link from the message if any and sets click on the link
     *
     * @param text message text
     */
    public void addClickToLinks(String text) {

        SpannableString ss = new SpannableString(text);
        Matcher m = Patterns.WEB_URL.matcher(text);
        while (m.find()) {
            final String url = m.group();

            ClickableSpan spannable = new ClickableSpan() {
                @Override
                public void onClick(View view) {

                    EventBus.getDefault().post(new closeBubbleEvent());

                    Uri webpage = Uri.parse(url);
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                    webIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    itemView.getContext().startActivity(webIntent);

                }
            };

            ss.setSpan(spannable, m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        }

        message.setText(ss);
        message.setMovementMethod(LinkMovementMethod.getInstance());

    }


}
