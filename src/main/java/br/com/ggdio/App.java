package br.com.ggdio;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.orbitz.consul.Consul;

import br.com.ggdio.microservice.Microservice;
import br.com.ggdio.microservice.ServiceHandler;

/**
 * Just a playground
 *
 */
public class App {
	
	public static Consul consul = Consul.builder().build();
	
	private static List<Microservice> microservices;

	private static ExecutorService executor;
	
	public static void main(String[] args) throws Exception {
		build();
		
		start();
		
		Thread.sleep(2000);
		deployConsole();
		
		System.exit(0);
	}

	private static void build() {
		microservices = Arrays.asList(
				
				// Shopping cart cluster
				new ShoppingCartMicroservice(6060),
				new ShoppingCartMicroservice(6061),
				new ShoppingCartMicroservice(6062),
				new ShoppingCartMicroservice(6063),
				new ShoppingCartMicroservice(6064),
				new ShoppingCartMicroservice(6065),
				new ShoppingCartMicroservice(6066),
				new ShoppingCartMicroservice(6067),
				new ShoppingCartMicroservice(6068),
				new ShoppingCartMicroservice(6069),
				
				new PaymentMicroservice(7071)
				
//				// INGESTION
//				buildMicroservice("data-ingestion", 6061, "DEV"),
//				buildMicroservice("data-ingestion", 6062, "HML"),
//				buildMicroservice("data-ingestion", 6063, "PRD"),
//				
//				// STREAMING
//				buildMicroservice("data-streaming", 7061, "DEV"),
//				buildMicroservice("data-streaming", 7061, "HML"),
//				buildMicroservice("data-streaming", 7061, "PRD"),
//				
//				// INTEGRATION
//				buildMicroservice("data-integration", 7061, "DEV"),
//				buildMicroservice("data-integration", 7061, "HML"),
//				buildMicroservice("data-integration", 7061, "PRD")
		);
	}
	
	private static void start() {
		executor = Executors.newFixedThreadPool(microservices.size());
		
		for (Microservice microservice : microservices) {
			executor.submit(microservice);
		}
		
	}
	
	private static void deployConsole() throws InterruptedException {
		printUsage();
		
		Scanner scanner = new Scanner(System.in);
		
		boolean run = true;
		while(run) {
			try {
				String directive = scanner.nextLine();
				
				if(directive != null && !directive.isEmpty()) {
					if(directive.equals("exit")) {
						try {
							for (Microservice microservice : microservices) {
								System.out.println("Cleaning running instances...");
								microservice.stop();
								System.out.println("Stopping...");
							}
						} finally {
							run = false;
							
						}
					} else if(directive.equals("list")){
						for (Microservice microservice : microservices) {
							System.out.println("Service[id="+microservice.getId()+", status=" + (microservice.isRunning() ? "Running" : "Stopped") + "]");
							System.out.println();
						}
						
					} else if(directive.startsWith("stop")) {
						String id = directive.split(" ")[1];
						for (Microservice microservice : microservices) {
							if(id.equals(microservice.getId())) {
								if(microservice.isRunning()) {
									System.out.println("Stopping microservice["+directive+"]...");
									microservice.stop();
									System.out.println("Done.");
								} else {
									System.out.println("Microservice is already stopped !");
								}
								break;
							}
						}
						
					} else if(directive.startsWith("start")) {
						String id = directive.split(" ")[1];
						for (Microservice microservice : microservices) {
							if(id.equals(microservice.getId())) {
								if(!microservice.isRunning()) {
									System.out.println("Starting microservice["+directive+"]...");
									microservice.stop();
									System.out.println("Done.");
								} else {
									System.out.println("Microservice is already running !");
								}
								break;
							}
						}
						
					} else if(directive.startsWith("unhealthy")) {
						String id = directive.split(" ")[1];
						for (Microservice microservice : microservices) {
							if(id.equals(microservice.getId())) {
								microservice.setHCInterval((microservice.getTTL() + 10) * 1000);
								break;
							}
						}
						
						
					} else if(directive.startsWith("healthy")) {
						String id = directive.split(" ")[1];
						for (Microservice microservice : microservices) {
							if(id.equals(microservice.getId())) {
								microservice.resetHCInterval();
								break;
							}
						}
						
					}
					else {
						System.out.println("Invalid argument '"+directive+"'");
						printUsage();
					}
				}
			} catch(Exception e) {
				System.err.println("Something went wrong: " + e.getMessage());
			}
		}
		Thread.sleep(2000);
		System.out.println("Stopped.");
		
	}

	private static void printUsage() {
		System.out.println();
		System.out.println("=====");
		System.out.println("Usage:");
		System.out.println("- Stop Microservice: 'stop [serviceId]'");
		System.out.println("- Start Microservice: 'start [serviceId]'");
		System.out.println("- Make Microservice Unhealthy: 'unhealthy [serviceId]'");
		System.out.println("- Make Microservice Healthy: 'healthy [serviceId]'");
		System.out.println("- List Microservices: 'list'");
		System.out.println("- Quit Program: 'exit'");
		System.out.println("=====");
		System.out.println();
	}

	private static Microservice buildMicroservice(String name, int port, String environment, ServiceHandler serviceHandler) {
		return new Microservice(name, port, serviceHandler, environment, name);
	}

}
