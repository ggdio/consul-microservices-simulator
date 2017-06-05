package br.com.ggdio.microservice;

import java.util.ArrayList;
import java.util.List;

import spark.Route;
import spark.route.HttpMethod;

/**
 * Service routes handler for microservices internal logic
 * 
 * @author Guilherme Dio
 *
 */
public class ServiceHandler {
	
	private final List<ServiceRoute> serviceRoutes;
	
	private ServiceHandler(List<ServiceRoute> handlers) {
		this.serviceRoutes = handlers;
	}
	
	List<ServiceRoute> getHandlers() {
		return serviceRoutes;
	}
	
	public static ServiceHandlerBuilder builder() {
		return new ServiceHandlerBuilder();
	}
	
	public static class ServiceHandlerBuilder {
		
		private final List<ServiceRoute> serviceRoutes = new ArrayList<>();
		
		public ServiceHandlerBuilder withMethod(HttpMethod method, String path, Route route) {
			serviceRoutes.add(new ServiceRoute(method, path, route));
			return this;
		}
		
		public ServiceHandlerBuilder withGET(String path, Route route) {
			return withMethod(HttpMethod.get, path, route);
		}
		
		public ServiceHandlerBuilder withPOST(String path, Route route) {
			return withMethod(HttpMethod.post, path, route);
		}
		
		public ServiceHandlerBuilder withPUT(String path, Route route) {
			return withMethod(HttpMethod.put, path, route);
		}
		
		public ServiceHandlerBuilder withDELETE(String path, Route route) {
			return withMethod(HttpMethod.delete, path, route);
		}
		
		public ServiceHandler build() {
			return new ServiceHandler(serviceRoutes);
		}
		
	}
	
	
}