package social.chat.whatsapp.fb.messenger.messaging.ViewHolder;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import social.chat.whatsapp.fb.messenger.messaging.R;

/**
 * Created by mohak on 13/3/17.
 */
public class AppListHolder extends RecyclerView.ViewHolder {

    public TextView name;
    public ImageView icon;
    public LinearLayout linearLayout;
    public TextView comingSoon;

    public AppListHolder(View view) {
        super(view);

        name = (TextView) view.findViewById(R.id.packageText);
        icon = (ImageView) view.findViewById(R.id.packageImage);
        linearLayout = (LinearLayout) view.findViewById(R.id.mainLinear);
        comingSoon = (TextView) view.findViewById(R.id.comingSoon);

    }
}
