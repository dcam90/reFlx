package multitouch.android.vogella.com.reflx;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainView extends View {
        //SHARED PREFERENCE VARIABLES
        private static final String the_high_score = "the_high_score"; //to access shared preferences
        private static final String the_name = "the_name"; //to access shared preferences
        private SharedPreferences prefs;
    
        //GAME COUNTERS + SCORES
        private int boxesTouched; //record the number of boxes touched
        private int score; //current score
        private int level; //current level
        private int counter; //remaining boxes
        private int plusminuspoints; //+ - points
        private int viewWidth; // stores the width of this View
        private int viewHeight; // stores the height of this view
        private boolean gameOver; // check to see if the game is over
        private boolean gamePaused; // whether the game has ended
        private boolean dialogDisplayed; //check to see if the game over dialog has been prompted
        private int highScore; // the game's all time high score
        private static final int player_lives = 3; // start with 3 lives
        private static final int maximum_lives = 7; // maximum # of total lives
        private int new_level_check = 10; // boxes to reach new level (INITIALLY SET TO 10)
        private Boolean checkPause = true; //for the level transition

        //BOXES + THEIR ANIMATIONS
        private long boxLifespan; // lifespan of a box
        private final Queue<ImageView> boxes = new ConcurrentLinkedQueue<>(); //store boxes in an imageview queue
        private final Queue<Animator> animators = new ConcurrentLinkedQueue<>(); //store their animations
        private static final int initial_anim_duration = 6000;
        private static final Random random = new Random(); // for random coords
        private static final int the_width = 110; // initial box size
        private static final float x_scale = 0.29f; // end animation x scale
        private static final float y_scale = 0.29f; // end animation y scale
        private static final int init_boxes = 5; // initial # of boxes
        private static final int box_delay = 500; // delay in milliseconds
        private Handler boxHandler; // adds new boxes to the game
    
        //LAYOUTS AND TEXTVIEW INFORMATION
        private TextView currentScoreTextView; // displays current score
        private TextView levelTextView; // displays current level
        private TextView counterTextView; //to countdown what's left
        private TextView plusMinusView; //plus/minus for point calculation
        private TextView plusMinusScoreView; //plus/minus score for point calculation
        private LinearLayout livesLinearLayout; // displays lives remaining
        private RelativeLayout relativeLayout; // displays boxes
        private Resources resources; // used to load resources
        private LayoutInflater layoutInflater; // used to inflate GUIs
    
        //FONTPATH FOR GAME'S FONT
        public String fontPath = "fonts/Broken Robot.ttf"; //to set font for score, level, and counter

        //SOUNDS
        private static final int tap_sound_ID1 = 1;
        private static final int tap_sound_ID2 = 2;
        private static final int tap_sound_ID3 = 3;
        private static final int tap_sound_ID4 = 4;
        private static final int tap_sound_ID5 = 5;
        private static final int tap_sound_ID6 = 6;
        private static final int tap_sound_ID7 = 7;
        private static final int tap_sound_ID8 = 8;
        private static final int miss_sound_ID = 9;
        private static final int lose_life_sound_ID = 10;
        //////SOUND ATTRIBUTES
        private static final int sound_prior = 1;
        private static final int sound_qual = 100;
        private static final int max_strs = 4;
        private SoundPool soundPool; // plays sound effects
        private int volume; // sound effect volume
        MediaPlayer music = new MediaPlayer();

        public MainView(Context context, RelativeLayout parentLayout) {
            super(context);
            resources = context.getResources();
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            relativeLayout = parentLayout;
            prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            highScore = prefs.getInt(the_high_score, 0);
            ///////////////////////set all of the layouts (score, lives, etc.)
            livesLinearLayout = (LinearLayout) relativeLayout.findViewById(R.id.the_lives);
            currentScoreTextView = (TextView) relativeLayout.findViewById(R.id.the_score); //score view
            counterTextView = (TextView) relativeLayout.findViewById(R.id.the_counter); //counter view
            levelTextView = (TextView) relativeLayout.findViewById(R.id.the_level); //level view
            plusMinusView = (TextView) relativeLayout.findViewById(R.id.plusminus); //plus/minus view
            plusMinusScoreView = (TextView) relativeLayout.findViewById(R.id.plusminusscore);
            ////////////////////////////////////set the font for all of the layouts
            AssetManager assetManager = getContext().getAssets();
            Typeface type = Typeface.createFromAsset(assetManager, fontPath); //used to change the font
            currentScoreTextView.setTypeface(type);
            counterTextView.setTypeface(type);
            levelTextView.setTypeface(type);
            plusMinusView.setTypeface(type);
            plusMinusScoreView.setTypeface(type);
            /////////////////////////////start the game
            boxHandler = new Handler(); // add boxes to start up of game
            playRandomTrack();
        }

        //initialize the background music
        public void playRandomTrack() {
            Random random = new Random();
            int num = random.nextInt(4) + 1; //random number between 1 and 4
            switch(num) {
                case 1:
                    music = MediaPlayer.create(getContext(), R.raw.main1);
                    break;
                case 2:
                    music = MediaPlayer.create(getContext(), R.raw.main2);
                    break;
                case 3:
                    music = MediaPlayer.create(getContext(), R.raw.main3);
                    break;
                case 4:
                    music = MediaPlayer.create(getContext(), R.raw.mainr4);
                    break;
            }

            music.start();
            music.setLooping(true);
        }

        @Override
        protected void onSizeChanged(int width, int height, int oldw, int oldh) {
            viewWidth = width; // save the new width
            viewHeight = height; // save the new height
        }

        public void pause() {
            gamePaused = true;
            soundPool.release(); // stop all sounds
            soundPool = null;
            stopAnimations(); // stop all animations
        }

        private void stopAnimations() {
            for (Animator animator : animators)
                animator.cancel(); //cancel the box animations
            
            for (ImageView view : boxes)
                relativeLayout.removeView(view); //hide the view

            boxHandler.removeCallbacks(addBoxRunnable);
            animators.clear();
            boxes.clear();
        }

        public void resume(Context context) {
            gamePaused = false;
            startSoundEffects(context); // initialize app's SoundPool

            if (!dialogDisplayed) {
                resetGame(); // start the game
            }
        }

        public void resetGame() {
            boxes.clear(); //empty the List of boxes
            animators.clear(); //empty the List of Animators
            livesLinearLayout.removeAllViews(); //clear old lives from screen

            boxLifespan = initial_anim_duration;
            boxesTouched = 0; //reset the number of boxes touched
            score = 0; //reset the score
            level = 1; //reset the level
            counter = new_level_check; //reset the counter
            gameOver = false; // the game is not over
            displayScores(0); // display scores and level

            final ViewGroup nullParent = null;
            for (int i = 0; i < player_lives; i++) { //re-add the lives
                livesLinearLayout.addView(layoutInflater.inflate(R.layout.lifebar, nullParent));
            }

            for (int i = 1; i <= init_boxes; ++i) {
                boxHandler.postDelayed(addBoxRunnable, i * box_delay);
            }
        }

        private void startSoundEffects(Context context) {
            SparseIntArray soundSparse;
            soundPool = new SoundPool(max_strs, AudioManager.STREAM_MUSIC, sound_qual);

            // set sound effect volume
            AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            volume = manager.getStreamVolume(AudioManager.STREAM_MUSIC);

            soundSparse = new SparseIntArray();

            //////ALL HIT SOUNDS
            soundSparse.put(tap_sound_ID1, soundPool.load(context, R.raw.a, sound_prior));
            soundSparse.put(tap_sound_ID2, soundPool.load(context, R.raw.b, sound_prior));
            soundSparse.put(tap_sound_ID3, soundPool.load(context, R.raw.c, sound_prior));
            soundSparse.put(tap_sound_ID4, soundPool.load(context, R.raw.d, sound_prior));
            soundSparse.put(tap_sound_ID5, soundPool.load(context, R.raw.e, sound_prior));
            soundSparse.put(tap_sound_ID6, soundPool.load(context, R.raw.f, sound_prior));
            soundSparse.put(tap_sound_ID7, soundPool.load(context, R.raw.g, sound_prior));
            soundSparse.put(tap_sound_ID8, soundPool.load(context, R.raw.h, sound_prior));

            ///////OTHER SOUNDS
            soundSparse.put(miss_sound_ID, soundPool.load(context, R.raw.miss, sound_prior));
            soundSparse.put(lose_life_sound_ID, soundPool.load(context, R.raw.disappear, sound_prior));
        }

        private void displayScores(int check) {
            currentScoreTextView.setText(resources.getString(R.string.score) + " " + score);
            levelTextView.setText(resources.getString(R.string.level) + " " + level);
            counterTextView.setText("Boxes Remaining: " + " " + counter);
            switch (check) {
                case 0: plusMinusView.setText(" ");
                    plusMinusScoreView.setText(String.valueOf(plusminuspoints));
                    break;
                case 1: plusMinusView.setText("-"); //set to minus
                    plusMinusScoreView.setText(String.valueOf(plusminuspoints));
                    plusMinusView.setTextColor(Color.parseColor("#ff0000"));
                    plusMinusScoreView.setTextColor(Color.parseColor("#ff0000"));
                    break;
                case 2: plusMinusView.setText("+"); //set to plus
                    plusMinusScoreView.setText(String.valueOf(plusminuspoints));
                    plusMinusView.setTextColor(Color.parseColor("#008000"));
                    plusMinusScoreView.setTextColor(Color.parseColor("#008000"));
                    break;
            }
        }

        // Runnable used to add new boxes to the game at the start
        private Runnable addBoxRunnable = new Runnable() {
            public void run() {
                if (boxesTouched == 0) {
                    counter = new_level_check; //beginning of each level, set the counter to what's needed to be touched
                    counterTextView.setText("Boxes Remaining: " + " " + counter);
                }
                addNewBox(); // add a new box to the game
            }
        };

        // adds a new box at a random location and starts its animation
        public void addNewBox() {
            // choose two random coordinates for the starting and ending points
            int x = random.nextInt(viewWidth - the_width);
            int y = random.nextInt(viewHeight - the_width);
            int x2 = random.nextInt(viewWidth - the_width);
            int y2 = random.nextInt(viewHeight - the_width);

            final ViewGroup nullParent = null;
            // create new box
            final ImageView box = (ImageView) layoutInflater.inflate(R.layout.notclicked, nullParent);
            boxes.add(box); // add the new box to our list of boxes
            box.setLayoutParams(new RelativeLayout.LayoutParams(the_width, the_width));
            box.setImageResource(random.nextInt(2) == 0 ? R.drawable.black : R.drawable.white);
            box.setX(x); // set box's starting x location
            box.setY(y); // set box's starting y location
            box.setOnClickListener( // listens for box being clicked
                    new OnClickListener() {
                        public void onClick(View v) {
                            touchedBox(box); // handle touched box
                        }
                    }
            );
            relativeLayout.addView(box); // add box to the screen

            // configure and start box's animation
            box.animate().x(x2).y(y2).scaleX(x_scale).scaleY(y_scale).setDuration(boxLifespan).setListener(
                    new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            animators.add(animation); //start animation, add it
                        }

                        public void onAnimationEnd(Animator animation) {
                            animators.remove(animation); // animation done, remove

                            if (!gamePaused && boxes.contains(box)) {
                                missedBox(box); // lose a life
                            }
                        }
                    }
            );
        }

        //when the user misses ...
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (soundPool != null) {
                soundPool.play(miss_sound_ID, volume, volume, sound_prior, 0, 1f);
            }
            score -= 15 * level; // remove some points
            plusminuspoints = 15 * level;
            score = Math.max(score, 0); // do not let the score go below zero
            displayScores(1); // update scores/level on screen
            return true;
        }

        // when the users clicks on a box ...
        private void touchedBox(ImageView box) {
            relativeLayout.removeView(box); // remove touched box from screen
            boxes.remove(box); // remove old box from list
            --counter; //decrement the counter

            ++boxesTouched; // increment the number of boxes touched
            score += 10 * level; // increment the score
            plusminuspoints = 10 * level;

            // play the hit sounds
            if (soundPool != null) {
                int random = new Random().nextInt(8) + 1; //random number between 1 and 8
                soundPool.play(random, volume, volume, sound_prior, 0, 1f);
            }

            if (boxesTouched % new_level_check == 0) {
                ++new_level_check; //increment the amount of boxes needed to be touched to proceed to the next level
                checkPause = false;
                ++level; // increment the level
                boxLifespan *= 0.95; // game is 5% faster

                final ViewGroup nullParent = null;
                if (livesLinearLayout.getChildCount() < maximum_lives) {
                    ImageView life = (ImageView) layoutInflater.inflate(R.layout.lifebar, nullParent);
                    livesLinearLayout.addView(life); // add life to screen (should only happen if level is complete)
                }
                pause(); //pause the game for the dialog
                Builder levelDialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom);
                AssetManager assetManager = getContext().getAssets();
                ///////////// FOR TITLE
                TextView title = new TextView(getContext());
                Typeface type = Typeface.createFromAsset(assetManager, fontPath); //used to change the font
                title.setText(R.string.well_done);
                title.setTextSize(30);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(type);
                levelDialog.setCustomTitle(title);
                ///////////// FOR BODY
                TextView body = new TextView(getContext());
                body.setText("GET READY FOR LEVEL " + level + "\nTARGET: " +
                        new_level_check + " BOXES");
                body.setTextSize(20);
                body.setGravity(Gravity.CENTER);
                body.setTypeface(type);
                levelDialog.setView(body);
                /////////////////
                levelDialog.setPositiveButton(R.string.next_level, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                checkPause = true;
                                resume(getContext());
                                boxesTouched = 0;
                                counter = new_level_check;

                                for (int i = 1; i <= init_boxes; ++i) {
                                    boxHandler.postDelayed(addBoxRunnable, i * box_delay);
                                }
                            }
                        }
                );

                dialogDisplayed = true;
                levelDialog.setCancelable(false);
                levelDialog.show();
            }

            if (!gameOver && checkPause) {
                addNewBox(); // add another untouched box
            }
            displayScores(2); // update score/level on the screen
        }

        public void missedBox(ImageView box) {
            boxes.remove(box); //the box disappears
            relativeLayout.removeView(box); // let the view know the box disappeared

            if (gameOver) // if the game is already over, exit
                return;

            // play the disappear sound effect
            if (soundPool != null)
                soundPool.play(lose_life_sound_ID, volume, volume, sound_prior, 0, 1f);

            // if the game has been lost
            if (livesLinearLayout.getChildCount() == 0) {
                gameOver = true; // the game is over
                stopAnimations();

                if (score > highScore) {
                    secondDialog();
                }
                else {
                    firstDialog();
                }
            }
            else { //remove a life (a box disappeared)
                livesLinearLayout.removeViewAt(livesLinearLayout.getChildCount() - 1); //decrease the # of lives
                addNewBox(); // add another box to game
            }
        }

        //ONLY DISPLAYS WHEN SOMEONE ATTAINS A NEW HIGH SCORE
        public void secondDialog() {
            Builder alertDialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom);
            AssetManager assetManager = getContext().getAssets();
            ///////////// FOR TITLE
            TextView title = new TextView(getContext());
            Typeface type = Typeface.createFromAsset(assetManager, fontPath); //used to change the font
            title.setText(R.string.high_score);
            title.setTextSize(30);
            title.setGravity(Gravity.CENTER);
            title.setTypeface(type);
            alertDialog.setCustomTitle(title);
            final EditText input = new EditText(getContext());
            input.setHint("ENTER YOUR NAME");
            alertDialog.setView(input);
            /////////////////
            alertDialog.setPositiveButton(R.string.con_tinue, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt(the_high_score, score);
                            editor.putString(the_name, input.getText().toString());
                            editor.apply(); // store the new high score
                            firstDialog();
                        }
                    }
            );
            dialogDisplayed = true;
            alertDialog.setCancelable(false);
            alertDialog.show(); // display the reset game dialog
        }

        //ALWAYS DISPLAYS SCORE
        public void firstDialog() {
            Builder alertDialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom);
            AssetManager assetManager = getContext().getAssets();
            ///////////// FOR TITLE
            TextView title = new TextView(getContext());
            Typeface type = Typeface.createFromAsset(assetManager, fontPath); //used to change the font
            title.setText(R.string.game_over);
            title.setTextSize(30);
            title.setGravity(Gravity.CENTER);
            title.setTypeface(type);
            alertDialog.setCustomTitle(title);
            ///////////// FOR BODY
            TextView body = new TextView(getContext());
            body.setText("Score: " + score);
            body.setTextSize(20);
            body.setGravity(Gravity.CENTER);
            body.setTypeface(type);
            alertDialog.setView(body);
            /////////////////
            alertDialog.setPositiveButton(R.string.reset_game, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            music.stop();
                            displayScores(0);
                            dialogDisplayed = false;
                            playRandomTrack();
                            resetGame(); // start a new game
                        }
                    }
            );
            alertDialog.setNegativeButton(R.string.go_home, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    music.stop();
                    Intent intent = new Intent(getContext(), title.class);
                    getContext().startActivity(intent);
                }
            });
            dialogDisplayed = true;
            alertDialog.setCancelable(false);
            alertDialog.show(); // display the reset game dialog
        }
}
