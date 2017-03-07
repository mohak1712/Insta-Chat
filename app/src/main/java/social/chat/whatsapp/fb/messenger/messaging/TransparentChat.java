package social.chat.whatsapp.fb.messenger.messaging;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class TransparentChat extends AppCompatActivity implements ChatFragment.OnFragmentInteractionListener {


    private Toolbar toolbar;
    private LinearLayout mainLinear;
    private ViewPager pager;
    private TabLayout tabLayout;
    private EventBus bus = EventBus.getDefault();
    private ArrayList<NotificationModel> msgsData;
    private HashMap<String, ArrayList<NotificationModel>> listHashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);

        msgsData = getIntent().getParcelableArrayListExtra(Constants.msgs);

        if (msgsData == null)
            return;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainLinear = (LinearLayout) findViewById(R.id.mainLinear);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);

        arrangeData();
        setupViewPager(pager);
        tabLayout.setupWithViewPager(pager);
        getContectNumber();

    }


    private void setupViewPager(ViewPager viewPager) {

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());

        for (String key : listHashMap.keySet()) {

            adapter.addFrag(ChatFragment.newInstance(listHashMap.get(key)), key);
        }
        viewPager.setAdapter(adapter);
    }


    void getContectNumber() {

        ArrayList<String> myWhatsappContacts = new ArrayList<String>();


        Cursor cursor = getContentResolver().query(
                ContactsContract.RawContacts.CONTENT_URI,
                new String[]{ContactsContract.RawContacts.CONTACT_ID},
                ContactsContract.RawContacts.ACCOUNT_TYPE + "= ?" + " AND " + ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY + "= ?",
                new String[]{"com.whatsapp", "Mummy"},
                null);


        if (cursor == null) {
            return;
        }

        while (cursor.moveToNext()) {

            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID));
            Toast.makeText(this, "" + id, Toast.LENGTH_SHORT).show();

            Cursor whatsAppContactCursor = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    new String[]{id}, null);

            if (whatsAppContactCursor != null) {

                whatsAppContactCursor.moveToFirst();
                String number = whatsAppContactCursor.getString(whatsAppContactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Toast.makeText(this, "" + number, Toast.LENGTH_SHORT).show();

                whatsAppContactCursor.close();

            }
        }

        cursor.close();
    }


    void arrangeData() {

        for (int i = 0; i < msgsData.size(); i++) {

            if (msgsData.get(i).getGroup()==null){

                if (listHashMap.containsKey(msgsData.get(i).getUserName())) {
                    listHashMap.get(msgsData.get(i).getUserName()).add(msgsData.get(i));

                } else {
                    ArrayList<NotificationModel> singleDataList = new ArrayList<>();
                    singleDataList.add(msgsData.get(i));
                    listHashMap.put(msgsData.get(i).getUserName(), singleDataList);
                }

            }else{


                if (listHashMap.containsKey(msgsData.get(i).getGroup())) {
                    listHashMap.get(msgsData.get(i).getGroup()).add(msgsData.get(i));

                } else {
                    ArrayList<NotificationModel> singleDataList = new ArrayList<>();
                    singleDataList.add(msgsData.get(i));
                    listHashMap.put(msgsData.get(i).getGroup(), singleDataList);
                }

            }


        }
    }


    @Override
    public void onBackPressed() {

        bus.post(true);
        finish();
        overridePendingTransition(0, 0);
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
