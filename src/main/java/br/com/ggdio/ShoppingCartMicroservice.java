package br.com.ggdio;

import java.util.ArrayList;
import java.util.List;

import br.com.ggdio.microservice.Microservice;
import br.com.ggdio.microservice.ServiceHandler;
import br.com.ggdio.microservice.ServiceHandler.ServiceHandlerBuilder;

public class ShoppingCartMicroservice extends Microservice {
	
	public static final String NAME = "shopping-cart";
	
	private static List<String> shoppingCart = new ArrayList<>();

	public ShoppingCartMicroservice(int port) {
		super(NAME, port, buildRoutes(ServiceHandler.builder()), "shopping-cart", "caching");
	}
	
	private static ServiceHandler buildRoutes(ServiceHandlerBuilder builder) {
		getItems(builder);
		addItem(builder);
		getStatus(builder);
		
		return builder.build();
	}
	
	private static void getItems(ServiceHandlerBuilder builder) {
		builder.withGET("/items", (req, res) -> {
			String r = "[\n";
			for (String item : shoppingCart) {
				r = r.concat("\t").concat("\"" + item + "\"").concat(", \n");
			}
			return r.concat("]"); 
		});
	}
	
	private static void addItem(ServiceHandlerBuilder builder) {
		builder.withGET("/status", (req, res) -> {
			return "{ \"status\": \"OK\" }";
		});
	}
	
	private static void getStatus(ServiceHandlerBuilder builder) {
		builder.withPOST("/items/:name", (req, res) -> { 
			String item = req.params("name");
			shoppingCart.add(item);
			return "{\"message\": \""+item+" added to the shopping cart.\"}"; 
		});
	}

}
