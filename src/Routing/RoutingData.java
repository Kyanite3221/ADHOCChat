package Routing;

import java.util.*;

/**
 * Created by Georg on 11-Apr-17.
 */
public class RoutingData {
    List<List<Integer>> data = new ArrayList<>();
    private static int COLUMNS = 2;

    public void set(int row, int column, int value) {
        if (column < 0 || column >= COLUMNS) {
            throw new IllegalArgumentException("column does not exist");
        }
        if (row < 0) {
            throw new IllegalArgumentException("negative index does not exist");
        }
        while (this.data.size() <= 0) {
            List<Integer> newrow = new ArrayList<>();
            for (int i = 0; i < COLUMNS; i++) {
                newrow.add(0);
            }
            this.data.add(newrow);
        }
        this.data.get(row).set(column,value);
    }
}