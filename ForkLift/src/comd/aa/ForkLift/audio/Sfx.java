package comd.aa.ForkLift.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class Sfx {
	public static Sound firing;
	public static void load()
	{
		firing = Gdx.audio.newSound(Gdx.files.internal("audio/motor.ogg"));
	}

}
