package social.chat.whatsapp.fb.messenger.messaging.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;

import social.chat.whatsapp.fb.messenger.messaging.ViewHolder.AppListHolder;
import social.chat.whatsapp.fb.messenger.messaging.R;

/**
 * Created by mohak on 7/3/17.
 */

public class AppListAdapter extends RecyclerView.Adapter<AppListHolder> {

    ArrayList<String> data;
    Context context;

    public AppListAdapter(Context context, ArrayList<String> data) {
        this.data = data;
        this.context = context;
    }

    @Override
    public AppListHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.single_applist, parent, false);
        return new AppListHolder(view);
    }

    @Override
    public void onBindViewHolder(AppListHolder holder, int position) {

        holder.name.setText(data.get(position));
        TextDrawable drawable = TextDrawable.builder()
                .buildRound("" + data.get(position).charAt(0), Color.parseColor("#3F51B5"));
        holder.icon.setImageDrawable(drawable);

        if (position != 0)
            holder.comingSoon.setVisibility(View.VISIBLE);
        else
            holder.comingSoon.setVisibility(View.GONE);


    }


    @Override
    public int getItemCount() {
        return data.size();
    }


}
