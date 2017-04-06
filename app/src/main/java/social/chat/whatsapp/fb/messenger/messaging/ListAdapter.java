package social.chat.whatsapp.fb.messenger.messaging;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mohak on 7/3/17.
 */

public class ListAdapter extends RecyclerView.Adapter<DataHolder> {

    private ArrayList<Object> data;
    private Context context;

    public ListAdapter(ArrayList<Object> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override

    public DataHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.single_message_1, parent, false);
        return new DataHolder(view);
    }

    @Override
    public void onBindViewHolder(DataHolder holder, int position) {

        if (data.get(position) instanceof reply) {

            reply currentModel = ((reply) data.get(position));
            holder.bindData2(currentModel);

            /* check from time difference between two message*/
            if ((data.get(position - 1) instanceof NotificationModel && ((currentModel.getTime() - (((NotificationModel) data.get(position - 1)).getTime())) / (1000 * 60)) %
                    60 >= 1) || (data.get(position - 1) instanceof reply && ((currentModel.getTime() - (((reply) data.get(position - 1)).getTime())) / (1000 * 60)) % 60 >= 1)) {

                holder.time.setVisibility(View.VISIBLE);
                Date date = new Date(currentModel.getTime());
                DateFormat formatter = new SimpleDateFormat("HH:mm");
                String dateFormatted = formatter.format(date);
                holder.time.setText(dateFormatted);

            } else
                holder.time.setVisibility(View.GONE);

        } else if (data.get(position) instanceof NotificationModel) {

            NotificationModel currentModel = ((NotificationModel) data.get(position));
            holder.bindData(currentModel);

            /* check from time difference between two message*/
            if (position == 0 || (data.get(position - 1) instanceof NotificationModel && ((currentModel.getTime() - (((NotificationModel) data.get(position - 1)).getTime())) / (1000 * 60)) %
                    60 >= 1) || (data.get(position - 1) instanceof reply && ((currentModel.getTime() - (((reply) data.get(position - 1)).getTime())) / (1000 * 60)) %
                    60 >= 1)) {

                holder.time.setVisibility(View.VISIBLE);
                Date date = new Date(currentModel.getTime());
                DateFormat formatter = new SimpleDateFormat("HH:mm");
                String dateFormatted = formatter.format(date);
                holder.time.setText(dateFormatted);

            } else
                holder.time.setVisibility(View.GONE);

        }

    }


    @Override
    public int getItemCount() {
        return  data.size();
    }


    /**
     * update recycler view
     * @param updatedData new data
     */
    public void swap(ArrayList<Object> updatedData) {

        data = new ArrayList<>();
        data.addAll(updatedData);
        notifyDataSetChanged();

    }
}
