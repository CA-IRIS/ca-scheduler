/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2008  Minnesota Department of Transportation
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package us.mn.state.dot.sched;

import java.util.Calendar;
import java.util.Date;

/**
 * Job for the scheduler to perform.
 *
 * @author Douglas Lau
 */
abstract public class Job implements Comparable<Job> {

	/** Job identifier */
	static protected long job_id = 0;

	/** Unique job identifier */
	protected final long id = job_id++;

	/** Next time this job must be performed */
	protected final Date nextTime;

	/** Time interval to perform this job */
	protected final long interval;

	/** Time offset from whole interval boundary */
	protected final long offset;

	/**
	 * Create a new scheduler job
	 * @param iField java.util.Calendar field for time interval
	 * @param i Time interval to schedule the job
	 * @param oField java.util.Calendar field for interval offset
	 * @param o Time interval offset to schedule the job
	 */
	public Job(int iField, int i, int oField, int o) {
		assert i >= 0;
		assert o >= 0;
		Date time = new Date(0);
		Calendar c = Calendar.getInstance();
		c.setTime(time);
		c.add(iField, i);
		interval = c.getTime().getTime();
		c.setTime(time);
		c.add(oField, o);
		offset = c.getTime().getTime();
		assert offset < interval;
		nextTime = new Date();
		computeNextTime();
	}

	/**
	 * Create a new scheduler job
	 * @param iField java.util.Calendar field for time interval
	 * @param i Time interval to schedule the job
	 */
	public Job(int iField, int i) {
		this(iField, i, Calendar.SECOND, 0);
	}

	/** Create a one-shot scheduler job */
	public Job(int milliseconds) {
		interval = 0;
		offset = 0;
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MILLISECOND, milliseconds);
		nextTime = c.getTime();
	}

	/** Create a one-shot job to schedule immediately */
	public Job() {
		this(0);
	}

	/** Compute the next time this job will be scheduled */
	protected void computeNextTime() {
		Calendar c = Calendar.getInstance();
		long off = offset - c.get(Calendar.ZONE_OFFSET) -
			c.get(Calendar.DST_OFFSET);
		long now = c.getTime().getTime();
		long last = (now - off) / interval * interval;
		if(last > nextTime.getTime() + off) {
			System.err.println(nextTime +
			" MISSED EVENT, interval: " + interval);
		}
		nextTime.setTime(last + interval + off);
	}

	/** Perform the task for this job */
	public void performTask() throws Exception {
		if(interval > 0)
			computeNextTime();
		try {
			perform();
		}
		finally {
			complete();
		}
	}

	/** Actual "job" to be performed */
	abstract public void perform() throws Exception;

	/** Do this upon completion of the job */
	public void complete() {}

	/** Compare this job with another one */
	public int compareTo(Job other) {
		long c = nextTime.compareTo(other.nextTime);
		if(c == 0)
			c = interval - other.interval;
		if(c == 0)
			c = offset - other.offset;
		if(c == 0)
			c = id - other.id;
		if(c < 0)
			return -1;
		if(c > 0)
			return 1;
		return 0;
	}
}
