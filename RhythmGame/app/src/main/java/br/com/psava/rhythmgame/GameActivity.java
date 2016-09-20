package br.com.psava.rhythmgame;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.com.psava.rhythmgame.classes.HitMovement;
import br.com.psava.rhythmgame.classes.MovementAction;
import br.com.psava.rhythmgame.interfaces.MovementActionClickListener;

/**
 * Created by patricksava on 19/09/16.
 */

public class GameActivity extends AppCompatActivity implements MovementActionClickListener {

    private static final String TAG = "GameActivity";

    private FrameLayout mainView;
    private TextView txvScore;
    private TextView txvCombo;
    private View lifeRemainingView;
    private View lifeOverView;

    private MediaPlayer musicPlayer;

    private int screenWidth = 0;
    private int screenHeight = 0;
    private int score = 0;
    private int time = 0;
    private int combo = 0;
    private int life = 100;
    private int tick = 0;

    private int lastHitPoints = -1;

    private Handler gameTimer = new Handler();
    private Runnable gameTick = new Runnable() {
        @Override
        public void run() {
            gameTimer.postDelayed(this, 200);
            life -= 1;
            if (++tick % 5 == 0)
                drawView();

            updateStats();

            if(life <= 0){
                gameOver();
            }
        }
    };

    private void gameResults() {
        gameTimer.removeCallbacks(gameTick);

        Intent it = new Intent(GameActivity.this, GameOverActivity.class);
        startActivity(it);
    }

    private void gameOver() {
        gameTimer.removeCallbacks(gameTick);

        Intent it = new Intent(GameActivity.this, GameOverActivity.class);
        startActivity(it);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mainView = (FrameLayout) findViewById(R.id.activity_game);
        txvScore = (TextView) findViewById(R.id.game_text_score);
        txvCombo = (TextView) findViewById(R.id.game_text_combo);
        lifeRemainingView = findViewById(R.id.game_life_remaining_view);
        lifeOverView = findViewById(R.id.game_life_over_view);
        getSupportActionBar().hide();

        musicPlayer = MediaPlayer.create(this, R.raw.centroid);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels;
        screenWidth  = displaymetrics.widthPixels;

        startGame();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gameTimer.removeCallbacks(gameTick);
        musicPlayer.stop();
        musicPlayer.release();
    }

    private void startGame(){
        gameTimer.post(gameTick);

        musicPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (life <= 0)
                    gameOver();

                gameResults();
            }
        });
        time = musicPlayer.getDuration();
        musicPlayer.start();

    }

    public void handleMovementAction(MovementAction ma, int hitPoints){
        mainView.removeView(ma);

        if(hitPoints > 0){
            MediaPlayer mp = MediaPlayer.create(this, R.raw.normalshot);
            mp.start();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.stop();
                    mp.release();
                }
            });

            combo++;
            score += hitPoints * (1 + combo/10);
            life = Math.min(life + 10, 100);
        } else {
            combo = 0;
            life -= 10;
        }
        lastHitPoints = hitPoints;
        updateStats();
    }

    private void drawView(){
        float x = Math.min((float)Math.random() * screenWidth, screenWidth - 200);
        float y = Math.min((float)Math.random() * screenHeight + 36, screenHeight - 236);
        HitMovement hm = new HitMovement(this, this, x, y);

        mainView.addView(hm);
    }

    private void updateStats(){
        txvScore.setText("Score: " + this.score + ((lastHitPoints != -1)?" + " + lastHitPoints : "") );
        txvCombo.setText("Combo: " + this.combo + "x");

        LinearLayout.LayoutParams lifeRemParams = (LinearLayout.LayoutParams) lifeRemainingView.getLayoutParams();
        LinearLayout.LayoutParams lifeOverParams = (LinearLayout.LayoutParams) lifeOverView.getLayoutParams();

        lifeRemParams.weight = life/100.0f;
        lifeOverParams.weight = 1.0f - life/100.0f;

        lifeRemainingView.setLayoutParams(lifeRemParams);
        lifeOverView.setLayoutParams(lifeOverParams);
    }
}
