package masterimis.proggraphique.opengles;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        Map<Integer, MediaPlayer> sounds = new HashMap<>();
        sounds.put(Plateau.SOUND_SWIPE, MediaPlayer.create(getApplicationContext(), R.raw.swipe));
        sounds.put(Plateau.SOUND_ERROR, MediaPlayer.create(getApplicationContext(), R.raw.error));
        sounds.put(Plateau.SOUND_WIN, MediaPlayer.create(getApplicationContext(), R.raw.victory));

        setContentView(new GLView(this, sounds));
    }
}
