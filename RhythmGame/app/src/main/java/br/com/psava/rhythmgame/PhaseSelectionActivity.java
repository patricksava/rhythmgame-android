package br.com.psava.rhythmgame;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class PhaseSelectionActivity extends AppCompatActivity {

    private ListView songList;

    public static final int REQUEST_WRITE_STORAGE = 0x23;
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
                startActivity(it);
            }
        });

        findViewById(R.id.phase_selection_project).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasPermission = (ContextCompat.checkSelfPermission(PhaseSelectionActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
                if (!hasPermission) {
                    ActivityCompat.requestPermissions(PhaseSelectionActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_WRITE_STORAGE);
                } else {
                    Intent it = new Intent(PhaseSelectionActivity.this, ProjectPhaseActivity.class);
                    startActivity(it);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent it = new Intent(PhaseSelectionActivity.this, ProjectPhaseActivity.class);
                    startActivity(it);
                } else {
                    Toast.makeText(this, "The app was not allowed to write to your storage. " +
                            "Hence, it cannot function properly. Please consider granting it this permission",
                            Toast.LENGTH_LONG).show();
                }
            }
        }

    }
}
