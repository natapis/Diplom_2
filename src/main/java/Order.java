import io.restassured.response.Response;

import java.util.ArrayList;

import static constant.Api.*;
import static io.restassured.RestAssured.given;

public class Order {
 //   private ArrayList<String> ingredients;
    public Order(){

    }
    public Response createOrderWithAuth(String token, IngredientsForOrder ingredients){
        return given()
                .auth().oauth2(token.substring(7))
                .and()
                .header("Content-type","application/json")
                .and()
                .body(ingredients)
                .when()
                .post(CREATE_ORDER_API);
    }

    public Response createOrderWithoutAuth(IngredientsForOrder ingredients){
        return given()
                .header("Content-type","application/json")
                .and()
                .body(ingredients)
                .when()
                .post(CREATE_ORDER_API);
    }


}
