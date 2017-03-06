package social.chat.whatsapp.fb.messenger.messaging;

import android.animation.ValueAnimator;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;


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


    private WindowManager.LayoutParams imageWindowParams;
    private LinearLayout removeView, bubbleView;
    private LayoutInflater inflater;
    private ArrayList<NotificationModel> msgsData;


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

        bubbleView.setOnTouchListener(new View.OnTouchListener() {

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
                                Toast.makeText(FloatingBubble.this, "clicked", Toast.LENGTH_SHORT).show();
                                animateView(layoutParams.x, 0, layoutParams.y, 0);
                                startNewActivity();
                            }
                        } else {


                            if (layoutParams.x < widthOfDev / 2)
                                animateView(layoutParams.x, 0, layoutParams.y, layoutParams.y);
                            else
                                animateView(layoutParams.x, (widthOfDev - bubbleView.getWidth()), layoutParams.y, layoutParams.y);
                        }

                        return true;

                }
                return false;
            }
        });
    }


    private void startNewActivity() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(FloatingBubble.this, TransparentChat.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putParcelableArrayListExtra(Constants.msgs,msgsData);
                startActivity(intent);
            }
        }, 300);

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
//        ImageView bubble = (ImageView) bubbleView.findViewById(R.id.bubble);
//        bubble.setImageResource(R.mipmap.ic_launcher);

        imageWindowParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        imageWindowParams.gravity = Gravity.TOP | Gravity.LEFT;
        imageWindowParams.x = 10;
        imageWindowParams.y = heightOfDev / 2;
        windowManager.addView(bubbleView, imageWindowParams);
    }

    private void addRemoveView() {

        removeView = (LinearLayout) inflater.inflate(R.layout.remove_bubble, null);
        removeBubble = (ImageView) removeView.findViewById(R.id.removeImg);
        removeBubble.setImageResource(R.drawable.circle_cross);
        WindowManager.LayoutParams paramRemove = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
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
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        msgsData = intent.getParcelableArrayListExtra(Constants.msgs);
        return super.onStartCommand(intent, flags, startId);
    }
}