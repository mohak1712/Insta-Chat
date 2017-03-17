package social.chat.whatsapp.fb.messenger.messaging;

import android.animation.ValueAnimator;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.key;


/**
 * Created by mohak on 22/1/17.
 */

public class FloatingBubble extends Service {

    /**
     * window manager
     */
    private WindowManager windowManager;

    /**
     * image view for stopping the service
     */
    private ImageView removeBubble;

    /**
     * initial exact coordinates of chat head
     */
    private int x_init_cord, y_init_cord;

    /**
     * initial relative coordinates of chat head
     */
    private int x_init_margin, y_init_margin;

    /**
     * width and height of device
     */
    private int widthOfDev, heightOfDev;

    /**
     * arranges data in key values pair
     */
    private LinkedHashMap<String, ArrayList<NotificationModel>> listHashMap = new LinkedHashMap<>();

    /**
     * ViewPager Adapter
     */
    CustomPagerAdapter adapter;

    /**
     * contains list of keys
     */
    ArrayList<String> keys = new ArrayList<>();

    /**
     * previous location of chat head
     */
    private int click_x, click_y;

    /**
     * check if chat window is attached
     */
    boolean isWindowAttached = false;

    /**
     * bubble view
     */

    CircleImageView bubble;

    /**
     * update horizontal scrollview based on key value
     */

    boolean isKeyAvailable = false, updatePager = false;


    private WindowManager.LayoutParams imageWindowParams;
    private LinearLayout removeView, bubbleView;
    private LayoutInflater inflater;
    private ArrayList<NotificationModel> msgsData;
    private LinearLayout chatLinear;
    private ViewPager view_pager;
    private RelativeLayout relative;
    private HorizontalScrollView horizontal_scroller;
    private LinearLayout horizontalLinearLayout;
    private boolean resetAdapter;


    @Override
    public void onCreate() {

        super.onCreate();


        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displaymetrics);
        heightOfDev = displaymetrics.heightPixels;
        widthOfDev = displaymetrics.widthPixels;


        addRemoveView();
        addBubbleView();

