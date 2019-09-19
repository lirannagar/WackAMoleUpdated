package com.game.whackamole;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardsActivity extends AppCompatActivity {

    ListView listView;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        // Set to fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_leaderboards);

        String[] mProjection =
                {
                        ScoresProvider.NAME,
                        ScoresProvider.LOCATION,
                        ScoresProvider.SCORE
                };

        final Cursor mCursor = getContentResolver().query(
                ScoresProvider.CONTENT_URI,
                mProjection,
                null,
               null,
                ScoresProvider.SCORE + " DESC");





        listView=findViewById(R.id.listView);
        final List<String> names =new ArrayList<String>();
        final List<Integer> scores =new ArrayList<Integer>();
        final List<String> locations=new ArrayList<String>();
        for(int i=0;i<10&&i<mCursor.getCount();i++){
            mCursor.moveToNext();
            names.add(mCursor.getString(mCursor.getColumnIndex(ScoresProvider.NAME)));
            locations.add(mCursor.getString(mCursor.getColumnIndex(ScoresProvider.LOCATION)));
            scores.add(mCursor.getInt(mCursor.getColumnIndex(ScoresProvider.SCORE)));
        }


        back=findViewById(R.id.lead_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        String[] namesArray=new String[names.size()];
        namesArray=names.toArray(namesArray);
        LeaderboardsAdapter adapter=new LeaderboardsAdapter(getApplicationContext(),namesArray);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), LocationActivity.class);

                    intent.putExtra("location", locations.get(i));
                    intent.putExtra("name",names.get(i));
                    intent.putExtra("position",i+1);

                    intent.putExtra("score",scores.get(i));
                    startActivity(intent);


            }
        });
    }
}
