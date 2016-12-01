package br.com.psava.rhythmgame;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by patricksava on 30/11/16.
 */

public class GameResultsActivity extends AppCompatActivity {

    private TextView txvScore;
    private TextView txvMaxCombo;
    private TextView txvResult;

    private int score;
    private int maxCombo;
    private int maxMovs;
    private int rightMovs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_results);

        Bundle bundle = getIntent().getExtras();
        score = bundle.getInt("score");
        maxCombo = bundle.getInt("maxcombo");
        maxMovs = bundle.getInt("maxmovs");
        rightMovs = bundle.getInt("hits");

        double hitRatio = ((double)rightMovs) / ((double)maxMovs);
        txvScore    = (TextView) findViewById(R.id.game_result_score);
        txvMaxCombo = (TextView) findViewById(R.id.game_result_maxcombo);
        txvResult   = (TextView) findViewById(R.id.game_result_result);

        txvScore.setText("Pontuação: " + String.valueOf(score));
        txvMaxCombo.setText("Max Combo: " + String.valueOf(maxCombo));
        txvResult.setText("Nota: " + convertHitRatio(hitRatio));

        findViewById(R.id.game_result_replay_song).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(GameResultsActivity.this, GameActivity.class);
                startActivity(it);
            }
        });

        findViewById(R.id.game_result_new_song).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(GameResultsActivity.this, PhaseSelectionActivity.class);
                startActivity(it);
            }
        });
    }

    private String convertHitRatio(double hitRatio) {
        if (hitRatio < 0.5) return "D";
        else if (hitRatio < 0.70) return "C";
        else if (hitRatio < 0.85) return "B";
        else if (hitRatio < 0.96) return "A";
        else return "S";
    }
}
