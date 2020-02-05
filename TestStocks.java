package com.java.TestStocks;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import com.java.StocksList.StocksList;
import com.java.Yahoo.stockAPI.YahooStockAPI;

import yahoofinance.Stock;

public class TestStocks {

	public static void main(String[] args) throws IOException {

		YahooStockAPI yahooStockAPI = new YahooStockAPI();

		StocksList stocksList = new StocksList();
		String[] stockNames = stocksList.getListofStocks();
		String[] StocksLessThanOe = stocksList.getStocksLessThanOne();
		String[] StocksBetweenCentsAndThree = stocksList.getStocksBetweenCentsAndThree();

//		String[] OneStock= {"LTFD"};		
//		yahooStockAPI.getAllHistory(OneStock, 1, "daily");

		// StockList , 1 Year, Days before, Current day(0 for today -1 yesterday)
		Map<String, Stock> NewStocks = yahooStockAPI.LowestLows(StocksBetweenCentsAndThree, 1, -14, -1,
				new BigDecimal(0.05), "daily");
		yahooStockAPI.PrintPossibleStocks(NewStocks);

		// StockList , 1 Year, Days before, Current day(0 for today -1 yesterday)
		// ,change in percent
//		Map<String, Stock> NewStocks=yahooStockAPI.LowestLowsWithPullback(StocksBetweenCentsAndThree, 1, -14, -1,new BigDecimal(1.15) , "daily");
//		yahooStockAPI.PrintPossibleStocks(NewStocks);

	}

}
