import io.restassured.response.Response;

import static constant.Api.GET_INGREDIENTS;
import static io.restassured.RestAssured.given;

public class Ingredients {
    public Ingredients(){

    }
    public Response getIngredients(){
        return given()
                .get(GET_INGREDIENTS);
    }
}
