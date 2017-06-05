package br.com.ggdio.microservice;

import spark.Route;
import spark.route.HttpMethod;

/**
 * Service route dto
 * @author Guilherme Dio
 *
 */
class ServiceRoute {

	private final HttpMethod method;
	private final String path;
	private final Route route;
	
	ServiceRoute(HttpMethod method, String path, Route route) {
		super();
		this.method = method;
		this.path = path;
		this.route = route;
	}
	
	HttpMethod getMethod() {
		return method;
	}
	
	String getPath() {
		return path;
	}
	
	Route getRoute() {
		return route;
	}
	
}