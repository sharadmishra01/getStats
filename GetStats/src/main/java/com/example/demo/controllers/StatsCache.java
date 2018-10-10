package com.example.demo.controllers;

import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

/**
 * An automatically refresh able cache it updated itself after MAX AGE to remove
 * stale entries redefines values and updates the cache, the same cached results
 * then can be used by GET API. POST API updates the results accordingly
 * 
 * Uses concurrent hash map to return the data on best effort basis
 * 
 * refreshes the cache and finally uses collection API to find min and max as
 * this would be caching the results for min, max, average and sum which
 * essentially means that get API API will only take O(1) time as it will use
 * calculated results. this method takes O(n)
 * 
 * @author sharad.mishra
 *
 */

@Component
public class StatsCache {

	private long maxAge;

	private ConcurrentMap<Long, Double> store;

	private double min; // just keeping min and max may be replaced by
						// collection api after
						// verifying complexity

	private double max;

	private double avg;

	private double sum;

	private static int INITIAL_DELAY = 60; // s
	private static int MAX_AGE = 60 * 1000; // ms
	private static int CACHE_REFRESH_PERIOD = 60; // for now 10

	public StatsCache() {

		this.maxAge = MAX_AGE;
		this.store = new ConcurrentHashMap<>();

		try {

			Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
					new CacheRefershThread(), INITIAL_DELAY,
					CACHE_REFRESH_PERIOD, TimeUnit.SECONDS);
		} catch (Exception ex) {
			System.out.println(" Got exception while refresh");
		}
	}

	/**
	 * puts the value in map after validation if transaction is 60 seconds old
	 * dont allow or even it is of future dont allow as that is not feasible
	 * 
	 * @param key
	 * @param value
	 */
	public void put(Long key, Double value) {

		if (System.currentTimeMillis() - key > (MAX_AGE * 100)
				|| (key - System.currentTimeMillis() > 0)) {
			// do nothing
			return;
		}
		System.out.println("Adding " + key + "with val " + value);


		if (store.get(key) == null) { // dont override prev val for same time stamp
			sum = sum + value;

			avg = (avg * store.size() + value) / (store.size() + 1);

			store.put(key, value);

			if (value < min || min == 0.0) {
				System.out.println("replacing min");
				min = value;
			}

			if (value > max) {
				System.out.println("replacing max");

				max = value;
			}
		}

	}

	/**
	 * refreshes the cache and finally uses collection API to find min and max
	 * as this would be caching the results for min, max, average and sum which
	 * essentially menat that get API API will only take O(1) time as it will
	 * use calculated result. this method takes O(n)
	 */
	public void refreshCache() {

		System.out.println("Going to refresh Cache");

		Iterator<Long> iter = store.keySet().iterator();

		while (iter.hasNext()) {
			Long key = iter.next();
			if (System.currentTimeMillis() - key > maxAge) {
				System.out.println(" Removing the entry " + key);
				store.remove(key);

			}
		}

		System.out.println(" Calculating min and max again");

		if (!store.isEmpty()) {
			min = Collections.min(store.values());
			max = Collections.max(store.values());

		} else {
			min = max = 0.0;
		}
		
		calculateSumAndAverage();

	}

	/**
	 * method to calculate the sum and average again
	 */
	public void calculateSumAndAverage() {

		if (store.size() == 0) {
			sum = avg = 0.0;
			return;
		}

		double sum = 0.0;
		for (double f : store.values()) {
			sum += f;
		}

		this.sum = sum;
		avg = sum / store.size();
	}

	public int getSize() {
		return store.size();
	}

	/**
	 * This is main thread which will run automatically to update the cache, it
	 * will update the cache of transaction at regular interval of MAX AGE which
	 * is 60 seconds
	 * 
	 * @author sharad.mishra
	 *
	 */
	class CacheRefershThread implements Runnable {

		@Override
		public void run() {
			refreshCache();
		}

	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public void setAvg(double avg) {
		this.avg = avg;
	}

	public void setSum(double sum) {
		this.sum = sum;
	}

	public double getAvg() {
		return avg;
	}

	public double getSum() {
		return sum;
	}

	// JUst a test method
	private static long calRandomLong() {
		// ����return ((long) (Math.random() * (100000L - 1L)));
		return (long) (Math.random() * (30000L - 1L) + System
				.currentTimeMillis());

	}

	public static void main(String args[]) {

		System.out.println(" Current time " + System.currentTimeMillis());

		// StatsCache cache = new StatsCache();
		//
		// while (true) {
		// cache.put(calRandomLong(), Math.random() + 20.0);
		// cache.put(calRandomLong(), Math.random() + 20.0);
		// cache.put(calRandomLong(), Math.random() + 20.0);
		// cache.put(calRandomLong(), Math.random() + 20.0);
		// cache.put(calRandomLong(), Math.random() + 20.0);
		//
		// System.out.println("Current Min " + cache.min + " Actual min "
		// + Collections.min(cache.store.values()));
		// System.out.println("Current Max " + cache.max + " Actual max "
		// + Collections.max(cache.store.values()));
		// System.out.println("Current Size " + cache.getSize());
		//
		// try {
		// Thread.sleep(5000); // Just to test
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// }
	}

}
