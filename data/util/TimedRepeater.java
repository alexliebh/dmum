package be.alexandreliebh.picacademy.data.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimedRepeater {

	private ScheduledExecutorService scheduler;
	private long initDelay;
	private long period;

	public TimedRepeater(long initDelay, long period) {
		scheduler = Executors.newSingleThreadScheduledExecutor();
		this.initDelay = initDelay;
		this.period = period;
	}

	public void start(Runnable r) {
		scheduler.scheduleAtFixedRate(r, initDelay, period, TimeUnit.SECONDS);

	}

	public void stop() {
		scheduler.shutdown();
	}

	public int getPeriod() {
		return (int) period;
	}
	
}
