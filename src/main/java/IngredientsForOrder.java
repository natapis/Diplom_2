import io.restassured.response.Response;

import java.util.ArrayList;

import static constant.Api.GET_INGREDIENTS;
import static io.restassured.RestAssured.given;

public class IngredientsForOrder {
    private ArrayList<String> ingredients = new ArrayList<>();
    public IngredientsForOrder(){

    }
    public ArrayList<String> getIngredients(){
        return ingredients;
    }

}
