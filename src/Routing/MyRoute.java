package Routing;

/**
 * Created by Georg on 11-Apr-17.
 */
public class MyRoute {
    private byte[] destination;
    private byte[] nexthop;
    private int cost;

    public MyRoute (byte[] destination, byte[] nexthop, int cost) {
        this.destination = destination;
        this.nexthop = nexthop;
        this.cost = cost;
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

    public byte[] getDestination() {
        return destination;
    }

    public byte[] getNexthop() {
        return nexthop;
    }

    public int getCost() {
        return cost;
    }

}
