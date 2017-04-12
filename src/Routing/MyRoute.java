package Routing;

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

    public String getName() {
        return name;
    }

}
