package social.chat.whatsapp.fb.messenger.messaging;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by mohak on 13/3/17.
 */
public class AppListHolder extends RecyclerView.ViewHolder {

    TextView name;
    ImageView icon;
    LinearLayout linearLayout;
    TextView comingSoon;

    public AppListHolder(View view) {
        super(view);

        name = (TextView) view.findViewById(R.id.packageText);
        icon = (ImageView) view.findViewById(R.id.packageImage);
        linearLayout = (LinearLayout) view.findViewById(R.id.mainLinear);
        comingSoon = (TextView) view.findViewById(R.id.comingSoon);

    }
}
