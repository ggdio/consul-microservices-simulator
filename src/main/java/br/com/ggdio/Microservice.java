package br.com.ggdio;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.orbitz.consul.AgentClient;
import com.orbitz.consul.NotRegisteredException;

public class Microservice implements Runnable {
	
	private static final AtomicInteger generator = new AtomicInteger(1);
	
	private AtomicBoolean run = new AtomicBoolean(true);
	
	private final String id;
	private final String name;
	private final int port;
	private final long ttl;
	private final String[] tags;
	private final AtomicLong sleep;
	
	private final AgentClient consul = App.consul.agentClient();
	
	public Microservice(String name, int port, long ttl, String...tags) {
		this.id = name + "-" + generator.getAndIncrement();
		this.name = name;
		this.port = port;
		this.ttl = ttl;
		this.tags = tags;
		this.sleep = new AtomicLong((ttl -1l) * 1000l);
	}
	
	public String getId() {
		return id;
	}

	public void run() {
		System.out.println("Service[id=" + id + "] starting...");
		consul.register(port, ttl, name, id, tags);
		run.set(true);
		while(run.get()) {
			try {
				consul.pass(id);
				
			} catch (NotRegisteredException e) {
				e.printStackTrace();
				
			} finally {
				try {
					Thread.sleep(sleep.get());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		System.out.println("Service[id=" + id + "] stopping...");
		
		consul.deregister(id);
		
	}
	
	public void resetHCInterval() {
		setHCInterval((ttl -1l) * 1000l);
	}
	
	public void setHCInterval(long interval) {
		this.sleep.set(interval);
	}
	
	public long getTTL() {
		return ttl;
	}
	
	public boolean isRunning() {
		return run.get();
	}
	
	public void stop() {
		run.set(false);
	}

}