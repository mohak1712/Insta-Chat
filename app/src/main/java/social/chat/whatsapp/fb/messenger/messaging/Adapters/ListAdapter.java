package social.chat.whatsapp.fb.messenger.messaging.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import social.chat.whatsapp.fb.messenger.messaging.Models.replyModel;
import social.chat.whatsapp.fb.messenger.messaging.Models.NotificationModel;
import social.chat.whatsapp.fb.messenger.messaging.R;
import social.chat.whatsapp.fb.messenger.messaging.ViewHolder.DataHolder;

/**
 * Created by mohak on 7/3/17.
 */

public class ListAdapter extends RecyclerView.Adapter<DataHolder> {

    private static final int V1 = 1;
    private static final int V2 = 2;

    private ArrayList<Object> data;
    private Context context;

    public ListAdapter(ArrayList<Object> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override

    public DataHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        if (viewType == V1)
            view = LayoutInflater.from(context).inflate(R.layout.single_message_1, parent, false);
        else
            view = LayoutInflater.from(context).inflate(R.layout.single_message_2, parent, false);

        return new DataHolder(view);
    }

    @Override
    public void onBindViewHolder(DataHolder holder, int position) {

        if (data.get(position) instanceof replyModel) {

            replyModel currentModel = ((replyModel) data.get(position));
            holder.bindData2(currentModel);

            /* check from time difference between two message*/
            if ((data.get(position - 1) instanceof NotificationModel && ((currentModel.getTime() - (((NotificationModel) data.get(position - 1)).getTime())) / (1000 * 60)) %
                    60 >= 1) || (data.get(position - 1) instanceof replyModel && ((currentModel.getTime() - (((replyModel) data.get(position - 1)).getTime())) / (1000 * 60)) % 60 >= 1)) {

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
                    60 >= 1) || (data.get(position - 1) instanceof replyModel && ((currentModel.getTime() - (((replyModel) data.get(position - 1)).getTime())) / (1000 * 60)) %
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
    public int getItemViewType(int position) {

        if (data.get(position) instanceof NotificationModel)
            return V1;
        else
            return V2;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public ArrayList<Object> getData(){

        return data;
    }


    /**
     * update recycler view
     *
     * @param updatedData new data
     */
    public void swap(ArrayList<Object> updatedData) {

        data = new ArrayList<>();
        data.addAll(updatedData);
        notifyDataSetChanged();

    }
}