        bubble.setOnTouchListener(new View.OnTouchListener() {

            boolean isLongclick = false, inBound = false;
            long time_start = 0, time_end = 0;

            Handler handler_longClick = new Handler();
            Runnable runnable_longClick = new Runnable() {
                @Override
                public void run() {

                    isLongclick = true;
                }
            };

            @Override
            public boolean onTouch(View view, MotionEvent event) {


                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) bubbleView.getLayoutParams();
                WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();


                int x_cord = (int) event.getRawX();
                int y_cord = (int) event.getRawY();

                int x_cord_Destination, y_cord_Destination;


                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        time_start = System.currentTimeMillis();
                        handler_longClick.postDelayed(runnable_longClick, 800);

                        x_init_cord = x_cord;
                        y_init_cord = y_cord;

                        x_init_margin = layoutParams.x;
                        y_init_margin = layoutParams.y;


                        return true;

                    case MotionEvent.ACTION_MOVE:
                        int x_diff_move = x_cord - x_init_cord;
                        int y_diff_move = y_cord - y_init_cord;

                        x_cord_Destination = x_init_margin + x_diff_move;
                        y_cord_Destination = y_init_margin + y_diff_move;

                        if (isLongclick) {


                            if (x_cord >= (widthOfDev / 2 - (removeBubble.getWidth())) && x_cord <= (widthOfDev / 2 + (removeBubble.getWidth()))
                                    && y_cord > (heightOfDev - ((removeBubble.getHeight() * 2)))) {

                                Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                v.vibrate(5);
                                inBound = true;

                            } else {

                                inBound = false;

                                int x_cord_remove = ((widthOfDev - (removeBubble.getWidth())) / 2);
                                int y_cord_remove = (heightOfDev - ((removeBubble.getHeight() * 2)));

                                param_remove.x = x_cord_remove;
                                param_remove.y = y_cord_remove;

                                windowManager.updateViewLayout(removeView, param_remove);
                                removeView.setVisibility(View.VISIBLE);
                            }

                        }

                        layoutParams.x = x_cord_Destination;
                        layoutParams.y = y_cord_Destination;

                        windowManager.updateViewLayout(bubbleView, layoutParams);

                        return true;
                    case MotionEvent.ACTION_UP:

                        isLongclick = false;
                        removeView.setVisibility(View.GONE);

                        if (inBound) {
                            inBound = false;
                            stopSelf();
                            return true;
                        }

                        int x_diff = x_cord - x_init_cord;
                        int y_diff = y_cord - y_init_cord;

                        if (Math.abs(x_diff) < 20 && Math.abs(y_diff) < 20) {

                            time_end = System.currentTimeMillis();
                            if ((time_end - time_start) < 300) {

                                if (!isWindowAttached) {

                                    animateView(layoutParams.x, 0, layoutParams.y, 0);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            addChatLayout();

                                        }
                                    }, 300);
                                } else {

                                    removeChatWindow();

                                }

                            }


                        } else {

                            if (layoutParams.x < widthOfDev / 2)
                                animateView(layoutParams.x, 0, layoutParams.y, layoutParams.y);
                            else
                                animateView(layoutParams.x, (widthOfDev - bubble.getWidth()), layoutParams.y, layoutParams.y);
                        }

                        return true;

                }
                return false;
            }
        });
    }

    private void addChatLayout() {


        isWindowAttached = true;
        imageWindowParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);


        view_pager.setVisibility(View.VISIBLE);
        relative.setVisibility(View.VISIBLE);
        horizontal_scroller.setVisibility(View.VISIBLE);

        windowManager.updateViewLayout(bubbleView, imageWindowParams);

    }


    private void animateView(int startX, int endX, int startY, int endY) {

        ValueAnimator translateX = ValueAnimator.ofInt(startX, endX);
        ValueAnimator translateY = ValueAnimator.ofInt(startY, endY);

        translateX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                updateViewLayout(bubbleView, val, -1);

            }
        });

        translateX.setDuration(250).start();

        translateY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                updateViewLayout(bubbleView, -1, val);

            }
        });

        translateY.setDuration(250).start();
    }

    private void updateViewLayout(LinearLayout bubbleView, int animateX, int animateY) {


        if (animateX != -1) imageWindowParams.x = animateX;
        if (animateY != -1) imageWindowParams.y = animateY;

        windowManager.updateViewLayout(bubbleView, imageWindowParams);
    }


    private void addBubbleView() {

        bubbleView = (LinearLayout) inflater.inflate(R.layout.bubble_layout, null);
        bubble = (CircleImageView) bubbleView.findViewById(R.id.bubble);

        relative = (RelativeLayout) bubbleView.findViewById(R.id.rel);
        horizontal_scroller = (HorizontalScrollView) bubbleView.findViewById(R.id.scroller);
        view_pager = (ViewPager) bubbleView.findViewById(R.id.pager);
        horizontalLinearLayout = (LinearLayout) bubbleView.findViewById(R.id.horizontalLinear);


//        ImageView bubble = (ImageView) bubbleView.findViewById(R.id.bubble);
//        bubble.setImageResource(R.mipmap.ic_launcher);

        imageWindowParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        imageWindowParams.gravity = Gravity.TOP | Gravity.LEFT;
        imageWindowParams.x = 10;
        imageWindowParams.y = heightOfDev / 2;

        windowManager.addView(bubbleView, imageWindowParams);

        horizontalLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

//        for (int i = 0; i < horizontalLinearLayout.getChildCount(); i++) {
//
//            final int finalI = i;
//
//            horizontalLinearLayout.getChildAt(i).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    LinearLayout linearLayout = (LinearLayout) horizontalLinearLayout.getChildAt(finalI);
//                    TextView textView = (TextView) linearLayout.findViewById(R.id.title);
//
//                    Toast.makeText(FloatingBubble.this, "clicked " + finalI, Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//

        view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {

                horizontal_scroller.scrollTo(horizontalLinearLayout.getChildAt(position).getLeft(), 0);
                horizontalLinearLayout.getChildAt(position).setBackgroundColor(Color.parseColor("#d3d3d3"));

                for (int i = 0; i < horizontalLinearLayout.getChildCount(); i++) {

                    if (i != position)
                        horizontalLinearLayout.getChildAt(i).setBackgroundColor(Color.parseColor("#065E52"));
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void removeChatWindow() {

        isWindowAttached = false;

        horizontal_scroller.setVisibility(View.GONE);
        relative.setVisibility(View.GONE);
        view_pager.setVisibility(View.GONE);

        imageWindowParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        imageWindowParams.gravity = Gravity.TOP | Gravity.LEFT;
        imageWindowParams.x = 10;
        imageWindowParams.y = heightOfDev / 2;
        windowManager.updateViewLayout(bubbleView, imageWindowParams);


    }

    private void addRemoveView() {

        removeView = (LinearLayout) inflater.inflate(R.layout.remove_bubble, null);
        removeBubble = (ImageView) removeView.findViewById(R.id.removeImg);
        removeBubble.setImageResource(R.drawable.circle_cross);
        WindowManager.LayoutParams paramRemove = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        paramRemove.gravity = Gravity.TOP | Gravity.LEFT;


        removeView.setVisibility(View.GONE);
        windowManager.addView(removeView, paramRemove);


    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (windowManager != null && bubbleView != null) {
            windowManager.removeViewImmediate(bubbleView);
            windowManager.removeView(removeView);

            try {

                windowManager.removeView(chatLinear);
            } catch (Exception e) {

            /* view not found */

            }

        }

    }


    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        if (intent != null && intent.getParcelableArrayListExtra(Constants.msgs) != null) {


            Toast.makeText(this, "called", Toast.LENGTH_SHORT).show();

            msgsData = intent.getParcelableArrayListExtra(Constants.msgs);
//            Toast.makeText(this, "" + msgsData.size(), Toast.LENGTH_SHORT).show();

            Toast.makeText(this, "else " + msgsData.get(msgsData.size() - 1).getUserName(), Toast.LENGTH_SHORT).show();

            arrangeData();

            for (String key : listHashMap.keySet()) {

                keys.add(key);

                LinearLayout headLinear = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.scrolltext, null);
                TextView tv = (TextView) headLinear.findViewById(R.id.title);
                tv.setText(key);

                for (int i = 0; i < horizontalLinearLayout.getChildCount(); i++) {

                    LinearLayout linearLayout = (LinearLayout) horizontalLinearLayout.getChildAt(i);
                    TextView textView = (TextView) linearLayout.findViewById(R.id.title);
                    if (textView.getText().equals(key)) {
                        isKeyAvailable = true;
                        break;
                    } else {
                        isKeyAvailable = false;
                    }

                }

                if (!isKeyAvailable) {
                    horizontalLinearLayout.addView(headLinear);
                }
            }

            horizontal_scroller.setHorizontalScrollBarEnabled(false);

            for (int i = 0 ; i < horizontalLinearLayout.getChildCount() ; i++){

                final int finalI = i;

                horizontalLinearLayout.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Toast.makeText(FloatingBubble.this, "clciked", Toast.LENGTH_SHORT).show();
                        view_pager.setCurrentItem(finalI);
                    }
                });
            }

            RecyclerView recyclerview = null;
            String key2 = null;

            for (String key : listHashMap.keySet()) {

                recyclerview = (RecyclerView) view_pager.findViewWithTag(key).findViewById(R.id.list);

                if (recyclerview != null)
                    ((ListAdapter) (recyclerview.getAdapter())).swap(listHashMap.get(key));
                else {

                    updatePager = true;
                    break;
                }

//                    if (recyclerview == null) {
//                        updatePager = true;
//                        key2 = key;
//                    adapter.updateView(key, listHashMap.get(key));
//                        ((ListAdapter) (recyclerview.getAdapter())).swap(listHashMap.get(key));
////                        break;
//
//                    } else

//                if (recyclerview != null)
//
//                else {

//                    int pos = view_pager.getCurrentItem();
//                    adapter = new CustomPagerAdapter(this, listHashMap, keys);
//                    view_pager.setAdapter(adapter);
////                        adapter.updateView(key, listHashMap.get(key));
//                    view_pager.setCurrentItem(pos);
//                        view_pager.findViewById(R.id.list).setTag(key);
//                        recyclerview = (RecyclerView) view_pager.findViewWithTag(key);
//                        ((ListAdapter) (recyclerview.getAdapter())).swap(listHashMap.get(key));

//                }
//
//                if (updatePager) {
//                    adapter = new CustomPagerAdapter(this, listHashMap, keys);
//                    view_pager.setAdapter(adapter);
////                    ((ListAdapter) (recyclerview.getAdapter())).swap(listHashMap.get(key2));
//
//                    updatePager = false;
//                }
            }


            if (updatePager) {

                int pos = view_pager.getCurrentItem();
                adapter = new CustomPagerAdapter(this, listHashMap, keys);
                view_pager.setAdapter(adapter);
//                        adapter.updateView(key, listHashMap.get(key));
                view_pager.setCurrentItem(pos);

                updatePager = false;
            }

            view_pager.findViewWithTag(R.id.send).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(FloatingBubble.this, ""+view_pager.getCurrentItem(), Toast.LENGTH_SHORT).show();

                    PendingIntent pIntent = intent.getParcelableExtra("p");
                    try {
                        pIntent.send();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }

                }
            });

        }

        return super.onStartCommand(intent, flags, startId);
    }


    public void arrangeData() {

        listHashMap.clear();

        for (int i = 0; i < msgsData.size(); i++) {

            if (msgsData.get(i).getGroup().equals("-null_123")) {

                if (listHashMap.containsKey(msgsData.get(i).getUserName())) {
                    listHashMap.get(msgsData.get(i).getUserName()).add(msgsData.get(i));

                } else {
                    ArrayList<NotificationModel> singleDataList = new ArrayList<>();
                    singleDataList.add(msgsData.get(i));
                    listHashMap.put(msgsData.get(i).getUserName(), singleDataList);
                }

            } else {

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


}