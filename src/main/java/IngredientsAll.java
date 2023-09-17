import io.restassured.response.Response;

import java.util.ArrayList;

import static constant.Api.GET_INGREDIENTS;
import static io.restassured.RestAssured.given;

public class IngredientsAll {
    private ArrayList<String> ingredients;
    public IngredientsAll(){

    }
    public Response getIngredients(){
        return given()
                .get(GET_INGREDIENTS);
    }
}
