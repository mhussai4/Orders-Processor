# Orders-Processor
Project Overview
The OrdersProcessor is a Java program that helps process customer orders. It can work in two ways: one order at a time (single-threaded) or many orders at once (multi-threaded). The program reads item prices from a file and then processes customer orders to calculate the total cost for each. It saves all the results, including a summary of how many items were sold and the total money earned, in a result file.

Features
Order Processing: The program reads customer orders from text files. Each order includes items and their quantities, which are matched with their prices from another file.

Single and Multi-Threading: You can choose how to process the orders:

Single-threaded: Processes each order one by one.
Multi-threaded: Handles multiple orders at the same time, which is faster when you have a lot of orders.
Flexible Input and Output:

You provide the file with item prices, the number of orders, the base name of the order files, and the name of the output file.
The result file contains detailed information about each customer’s order and a summary of total items sold and total earnings.
Error Handling: If a file is missing or something goes wrong, the program will notify you without crashing.






