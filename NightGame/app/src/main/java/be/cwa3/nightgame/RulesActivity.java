package be.cwa3.nightgame;

import android.content.DialogInterface;
import android.graphics.Color;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created by Jean Frerot on 26/11/15.
 */

public class RulesActivity extends AppCompatActivity {
    TextView titleDefenders, titleAttackers, textDefenders, textAttackers;
    private boolean isVisible;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);


        titleDefenders = (TextView) findViewById(R.id.title_defenders);
        titleAttackers = (TextView) findViewById(R.id.title_attackers);
        textDefenders = (TextView) findViewById(R.id.text_defenders);
        textAttackers = (TextView) findViewById(R.id.text_attackers);

        titleDefenders.setTextColor(Color.rgb(0,0,155));
        titleAttackers.setTextColor(Color.rgb(0,0,155));

        titleDefenders.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textDefenders.getVisibility()==View.GONE)
                    textDefenders.setVisibility(View.VISIBLE);
                else {
                    textDefenders.setVisibility(View.GONE);
                }
            }
        });

        titleAttackers.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textAttackers.getVisibility() == View.GONE)
                    textAttackers.setVisibility(View.VISIBLE);
                else {
                    textAttackers.setVisibility(View.GONE);
                }
            }
        });


    }
}

