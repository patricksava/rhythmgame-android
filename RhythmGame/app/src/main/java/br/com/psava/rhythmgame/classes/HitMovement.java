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

    private Handler timeoutHandler;
    private Runnable timeout = new Runnable() {
        @Override
        public void run() {
            HitMovement.this.listener.handleMovementAction(HitMovement.this, 0);
        }
    };

    public HitMovement(Context c, MovementActionClickListener listener, float x, float y) {
        super(c);
        this.timeoutHandler = new Handler();
        this.listener = listener;
        this.size = 200;
        this.setLayoutParams(new ViewGroup.LayoutParams(size, size));
        this.setX(x);
        this.setY(y);
        this.setBackgroundResource(R.drawable.solid_blue);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                HitMovement hm = (HitMovement) v;
                timeoutHandler.removeCallbacks(timeout);

                //TODO: verificar quanto acertou pra mandar os hitPoints corretos
                hm.listener.handleMovementAction(HitMovement.this, 100);
            }
        });

        timeoutHandler.postDelayed(timeout, 2000);
    }

}
