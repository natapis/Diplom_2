import io.restassured.response.Response;

import java.util.ArrayList;

import static constant.Api.CREATE_GET_ORDER_API;
import static constant.Api.CREATE_USER_API;
import static io.restassured.RestAssured.given;

public class Order {
 //   private ArrayList<String> ingredients;
    public Order(){

    }
    public Response createOrderWithAuth(String token, ArrayList<Ingredient> ingredients){
        return given()
                .auth().oauth2(token)
                .and()
                .header("Content-type","application/json")
                .and()
                .body(ingredients)
                .when()
                .post(CREATE_GET_ORDER_API);
    }

    public Response createOrderWithoutAuth(ArrayList<Ingredient> ingredients){
        return given()
                .header("Content-type","application/json")
                .and()
                .body(ingredients)
                .when()
                .post(CREATE_GET_ORDER_API);
    }

    public Response getInfoOrderWithAuth(String token){
        return given()
                .auth()
                .oauth2(token)
                .and()
                .get(CREATE_GET_ORDER_API);
    }
    public Response getInfoOrderWithoutAuth(){
        return given()
                .get(CREATE_GET_ORDER_API);
    }
}
