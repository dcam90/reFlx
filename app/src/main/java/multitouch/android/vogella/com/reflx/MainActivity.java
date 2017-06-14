package multitouch.android.vogella.com.reflx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    private MainView view; //to display and manage the game

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout the_layout = (RelativeLayout)findViewById(R.id.relativeLayout);
        view = new MainView(this, the_layout);
        //constructor requires: context (this activity), preference object, and the relative layout
        the_layout.addView(view, 0); //add the view to the layout at position 0
    }

    //we need to override both onPause and onResume
    @Override
    public void onPause() {
        super.onPause();
        view.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        view.resume(this);
    }
}
