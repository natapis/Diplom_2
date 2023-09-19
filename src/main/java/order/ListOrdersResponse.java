package order;

import order.IngredientsForOrder;

import java.util.ArrayList;

public class ListOrdersResponse {
    private ArrayList<IngredientsForOrder> orders;
    private boolean success;
    private float total;
    private float totalToday;
    public ArrayList<IngredientsForOrder> getOrders(){
        return orders;
    }

}
