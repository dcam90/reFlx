package multitouch.android.vogella.com.reflx;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class howtoplay2 extends Activity {
    ImageButton next_button2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.howtoplay2);
    }

    @Override
    public void onResume() {
        super.onResume();
        next_button2 = (ImageButton)findViewById(R.id.okletsplay);
        next_button2.setImageResource(R.drawable.okletsplay); //put the button back to its normal state
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void onClick3(View view) {
        MediaPlayer click = MediaPlayer.create(this, R.raw.click); //add click sound to button
        click.start();
        next_button2 = (ImageButton)findViewById(R.id.okletsplay);
        next_button2.setImageResource(R.drawable.okletsplayyellow);
        startActivity(new Intent(this, MainActivity.class));
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); //transition
    }
}
