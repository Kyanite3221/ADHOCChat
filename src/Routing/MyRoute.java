package Routing;


import java.util.Arrays;

/**
 * Created by Georg on 11-Apr-17.
 */
public class MyRoute {
    private byte[] destination;
    private byte[] nexthop;
    private int cost;
    private String name;

    public MyRoute (byte[] destination, byte[] nexthop, int cost, String name) {
        this.destination = destination;
        this.nexthop = nexthop;
        this.cost = cost;
        this.name = name;
    }
    public MyRoute (byte[] destination, byte[] nexthop, int cost, byte[] name) {
        this.destination = destination;
        this.nexthop = nexthop;
        this.cost = cost;
        String tempname = new String(name);
        this.name = tempname;
    }

    public void setDestination (byte[] dest) {
        this.destination = dest;
    }

    public void setNexthop (byte[] nexthop) {
        this.nexthop = nexthop;
    }

    public void setCost (int cost) {
        this.cost = cost;
    }

    public void setName (String name) {this.name = name;}

    public byte[] getDestination() {
        return destination;
    }

    public byte[] getNexthop() {
        return nexthop;
    }

    public int getCost() {
        return cost;
    }

    public byte getCostAsByte () {
        return (byte) cost;
    }

    public String getName() {
        return name;
    }

    public byte[] getNameAsByte () {
        return name.getBytes();
    }

    @Override
    public String toString() {
        return "MyRoute{" +
                "destination=" + Arrays.toString(destination) +
                ", nexthop=" + Arrays.toString(nexthop) +
                ", cost=" + cost +
                ", name='" + name + '\'' +
                '}';
    }
}
