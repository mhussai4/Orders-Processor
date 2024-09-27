package processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class OrderProcessorThread implements Runnable {
	private TreeMap<String, Integer> orders;
	private File orderFileName;
	private TreeMap<String, Double> prices;

	public OrderProcessorThread(String fileName, TreeMap<String, Double> prices) {
		this.orderFileName = new File(fileName);
		this.prices = prices;
		this.orders = new TreeMap<String, Integer>();
	}

	@Override
	public void run() {
		// Process the order
		try {
			Scanner scanner = new Scanner(orderFileName);
			scanner.next();
			System.out.println("Reading order for client with id: " + scanner.next());
			while (scanner.hasNext()) {
				String line = scanner.next();
				if (!orders.containsKey(line)) {
					orders.put(line, 1);
				} else {
					orders.put(line, orders.get(line) + 1);
				}
				scanner.next();
			}

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
	}
	
	
	public String getTotalCost() {
	    double cost = 0;
	    DecimalFormat df = new DecimalFormat("#.00");
	    for (Map.Entry<String, Integer> order : orders.entrySet()) {
	        cost += orders.get(order.getKey()).doubleValue() * prices.get(order.getKey());
	    }
	    // Format the cost to display two decimal places
	    String formattedCost = df.format(cost);

	    return formattedCost;
	}
	
	public String toString() {
		String string ="";
		DecimalFormat df = new DecimalFormat("#0.00");
		for (Map.Entry<String, Integer> order : orders.entrySet()) {
			if(order.getValue() > 0) {
				String formattedCostPerItem = df.format(prices.get(order.getKey()));
				string += "Item's name: " + order.getKey() + ", Cost per item: $" + formattedCostPerItem
				+ ", Quantity: " + order.getValue() + ", Cost: $" + df.format(order.getValue() * prices.get(order.getKey())) + "\n";
			}
		}
		string += "Order Total: $"+ this.getTotalCost() + "\n";
		return string;
	}
	
	

	public int get(String item) {
		if (orders.containsKey(item)) {
			return orders.get(item);
		}else {
			return 0;
		}
	}
	
}