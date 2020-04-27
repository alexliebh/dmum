package be.alexandreliebh.picacademy.data.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimedRepeater {

	private final long initDelay;
	private final long period;

	private ScheduledExecutorService scheduler;

	private boolean isMs;
	private Runnable runnable;

	public TimedRepeater(long initDelay, long period) {
		init();
		this.initDelay = initDelay;
		this.period = period;
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
		scheduler.scheduleAtFixedRate(r, initDelay, period, TimeUnit.SECONDS);
	}

	public void startMs(Runnable r) {
		this.isMs = true;
		this.runnable = r;
		scheduler.scheduleAtFixedRate(r, initDelay, period, TimeUnit.MILLISECONDS);

	}

	public void stop() {
		scheduler.shutdown();
	}

	public int getPeriod() {
		return (int) period;
	}

	private void init() {
		scheduler = Executors.newSingleThreadScheduledExecutor();
	}

}
