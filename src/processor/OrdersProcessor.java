package processor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import tests.TestingSupport;

public class OrdersProcessor {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		TreeMap<String, Double> prices = new TreeMap<String, Double>();

		System.out.print("Enter item's data file name: ");
		String itemDataFileName = scanner.next();

		try {
			Scanner scanner2 = new Scanner(new File(itemDataFileName));
			while (scanner2.hasNext()) {
				String itemName = scanner2.next();
				Double price = scanner2.nextDouble();
				prices.put(itemName, price);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		System.out.print("Enter 'y' for multiple threads, any other character otherwise: ");
		boolean useMultipleThreads = scanner.next().equalsIgnoreCase("y");

		System.out.print("Enter number of orders to process: ");
		int numOrders = scanner.nextInt();

		System.out.print("Enter order's base filename: ");
		String orderBaseFilename = scanner.next();

		System.out.print("Enter result's filename: ");
		String resultFilename = scanner.next();
		long startTime = System.currentTimeMillis();

		scanner.close();

		TreeMap<Integer, OrderProcessorThread> customers = new TreeMap<Integer, OrderProcessorThread>();

		if (useMultipleThreads) {
			multiThreadOrder(customers, prices, orderBaseFilename, itemDataFileName, numOrders);
		} else {
			singleThreadOrder(customers, prices, orderBaseFilename, numOrders);
		}

		writeToFile(customers, prices, resultFilename);
		long endTime = System.currentTimeMillis();
		System.out.println("Processing time (msec): " + (endTime - startTime));
	}

	private static void singleThreadOrder(TreeMap<Integer, OrderProcessorThread> customers,
			TreeMap<String, Double> prices, String orderBaseFileName, int numOrders) {
		for (int i = 1; i <= numOrders; i++) {
			try {
				Scanner scanner = new Scanner(new File(orderBaseFileName + Integer.toString(i) + ".txt"));
				scanner.next();
				OrderProcessorThread order = new OrderProcessorThread(orderBaseFileName + Integer.toString(i) + ".txt",
						prices);
				order.run();
				customers.put(scanner.nextInt(), order);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		}

	}

	private static void multiThreadOrder(TreeMap<Integer, OrderProcessorThread> customers,
			TreeMap<String, Double> prices, String orderBaseFileName, String referenceFile, int numOrders) {

		Thread[] threads = new Thread[numOrders];
		synchronized (customers) {

			for (int i = 1; i <= numOrders; i++) {
				try {
					File file = new File(orderBaseFileName + Integer.toString(i) + ".txt");
					Scanner scanner = new Scanner(file);
					scanner.next();
					OrderProcessorThread order = new OrderProcessorThread(
							orderBaseFileName + Integer.toString(i) + ".txt", prices);
					Thread newThread = new Thread(order);
					threads[i - 1] = newThread;
					newThread.start();
					customers.put(scanner.nextInt(), order);
					scanner.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}

			for (Thread thread : threads) {
				try {
					thread.join();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void writeToFile(TreeMap<Integer, OrderProcessorThread> orders, TreeMap<String, Double> prices,
			String resultFilename) {

		NumberFormat df = NumberFormat.getInstance();
		df.setGroupingUsed(true);
		df.setMinimumFractionDigits(2);
		df.setMaximumFractionDigits(2);

		FileWriter output;
		try {
			output = new FileWriter(resultFilename);
			for (Map.Entry<Integer, OrderProcessorThread> order : orders.entrySet()) {
				output.write("----- Order details for client with Id: " + order.getKey() + " -----\n");
				output.write(order.getValue().toString());
			}

			output.write("***** Summary of all orders *****\n");
			double totalCost = 0;
			for (String item : prices.keySet()) {
				int numProducts = 0;
				for (Integer key : orders.keySet()) {
					numProducts += orders.get(key).get(item);
				}

				if (numProducts > 0) {
					// Format the cost per item and item total with two decimal places
					String formattedCostPerItem = df.format(prices.get(item));
					String formattedItemTotal = df.format(numProducts * prices.get(item));

					output.write("Summary - Item's name: " + item + ", Cost per item: $" + formattedCostPerItem);
					output.write(", Number sold: " + numProducts + ", Item's Total: $" + formattedItemTotal + "\n");

				}

				totalCost += numProducts * prices.get(item);
			}
			output.write("Summary Grand Total: $" + df.format(totalCost) + "\n");
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
