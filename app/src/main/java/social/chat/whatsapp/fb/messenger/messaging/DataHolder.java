package social.chat.whatsapp.fb.messenger.messaging;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
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

    public void bindData(NotificationModel notificationModel, int position) {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.START;
        params.setMargins(0, 10, 0, 10);
        dataLinear.setLayoutParams(params);
        imageView.setVisibility(View.VISIBLE);
//        message.setBackgroundColor(Color.parseColor("#065E52"));

        if (!notificationModel.getGroup().equals("-null_123"))
            message.setText(notificationModel.getUserName() + " : \n" + notificationModel.getMsg());
        else
            message.setText(notificationModel.getMsg());

        TextDrawable drawable = TextDrawable.builder()
                .buildRound("" + notificationModel.getUserName().charAt(0), Color.parseColor("#065E52"));

        imageView.setImageDrawable(drawable);
        addClickToLinks(message.getText().toString());

    }

    public void bindData2(reply replymessage) {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.END;
        params.setMargins(0, 10, 0, 10);
        dataLinear.setLayoutParams(params);
        imageView.setVisibility(View.GONE);
        message.setText(replymessage.getMessage());
        addClickToLinks(replymessage.getMessage());
//        message.setBackgroundColor(Color.parseColor("#80065E52"));
    }


    /**
     * extracts link from the message if any and sets click on the link
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
