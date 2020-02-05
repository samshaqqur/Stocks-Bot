package com.java.dtoAPI;

import java.math.BigDecimal;


public class StockDTO {

private String name;
private BigDecimal price;
private BigDecimal change;
private String currency;
private BigDecimal bid;

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public BigDecimal getPrice() {
	return price;
}

public void setPrice(BigDecimal price) {
	this.price = price;
}

public BigDecimal getChange() {
	return change;
}

public void setChange(BigDecimal change) {
	this.change = change;
}

public String getCurrency() {
	return currency;
}

public void setCurrency(String currency) {
	this.currency = currency;
}

public BigDecimal getBid() {
	return bid;
}

public void setBid(BigDecimal bid) {
	this.bid = bid;
}

public StockDTO(String name, BigDecimal price, BigDecimal change, String currency, BigDecimal bid) {
	super();
	this.name = name;
	this.price = price;
	this.change = change;
	this.currency = currency;
	this.bid = bid;
}

@Override
public String toString() {
	return "StockDTO [name=" + name + ", price=" + price + "$ , change=" + change + "% , currency=" + currency + ", bid="
			+ bid + "]";
}





}
