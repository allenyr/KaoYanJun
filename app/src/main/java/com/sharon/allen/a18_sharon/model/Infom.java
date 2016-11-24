package com.sharon.allen.a18_sharon.model;

public class Infom {
	String name;
	String nature;


	public Infom() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Infom(String name, String nature) {
		super();
		this.name = name;
		this.nature = nature;
	}

	public String getNature() {
		return nature;
	}

	public void setNature(String nature) {
		this.nature = nature;
	}

	@Override
	public String toString() {
		return "Infom [name=" + name + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
