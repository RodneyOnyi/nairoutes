package com.rontech.nairoutes.model;


public class Route {
	private String  route, stage, fare, bus_stop;
	private int id;
	
	public Route() {
	}

	public Route(String name, String fare, String stage,String bus_stop) {
		
		this.route = name;
		this.fare = fare;
		this.stage = stage;
		this.bus_stop = bus_stop;
	}
	
	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String name) {
		this.route = name;
	}


	public String getFare() {
		return fare;
	}

	public void setFare(String fare) {
		this.fare = fare;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}
	
	public String getBus_Stop() {
		return bus_stop;
	}

	public void setBus_Stop(String bus_stop) {
		this.bus_stop = bus_stop;
	}
	

}
