package com.java.Yahoo.stockAPI;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.java.StocksList.StocksList;
import com.java.dtoAPI.StockDTO;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

public class YahooStockAPI {

	// Get one stock
	public StockDTO getStock(String stockName) throws IOException {

		StockDTO dto = null;
		Stock stock = YahooFinance.get(stockName);
		dto = new StockDTO(stock.getName(), stock.getQuote().getPrice(), stock.getQuote().getChange(),
				stock.getCurrency(), stock.getQuote().getBid());

		return dto;
	}

	// Get multiple stocks
	public Map<String, Stock> getStocks(String[] stockNames) throws IOException {

		Map<String, Stock> stocks = YahooFinance.get(stockNames);
		for (Map.Entry<String, Stock> entry : stocks.entrySet()) {
			System.out.println("Key = " + entry.getKey() + ", Name = " + entry.getValue().getName() + ", Price = "
					+ entry.getValue().getQuote().getPrice() + "$ , Change = " + entry.getValue().getQuote().getChange()
					+ "% , Currency = " + entry.getValue().getCurrency() + ", Bid = "
					+ entry.getValue().getQuote().getBid() + "$");

			System.out.println("========================================");

		}

		return stocks;
	}

	// History for multiple Stocks
	public void getAllHistory(String[] stockNames, int year, int daysBegin, int day, String searchType)
			throws IOException {

		// if changed need to change in getAllHistory function as well
		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		from.add(Calendar.YEAR, Integer.valueOf("-" + year)); // Setting the year to start from
//		from.add(Calendar.MONTH, Integer.valueOf(10)); // Setting the month to start from
		from.add(Calendar.DAY_OF_WEEK, Integer.valueOf(daysBegin));

		// customized Date
		Calendar CustomizedDate = Calendar.getInstance();
		// To Check during that Date: 0 means today and -11 means eleven days ago and so
		CustomizedDate.add(Calendar.DAY_OF_WEEK, Integer.valueOf(day));

		BigDecimal maxPrice = new BigDecimal(0);
		BigDecimal minPrice = new BigDecimal(0);
		int countNulls = 0;
		String DatesofNulls = "";
		Map<String, Stock> stocks = YahooFinance.get(stockNames);

		for (Map.Entry<String, Stock> entry : stocks.entrySet()) {

			System.out.println("******************************************");
			System.out.println("******************************************");
			System.out.println("******************************************");

			Stock stock = YahooFinance.get(entry.getValue().getSymbol());
			List<HistoricalQuote> history = stock.getHistory(from, to, getInterval(searchType));

			minPrice = history.get(0).getLow();

			for (HistoricalQuote quote : history) {
				if (quote.getHigh() != null || quote.getLow() != null) {

					System.out.println("========================");
					System.out.println("symbol:" + quote.getSymbol());
					System.out.println("date:" + convertDate(quote.getDate()));
					;
					System.out.println("High:" + quote.getHigh());
					System.out.println("Low:" + quote.getLow());
					System.out.println("Open:" + quote.getOpen());
					System.out.println("Close:" + quote.getClose());
					System.out.println("Volume:" + quote.getVolume());
					System.out.println("===============================");

					// find highest High
					maxPrice = maxPrice.max(FindHigh(quote.getHigh(), maxPrice));

					// Find lowest Low
					minPrice = minPrice.min(FindLow(quote.getLow(), minPrice));
				} else {
					countNulls++;
					DatesofNulls = DatesofNulls + convertDate(quote.getDate()) + ", ";

				}
			}
		}

		System.out.println("Date is from: " + convertDate(from));
		System.out.println("Date selected: " + convertDate(CustomizedDate));
		System.out.println("Highest High: " + maxPrice + "$");
		System.out.println("Lowest Low: " + minPrice + "$");
		System.out.println("Number of Nulls: " + countNulls);
		System.out.println("Dates of Nulls: " + DatesofNulls);

	}

