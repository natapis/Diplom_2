import java.util.ArrayList;

public class ListOrdersResponse {
    private ArrayList<IngredientsForOrder> orders;
    boolean success;
    float total;
    float totalToday;
    public ArrayList<IngredientsForOrder> getOrders(){
        return orders;
    }

}
