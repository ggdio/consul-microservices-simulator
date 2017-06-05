package br.com.ggdio;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import com.orbitz.consul.model.health.ServiceHealth;

import br.com.ggdio.microservice.Microservice;
import br.com.ggdio.microservice.ServiceHandler;
import br.com.ggdio.microservice.ServiceHandler.ServiceHandlerBuilder;

/**
 * Payment microservice that integrates with the {@link ShoppingCartMicroservice} through consul.io SD.
 * 
 * ...Don't mind the static refs, it's just XGH  :p 
 * 
 * @author Guilherme Dio
 *
 */
public class PaymentMicroservice extends Microservice {
	
	public static final String NAME = "payment";

	public PaymentMicroservice(int port) {
		super(NAME, port, buildRoutes(ServiceHandler.builder()), NAME);
	}
	
	private static ServiceHandler buildRoutes(ServiceHandlerBuilder builder) {
		getTotal(builder);
		
		return builder.build();
	}
	
	private static void getTotal(ServiceHandlerBuilder builder) {
		builder.withGET("/total", (req, res) -> {
			List<ServiceHealth> instances = App.consul.healthClient().getHealthyServiceInstances("shopping-cart").getResponse();
			
			ServiceHealth service = instances.get(sortIndex(instances));
			String host = service.getNode().getAddress();
			String port = String.valueOf(service.getService().getPort());
			String endpoint = "http://" + host + ":" + port;
			
			URL url = new URL(endpoint);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			
			conn.getResponseCode();
			
			int total = 0;
			
			InputStream is = new URL(endpoint + "/items").openConnection().getInputStream();
			Scanner scanner = new Scanner(is);
			String response = "{\n";
			while(scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if(line.contains("[")) continue;
				if(line.contains("]")) continue;
				
				String item = line.replaceAll(",", "");
				
				int value = sortIndex(50, 2);
				total += value;
				
				response = response.concat(item).concat(String.valueOf(value)).concat(",\n");
			}
			response = response.concat("\t\"_TOTAL\": ").concat(String.valueOf(total)).concat("\n");
			scanner.close();
			is.close();
			return response.concat("}");
		});
	}
	
	private static int sortIndex(List<?> list) {
		int max = list.size() - 1;
		int min = 0;
		return sortIndex(max, min);
	}

	private static int sortIndex(int max, int min) {
		return new Random().nextInt(max - min + 1) + min;
	}

}