	// Return stocks with lowest lows
	public Map<String, Stock> LowestLows(String[] stockNames, int year, int daysBegin, int day,
			BigDecimal AllowedChange, String searchType) throws IOException {

		// if changed need to change in getAllHistory function as well
		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		from.add(Calendar.YEAR, Integer.valueOf("-" + year)); // Setting the year to start from
//		from.add(Calendar.MONTH, Integer.valueOf(10)); // Setting the month to start from
		from.add(Calendar.DAY_OF_WEEK, Integer.valueOf(daysBegin));

		// customized Date
		Calendar CustomizedDate = Calendar.getInstance();
		// To Check during that Date: 0 means today and -11 means eleven days ago and so
		CustomizedDate.add(Calendar.DAY_OF_WEEK, Integer.valueOf(day));

		BigDecimal minPrice = new BigDecimal(0);

		Map<String, Stock> Newstocks = new HashMap<String, Stock>();
		Map<String, Stock> stocks = YahooFinance.get(stockNames);
		int i = 1;

		for (Map.Entry<String, Stock> entry : stocks.entrySet()) {

			Stock stock = YahooFinance.get(entry.getValue().getSymbol());
			List<HistoricalQuote> history = stock.getHistory(from, to, getInterval(searchType));

			minPrice = history.get(0).getLow();

			for (HistoricalQuote quote : history) {
				// Find lowest Low
				if (quote.getLow() != null) {
					minPrice = minPrice.min(FindLow(quote.getLow(), minPrice));
				}
			}

			// Check lowest price within 15% change
			BigDecimal AllowedMaxPrice = minPrice.multiply((new BigDecimal(1)).add(AllowedChange));
			BigDecimal AllowedMinPrice = minPrice.multiply((new BigDecimal(1)).subtract(AllowedChange));

			for (HistoricalQuote quote : history) {

				if (convertDate(quote.getDate()).contentEquals(convertDate(CustomizedDate)) && quote.getLow() != null
						&& quote.getVolume() >= 200000 && (quote.getLow().compareTo(AllowedMinPrice) > 0
								&& quote.getLow().compareTo(AllowedMaxPrice) < 0)) {

					System.out.println("******************************************");
					System.out.println(i + ") " + quote.getSymbol() + " Found low at " + quote.getLow() + "$ during "
							+ convertDate(quote.getDate()));
					System.out.println("Max Price: "+AllowedMaxPrice);
					System.out.println("Min Price: "+AllowedMinPrice);
					Newstocks.put(quote.getSymbol(), stock);
				}
			}
			i++;
		}

		return Newstocks;
	}

	// Return stocks with lowest lows
	public Map<String, Stock> LowestLowsWithPullback(String[] stockNames, int year, int daysBegin, int day,
			BigDecimal changeRate, String searchType) throws IOException {

		// if changed need to change in getAllHistory function as well
		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		from.add(Calendar.YEAR, Integer.valueOf("-" + year)); // Setting the year to start from
//		from.add(Calendar.MONTH, Integer.valueOf(10)); // Setting the month to start from
		from.add(Calendar.DAY_OF_WEEK, Integer.valueOf(daysBegin));

		// customized Date
		Calendar CustomizedDate = Calendar.getInstance();
		// To Check during that Date: 0 means today and -11 means eleven days ago and so
		CustomizedDate.add(Calendar.DAY_OF_WEEK, Integer.valueOf(day));

		BigDecimal minPrice = new BigDecimal(0);

		Map<String, Stock> Newstocks = new HashMap<String, Stock>();
		Map<String, Stock> stocks = YahooFinance.get(stockNames);
		int i = 1;

		for (Map.Entry<String, Stock> entry : stocks.entrySet()) {

			Stock stock = YahooFinance.get(entry.getValue().getSymbol());
			List<HistoricalQuote> history = stock.getHistory(from, to, getInterval(searchType));

			minPrice = history.get(0).getLow();

			for (HistoricalQuote quote : history) {
				// Find lowest Low
				if (quote.getLow() != null) {
					minPrice = minPrice.min(FindLow(quote.getLow(), minPrice));
				}
			}

			// Check lowest price within % change
			BigDecimal AllowedMaxPrice = minPrice.multiply(changeRate);

			for (HistoricalQuote quote : history) {

				if (convertDate(quote.getDate()).contentEquals(convertDate(CustomizedDate)) && quote.getLow() != null
						&& quote.getVolume() >= 200000 && quote.getLow().compareTo(AllowedMaxPrice) > 0) {

					System.out.println("******************************************");
					System.out.println(i + ") " + quote.getSymbol() + " Found Pull Back at " + quote.getLow()
							+ "$ during " + convertDate(quote.getDate()));
					Newstocks.put(quote.getSymbol(), stock);
				}
			}
			i++;
		}

		return Newstocks;
	}

	// Convert the date
	private String convertDate(Calendar cal) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String format1 = format.format(cal.getTime());
		return format1;
	}

	// Set the interval
	private Interval getInterval(String searchType) {
		Interval interval = null;
		switch (searchType.toUpperCase()) {
		case "MONTHLY":
			interval = Interval.MONTHLY;
			break;

		case "WEEKLY":
			interval = Interval.WEEKLY;
			break;

		case "DAILY":
			interval = Interval.DAILY;
			break;

		}
		return interval;
	}

	public BigDecimal FindHigh(BigDecimal CurrentPrice, BigDecimal MaxPrice) {
		// max function will calculate biggest between both values and store in Max
		BigDecimal Max = CurrentPrice.max(MaxPrice);
		return Max;
	}

	public BigDecimal FindLow(BigDecimal CurrentPrice, BigDecimal MinPrice) {
		// min function will calculate smallest between both values and store in Min
		BigDecimal Min = CurrentPrice.min(MinPrice);
		return Min;
	}

	public void PrintPossibleStocks(Map<String, Stock> StockNames) throws IOException {

		System.out.println("******************************************");
		System.out.println("Stocks found: ");
		int i = 1;
		for (Map.Entry<String, Stock> entry : StockNames.entrySet()) {

			Stock stock = YahooFinance.get(entry.getValue().getSymbol());
			System.out.println(i + ") " + stock.getSymbol() + " - At Price: " + stock.getQuote().getPrice() + "$");
			i++;
		}
	}

}
