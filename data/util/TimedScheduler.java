package be.alexandreliebh.picacademy.data.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimedScheduler {

	private ScheduledExecutorService scheduler;
	private long delay;

	private boolean isMs;
	private Runnable runnable;

	public TimedScheduler(long delay) {
		this.init();
		this.delay = delay;
		this.isMs = false;
	}

	public void restart() {
		init();
		if (isMs) {
			startMs(runnable);
		} else {
			start(runnable);
		}
	}

	public void start(Runnable r) {
		this.isMs = false;
		this.runnable = r;
		scheduler.schedule(r, delay, TimeUnit.SECONDS);
	}

	public void startMs(Runnable r) {
		this.isMs = true;
		this.runnable = r;
		scheduler.schedule(r, delay, TimeUnit.MILLISECONDS);

	}

	public void stop() {
		scheduler.shutdown();
	}

	public long getDelay() {
		return delay;
	}

	private void init() {
		scheduler = Executors.newSingleThreadScheduledExecutor();
	}
}
