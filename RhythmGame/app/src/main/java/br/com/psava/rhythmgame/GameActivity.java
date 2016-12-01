package br.com.psava.rhythmgame;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.HashMap;

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
    private SoundPool soundPool;
    private int hitSoundId;
    private boolean hitSoundLoaded;

    private int screenWidth = 0;
    private int screenHeight = 0;
    private int score = 0;
    private int time = 0;
    private int runtime = 0;
    private int combo = 0;
    private int maxCombo = 0;
    private int hits = 0;
    private int totalMovs = 1;
    private float life = 100.0f;
    private int tick = 0;
    private int actIndex = 0;

    private int lastHitPoints = -1;

    private JSONObject songData;
    private JSONArray movArray;

    private Handler gameTimer = new Handler();
    private Runnable gameTick = new Runnable() {
        @Override
        public void run() {
            gameTimer.postDelayed(this, 100);
            life -= 0.5;
            runtime += 100;
            try {
                if(movArray == null) {
                    movArray = songData.getJSONArray("movements");
                    totalMovs = movArray.length();
                }
                JSONObject movementData = movArray.getJSONObject(actIndex);
                long showAt = movementData.getLong("mov_show_ms");
                if (showAt - HitMovement.TIMING <= runtime) {
                    drawView((float) movementData.getDouble("mov_x"), (float) movementData.getDouble("mov_y"),
                            movementData.getInt("mov_seq_color"));
                    actIndex++;
                }
            } catch (JSONException ex) {
                if (movArray != null && actIndex >= movArray.length()) {
                    try {
                        JSONObject movementData = movArray.getJSONObject(totalMovs - 1);
                        long lastShowAt = movementData.getLong("mov_show_ms");
                        if(lastShowAt + 4000 <= runtime)
                            gameResults();
                    } catch (Exception exInt) {
                        Log.e(TAG, exInt.getClass().getName());
                    }
                }
            } catch (Exception ex) {
                Log.e(TAG, ex.getClass().getName());
            }
            updateStats();

            if(life <= 0){
                gameOver();
            }
        }
    };

    private void gameResults() {
        gameTimer.removeCallbacks(gameTick);

        Intent it = new Intent(GameActivity.this, GameResultsActivity.class);
        it.putExtra("score", score);
        it.putExtra("maxcombo", maxCombo);
        it.putExtra("maxmovs", totalMovs);
        it.putExtra("hits", hits);
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
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                                       int status) {
                hitSoundLoaded = true;
            }
        });
        hitSoundId = soundPool.load(this, R.raw.normalshot, 1);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels;
        screenWidth  = displaymetrics.widthPixels;

        readSongDataFromFile("Song1.json");

        startGame();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gameTimer.removeCallbacks(gameTick);
        musicPlayer.stop();
        musicPlayer.release();
    }

    private void readSongDataFromFile(String filename) {
        try {
            File file = new File(Environment.getExternalStorageDirectory(), ".rhythmgame/" + filename);
            FileInputStream stream = new FileInputStream(file);
            String jsonStr = null;
            try {
                FileChannel fc = stream.getChannel();
                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

                jsonStr = Charset.defaultCharset().decode(bb).toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                stream.close();
            }
            songData = new JSONObject(jsonStr);
        } catch (Exception ex) {
            Toast.makeText(this, "Erro na leitura do arquivo de movimentos", Toast.LENGTH_LONG).show();
        }
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
            soundPool.play(hitSoundId, 1, 1, 1, 0, 1);
            ++combo;
            ++hits;
            maxCombo = Math.max(maxCombo, combo);
            score += hitPoints * (1 + combo/10);
            if(hitPoints > HitMovement.HIT_GOOD)
                life = Math.min(life + 10, 100);
        } else {
            combo = 0;
            life -= 10;
        }
        lastHitPoints = hitPoints;
        updateStats();
    }

    private void drawView(float x, float y, int color){
        HitMovement hm = new HitMovement(this, this, x, y, color);

        mainView.addView(hm);
    }

    private void drawViewRandom(){
        float x = Math.min((float)Math.random() * screenWidth, screenWidth - 200);
        float y = Math.min((float)Math.random() * screenHeight + 36, screenHeight - 236);
        HitMovement hm = new HitMovement(this, this, x, y, Color.CYAN);

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
