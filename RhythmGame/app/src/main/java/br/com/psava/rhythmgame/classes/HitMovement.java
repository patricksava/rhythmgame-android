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

    private View centralView;
    private View timingView;

    private int timing = 100;
    private static final int MAX_TIMING = 100;

    private final int TIMING_REDUCTION = 2;

    public static final int HIT_MISS = 0;
    public static final int HIT_GOOD = 50;
    public static final int HIT_GREAT = 100;
    public static final int HIT_PERFECT = 300;

    public static final int MISS_HIT_FACTOR = 35;
    public static final int GOOD_HIT_FACTOR = 65;
    public static final int PERFECT_HIT_FACTOR  = 90;

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
                timeoutHandler.postDelayed(tick, 25);
            }
        }
    };


    public HitMovement(Context c, final MovementActionClickListener listener, float x, float y) {
        super(c);
        centralView = new View(c);
        timingView = new View(c);

        this.timeoutHandler = new Handler();
        this.listener = listener;
        this.size = 100;
        this.setLayoutParams(new ViewGroup.LayoutParams(size + MAX_TIMING, size + MAX_TIMING));
        this.setX(x);
        this.setY(y);

        centralView.setLayoutParams(new ViewGroup.LayoutParams(size, size));
        centralView.setX(MAX_TIMING/2);
        centralView.setY(MAX_TIMING/2);
        centralView.setBackgroundResource(R.drawable.solid_blue);
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
        timingView.setBackgroundResource(R.drawable.circle_line);

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
