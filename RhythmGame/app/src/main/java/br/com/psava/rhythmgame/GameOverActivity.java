package br.com.psava.rhythmgame;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by patricksava on 19/09/16.
 */

public class GameOverActivity extends AppCompatActivity {

    private Button btnYes;
    private Button btnNo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        //TODO: ler parametros do bundle

        btnYes = (Button) findViewById(R.id.game_over_button_yes);
        btnNo  = (Button) findViewById(R.id.game_over_button_no);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(GameOverActivity.this, GameActivity.class);

                //TODO: parametros no intent

                startActivity(it);
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(GameOverActivity.this, PhaseSelectionActivity.class);

                //TODO: parametros no intent

                startActivity(it);
            }
        });
    }
}
