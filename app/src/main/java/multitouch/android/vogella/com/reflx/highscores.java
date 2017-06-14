package multitouch.android.vogella.com.reflx;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class highscores extends Activity {
    String fontPath = "fonts/Broken Robot.ttf"; //to set font
    ImageButton prev_button;
    TextView firstline, score, secondline, person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.highscores);
        Typeface type = Typeface.createFromAsset(getAssets(), fontPath);
        //////
        firstline = (TextView)findViewById(R.id.gamealltime);
        score = (TextView)findViewById(R.id.scoretext);
        secondline = (TextView)findViewById(R.id.gamealltime2);
        person = (TextView)findViewById(R.id.persontext);
        //////
        firstline.setTypeface(type);
        score.setTypeface(type);
        secondline.setTypeface(type);
        person.setTypeface(type);
        /////
        firstline.setText("High score: ");
        secondline.setText("Achieved by: ");
        addValue(score, person);
    }

    public void goBack(View view) {
        MediaPlayer click = MediaPlayer.create(this, R.raw.click); //add click sound to button
        click.start();
        prev_button = (ImageButton)findViewById(R.id.prevButton);
        prev_button.setImageResource(R.drawable.previousbuttonyellow);
        startActivity(new Intent(this, title.class));
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); //transition
    }

    public void addValue(TextView text, TextView text2) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        int value = prefs.getInt("the_high_score", 0);
        String name = prefs.getString("the_name", "---");
        String value_str = Integer.toString(value);
        text.setText(value_str);
        text2.setText(name);
    }
}
