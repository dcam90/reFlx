package multitouch.android.vogella.com.reflx;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class title extends Activity {
    private Boolean check_sound = true; //used for muting the title screen's music
    MediaPlayer music = new MediaPlayer();
    ImageButton start_button, score_button, htp_button;
    ImageView background;
    AnimationDrawable animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.titlescreen);
        ////background animation (only for titlescreen due to performance)
        background = (ImageView)findViewById(R.id.back);
        background.setBackgroundResource(R.drawable.background_animation);
        animation = (AnimationDrawable)background.getBackground();
        animation.start();
        ////background music
        music = MediaPlayer.create(this, R.raw.mainmenumusic); //main menu music
        music.start();
        music.setLooping(true);
    }

    @Override
    public void onResume() { //deal with the case where the user returns back to the main menu
        super.onResume();
        start_button = (ImageButton)findViewById(R.id.startbutton);
        start_button.setImageResource(R.drawable.playimage2); //put the button back to its normal state
        score_button = (ImageButton)findViewById(R.id.scorebutton);
        score_button.setImageResource(R.drawable.highscore); //put the button back to its normal state
        htp_button = (ImageButton)findViewById(R.id.htpbutton);
        htp_button.setImageResource(R.drawable.howtoplay2);
    }

    @Override
    public void onPause() {
        super.onPause();
        music.stop();
        animation.stop();
    }

    //start button intent (called in the titlescreen.xml)
    public void playClick(View view) {
        MediaPlayer click = MediaPlayer.create(this, R.raw.click); //add click sound to button
        click.start();
        start_button = (ImageButton)findViewById(R.id.startbutton);
        start_button.setImageResource(R.drawable.playimage2click); //animate the button to appear yellow
        music.stop(); //stop the main menu music
        startActivity(new Intent(this, MainActivity.class));
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); //transition
    }

    //to see high scores
    public void scoreClick(View view) {
        MediaPlayer click = MediaPlayer.create(this, R.raw.click); //add click sound to button
        click.start();
        score_button = (ImageButton)findViewById(R.id.scorebutton);
        score_button.setImageResource(R.drawable.highscoreclicked); //animate the button to appear yellow
        startActivity(new Intent(this, highscores.class));
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); //transition
    }

    //how to play
    public void howToPlay(View view) {
        MediaPlayer click = MediaPlayer.create(this, R.raw.click); //add click sound to button
        click.start();
        htp_button = (ImageButton)findViewById(R.id.htpbutton);
        htp_button.setImageResource(R.drawable.howtoplay2yellow);
        startActivity(new Intent(this, howtoplay.class));
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); //transition
    }

    //sound button
    public void changeSound(View view) {
        ImageButton sound = (ImageButton)findViewById(R.id.soundbutton);
        if (check_sound) {
            music.stop();
            sound.setImageResource(R.drawable.nosound); //mute the sound and display the no sound icon
            check_sound = false;
        }
        else {
            music = MediaPlayer.create(this, R.raw.mainmenumusic); //main menu music
            music.start();
            sound.setImageResource(R.drawable.sound); //unmute the sound
            check_sound = true;
        }
    }
}
