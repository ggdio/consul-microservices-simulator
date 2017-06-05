package br.com.ggdio.microservice;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.orbitz.consul.AgentClient;
import com.orbitz.consul.NotRegisteredException;

import br.com.ggdio.App;
import spark.RouteImpl;
import spark.Service;

/**
 * Simple microservice simulator class with an keepAlive and a Front Controller Servlet thread.
 *   
 * @author Guilherme Dio
 *
 */
public class Microservice implements Runnable {
	
	private static final AtomicInteger generator = new AtomicInteger(1);
	
	private AtomicBoolean run = new AtomicBoolean(true);
	
	private final String id;
	private final String name;
	private final int port;
	private final long ttl = 5L;
	private final ServiceHandler serviceHandler;
	private final String[] tags;
	private final AtomicLong sleep;
	
	private final Service service;
	
	private final AgentClient consul = App.consul.agentClient();
	
	public Microservice(String name, int port, String...tags) {
		this(name, port, null, tags);
	}
	
	public Microservice(String name, int port, ServiceHandler serviceHandler, String...tags) {
		this.id = name + "-" + generator.getAndIncrement();
		this.name = name;
		this.port = port;
		this.serviceHandler = serviceHandler;
		this.tags = tags;
		this.sleep = new AtomicLong((ttl -1l) * 1000l);
		
		this.service = Service.ignite();
	}
	
	public String getId() {
		return id;
	}

	public void run() {
		setupPort(port);
		
		setupService();
		
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
	
	private void setupService() {
		if(serviceHandler != null) {
			for (ServiceRoute serviceRoute : serviceHandler.getHandlers()) {
				service.addRoute(serviceRoute.getMethod(), RouteImpl.create(serviceRoute.getPath(), serviceRoute.getRoute()));
			}
		}
		
	}

	private void setupPort(int port) {
		service.port(port);
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
		
		service.stop();
	}

}