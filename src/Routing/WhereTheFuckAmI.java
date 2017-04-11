package Routing;

import IPLayer.IPLayer;

/**
 * Created by Georg on 11-Apr-17.
 */
public class WhereTheFuckAmI {
    public static void main(String[] args) {
        IPLayer test = new IPLayer();
        System.out.println(test.ipStringToByteArray(test.getOwnIP()));
    }
}
