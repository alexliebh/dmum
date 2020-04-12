package be.alexandreliebh.picacademy.server.game;

import be.alexandreliebh.picacademy.data.PicConstants;
import be.alexandreliebh.picacademy.data.util.TimedRepeater;

public class PicGameScheduler {

	private TimedRepeater repeater;
	private volatile byte timer;
	private PicTimeListener listener;
	
	public PicGameScheduler() {
		init();
	}

	public synchronized void start() {
		this.repeater.start(new Runnable() {

			public void run() {
				listener.onTimeTick(timer);
				timer--;
				if (timer <= 0) {
					listener.onRoundEnd();
					stop();
					return;
				}
			}
		});
	}
	
	public void init() {
		this.repeater = new TimedRepeater(0, 1);
		this.timer = (byte) PicConstants.ROUND_TIME_SECONDS;
	}
	
	public void restart() {
		init();
		start();
	}
	
	public void stop() {
		this.repeater.stop();
	}
	
	public void addTimeListener(PicTimeListener listener) {
		this.listener = listener;
	}
	
	public byte getTimer() {
		return timer;
	}
	
	public interface PicTimeListener{
		public void onRoundEnd();
		public void onTimeTick(byte timer);
	}

}
