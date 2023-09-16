import java.util.ArrayList;

public class IngredientResponse {
    boolean success;
    ArrayList<Ingredient> data;

    public IngredientResponse(){

    }

    public ArrayList<Ingredient> getData() {
        return data;
    }
}
