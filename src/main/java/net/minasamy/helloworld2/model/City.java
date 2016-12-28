package net.minasamy.helloworld2.model;

/**
 * Created by Mina.Samy on 11/13/2016.
 */
public class City {

    public String name;

    public City(String name){
        this.name=name;
    }
    public static final City WARSAW=new City("Warsaw");
    public static final City LONDON=new City("London");
    public static final City PARIS=new City("Paris");
}
