package social.chat.whatsapp.fb.messenger.messaging;

import android.content.ClipboardManager;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by mohak on 25/2/17.
 */

public class CustomPagerAdapter extends PagerAdapter {

    private Context mContext;
    private LinkedHashMap<String, ArrayList<Object>> data;
    private ArrayList<String> keys;
    private Clicked clickListner;

    public CustomPagerAdapter(Context mContext, LinkedHashMap<String, ArrayList<Object>> data, ArrayList<String> keys) {
        this.mContext = mContext;
        this.data = data;
        this.keys = keys;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, final int position) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View layout = inflater.inflate(R.layout.chat_layout, collection, false);

        RecyclerView recyclerView = (RecyclerView) layout.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        ListAdapter adapter = new ListAdapter(data.get(keys.get(position)), mContext);
        recyclerView.setAdapter(adapter);
        recyclerView.setTag(keys.get(position));

        final EditText editText = (EditText) layout.findViewById(R.id.text);

        ImageView send = (ImageView) layout.findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (clickListner != null){

                    clickListner.itemClicked(position, editText.getText().toString());
                    editText.getText().clear();
                }

            }
        });

        editText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                ClipboardManager clipMan = (ClipboardManager) mContext.getSystemService(CLIPBOARD_SERVICE);
                if (clipMan.getPrimaryClip()!=null)
                    editText.setText(editText.getText().toString() + clipMan.getPrimaryClip().getItemAt(0).getText().toString());

                return true;
            }
        });

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if (i == KeyEvent.KEYCODE_BACK) {

                    editText.clearFocus();
                    return true;
                } else
                    return false;

            }
        });

        collection.addView(layout);

        return layout;
    }

    public void setItemClickListner(Clicked clickListner) {

        this.clickListner = clickListner;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {

        return data.keySet().size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public interface Clicked {

        void itemClicked(int pos, String message);
    }
}