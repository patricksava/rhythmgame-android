package br.com.psava.rhythmgame.classes;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;

import br.com.psava.rhythmgame.R;
import br.com.psava.rhythmgame.interfaces.MovementActionClickListener;

/**
 * Created by patricksava on 19/09/16.
 */

public class HitMovement extends MovementAction {

    public static final int MAX_TIMING = 100;

    public static final int TIMING_REDUCTION = 2;

    public static final int HIT_MISS = 0;
    public static final int HIT_GOOD = 50;
    public static final int HIT_GREAT = 100;
    public static final int HIT_PERFECT = 300;

    public static final int MISS_HIT_FACTOR = 45;
    public static final int GOOD_HIT_FACTOR = 75;
    public static final int PERFECT_HIT_FACTOR  = 90;

    public static final int CENTRAL_SIZE = 100;
    public static final int DELAY_MILLIS = 25;

    public static final int TIMING = (MAX_TIMING / TIMING_REDUCTION) * DELAY_MILLIS;

    private View centralView;
    private View timingView;

    private int timing = MAX_TIMING;

    private Handler timeoutHandler;
    private Runnable tick = new Runnable() {
        @Override
        public void run() {
            timing -= TIMING_REDUCTION;
            if(timing <= 0) {
                //Timeout!
                HitMovement.this.listener.handleMovementAction(HitMovement.this, HIT_MISS);
            } else {
                //Resize outer circle
                resizeTimingCircle();
                timeoutHandler.postDelayed(tick, DELAY_MILLIS);
            }
        }
    };


    public HitMovement(Context c, final MovementActionClickListener listener, float x, float y, int color) {
        super(c);
        centralView = new View(c);
        timingView = new View(c);

        this.timeoutHandler = new Handler();
        this.listener = listener;
        this.size = CENTRAL_SIZE;
        this.setLayoutParams(new ViewGroup.LayoutParams(size + MAX_TIMING, size + MAX_TIMING));
        this.setX(x - (size + MAX_TIMING)/2.0f);
        this.setY(y - (size + MAX_TIMING)/2.0f);

        centralView.setLayoutParams(new ViewGroup.LayoutParams(size, size));
        centralView.setX(MAX_TIMING/2);
        centralView.setY(MAX_TIMING/2);

        centralView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                timeoutHandler.removeCallbacks(tick);

                int hitPoints;
                if (MAX_TIMING - timing < MISS_HIT_FACTOR)
                    hitPoints = HIT_MISS;
                else if(MAX_TIMING - timing < GOOD_HIT_FACTOR)
                    hitPoints = HIT_GOOD;
                else if (MAX_TIMING - timing < PERFECT_HIT_FACTOR)
                    hitPoints = HIT_GREAT;
                else
                    hitPoints = HIT_PERFECT;

                listener.handleMovementAction(HitMovement.this, hitPoints);
            }
        });

        timingView.setLayoutParams(new ViewGroup.LayoutParams(size + MAX_TIMING, size + MAX_TIMING));
        timingView.setX(MAX_TIMING - timing);
        timingView.setY(MAX_TIMING - timing);

        switch (color){
            case 0:
                centralView.setBackgroundResource(R.drawable.solid_blue);
                timingView.setBackgroundResource(R.drawable.circle_line_blue);
                break;
            case 1:
                centralView.setBackgroundResource(R.drawable.solid_red);
                timingView.setBackgroundResource(R.drawable.circle_line_red);
                break;
            case 2:
                centralView.setBackgroundResource(R.drawable.solid_green);
                timingView.setBackgroundResource(R.drawable.circle_line_green);
                break;
            case 3:
                centralView.setBackgroundResource(R.drawable.solid_orange);
                timingView.setBackgroundResource(R.drawable.circle_line_orange);
                break;
            default:
                centralView.setBackgroundResource(R.drawable.solid_blue);
                timingView.setBackgroundResource(R.drawable.circle_line_blue);
                break;
        }

        this.addView(timingView);
        this.addView(centralView);

        timeoutHandler.post(tick);
    }

    private void resizeTimingCircle() {
        ViewGroup.LayoutParams params = timingView.getLayoutParams();

        params.height = size + timing;
        params.width  = size + timing;

        timingView.setLayoutParams(params);
        timingView.setX((MAX_TIMING - timing)/2);
        timingView.setY((MAX_TIMING - timing)/2);
    }
}
