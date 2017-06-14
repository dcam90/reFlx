package multitouch.android.vogella.com.reflx;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class howtoplay extends Activity {
    ImageButton next_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.howtoplay);
    }

    @Override
    public void onResume() {
        super.onResume();
        next_button = (ImageButton)findViewById(R.id.nextButton);
        next_button.setImageResource(R.drawable.nextbutton); //put the button back to its normal state
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void onClick2(View view) {
        MediaPlayer click = MediaPlayer.create(this, R.raw.click); //add click sound to button
        click.start();
        next_button = (ImageButton)findViewById(R.id.nextButton);
        next_button.setImageResource(R.drawable.nextbuttonyellow);
        startActivity(new Intent(this, howtoplay2.class));
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); //transition
    }
}
