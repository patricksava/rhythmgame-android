package br.com.psava.rhythmgame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PhaseSelectionActivity extends AppCompatActivity {

    private ListView songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phase_selection);

        songList = (ListView) findViewById(R.id.phase_selection_list);
        songList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.songs)));

        songList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it = new Intent(PhaseSelectionActivity.this, GameActivity.class);

                //TODO: Passar parametros pela intent que forem necessarios

                startActivity(it);
            }
        });

        findViewById(R.id.phase_selection_project).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(PhaseSelectionActivity.this, ProjectPhaseActivity.class);
                startActivity(it);
            }
        });
    }
}
