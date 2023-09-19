package order;

import io.restassured.response.Response;
import order.IngredientsForOrder;

import static constant.Api.*;
import static io.restassured.RestAssured.given;

public class Order {
    public Order() {

    }

    public Response createOrderWithAuth(String token, IngredientsForOrder ingredients) {
        return given()
                .auth().oauth2(token.substring(7))
                .and()
                .header("Content-type", "application/json")
                .and()
                .body(ingredients)
                .when()
                .post(CREATE_ORDER_API);
    }

    public Response createOrderWithoutAuth(IngredientsForOrder ingredients) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(ingredients)
                .when()
                .post(CREATE_ORDER_API);
    }


}
