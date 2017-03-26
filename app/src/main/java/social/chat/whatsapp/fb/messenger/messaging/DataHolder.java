package social.chat.whatsapp.fb.messenger.messaging;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mohak on 7/3/17.
 */
public class DataHolder extends RecyclerView.ViewHolder {

    public TextView message, time;
    public CircleImageView imageView;
    public LinearLayout dataLinear;

    public DataHolder(View view) {
        super(view);
        time = (TextView) view.findViewById(R.id.time);
        message = (TextView) view.findViewById(R.id.message);
        imageView = (CircleImageView) view.findViewById(R.id.icon);
        dataLinear = (LinearLayout) itemView.findViewById(R.id.linear_data);
    }

    public void bindData(NotificationModel notificationModel, int position) {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.START;
        params.setMargins(0, 10, 0, 10);
        dataLinear.setLayoutParams(params);
        imageView.setVisibility(View.VISIBLE);
        message.setBackgroundColor(Color.parseColor("#065E52"));

        if (!notificationModel.getGroup().equals("-null_123"))
            message.setText(notificationModel.getUserName() + " : \n" + notificationModel.getMsg());
        else
            message.setText(notificationModel.getMsg());

        imageView.setImageBitmap(notificationModel.getIcon());

    }

    public void bindData2(reply replymessage) {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.END;
        params.setMargins(0, 10, 0, 10);
        dataLinear.setLayoutParams(params);
        imageView.setVisibility(View.GONE);
        message.setText(replymessage.getMessage());
        message.setBackgroundColor(Color.parseColor("#0DC143"));
    }
}
