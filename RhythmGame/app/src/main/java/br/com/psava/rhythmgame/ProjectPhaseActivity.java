package br.com.psava.rhythmgame;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.LinkedList;

import br.com.psava.rhythmgame.classes.HitMovement;
import br.com.psava.rhythmgame.classes.MovementAction;
import br.com.psava.rhythmgame.interfaces.MovementActionClickListener;
import br.com.psava.rhythmgame.io.JsonProjectWriter;

/**
 * Created by patricksava on 04/10/16.
 */

public class ProjectPhaseActivity extends AppCompatActivity implements MovementActionClickListener {

    private int screenWidth;
    private int screenHeight;

    private FrameLayout screen;

    private long start;
    private long time;

    private int sequence = 0;

    private MediaPlayer musicPlayer;

    private LinkedList<MovementMetadata> movList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_phase);

        movList = new LinkedList<>();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels;
        screenWidth  = displaymetrics.widthPixels;

        screen = (FrameLayout) findViewById(R.id.project_phase_screen);
        screen.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                registerMovement(event);
                return false;
            }
        });
        start = System.currentTimeMillis();
        musicPlayer = MediaPlayer.create(this, R.raw.centroid);
        time = musicPlayer.getDuration();
        musicPlayer.start();

        findViewById(R.id.project_phase_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishProject();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        finishProject();
    }

    private void finishProject() {
        try {
            if (musicPlayer.isPlaying()) {
                musicPlayer.stop();
                musicPlayer.release();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        prepareJsonFile();

        finish();
    }

    @Override
    public void handleMovementAction(MovementAction ma, int hitPoints) {
        screen.removeView(ma);
    }

    public void registerMovement(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x, y;
            x = event.getX();
            y = event.getY();
            long msShow = System.currentTimeMillis() - start;
            msShow -= HitMovement.MAX_TIMING/HitMovement.TIMING_REDUCTION * HitMovement.DELAY_MILLIS;

            if(movList != null && movList.size() > 0) {
                MovementMetadata lastMM = movList.getLast();
                if (msShow - lastMM.msShow >= 1000)
                    ++sequence;
            }

            MovementMetadata mm = new MovementMetadata(x, y, msShow, sequence, sequence % 4);

            movList.add(mm);
            mm.logMetadata();

            HitMovement hm = new HitMovement(ProjectPhaseActivity.this, ProjectPhaseActivity.this, x, y, sequence % 4);

            screen.addView(hm);
        }
    }

    private void prepareJsonFile(){
        try {
            JSONObject json = new JSONObject();
            json.put("song", "Song name");
            json.put("song_time", time);

            JSONArray movementsJson = new JSONArray();
            for(MovementMetadata mm : movList){
                JSONObject movement = new JSONObject();
                movement.put("mov_type", "hit");
                movement.put("mov_show_ms", mm.msShow);
                movement.put("mov_x", mm.x);
                movement.put("mov_y", mm.y);
                movement.put("mov_seq_num", mm.seqNum);
                movement.put("mov_seq_color", mm.seqColor);

                movementsJson.put(movement);
            }
            json.put("movements", movementsJson);

            Log.d("ProjectPhase", json.toString());
            JsonProjectWriter.writeJsonFile("Song1", json, this);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    private class MovementMetadata {
        float x, y;
        long msShow;
        int seqNum;
        int seqColor;

        public MovementMetadata(float x, float y, long msShow, int seqNum, int color){
            this.x = x;
            this.y = y;
            this.msShow = msShow;
            this.seqNum = seqNum;
            this.seqColor = color;
        }

        public void logMetadata(){
            Log.d("Mov Metadata", "X = " + x + " | Y = " + y);
            Log.d("Mov Metadata", "msShow = " + msShow);
            Log.d("Mov Metadata", "seqNum = " + seqNum + " | seqColor = " + seqColor);
        }
    }
}
