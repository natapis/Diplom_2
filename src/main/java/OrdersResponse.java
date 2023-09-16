public class OrdersResponse {
    private String createdAt;
    private IngredientsForOrder ingredients;
    private String name;
    private float number;
    private String status;
    private String updatedAt;
    private String _id;
    public IngredientsForOrder getIngredientsForOrder(){
        return ingredients;
    }
}
