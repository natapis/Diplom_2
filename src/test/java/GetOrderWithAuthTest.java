import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Random;

import static constant.Api.BASE_URL;
import static java.lang.Math.random;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

public class GetOrderWithAuthTest {


    private String token;
    private UserClient userClient = new UserClient();
    private User user = UserGenerator.requiredFields();
    private Faker faker = new Faker();
    private ArrayList<Ingredient> allIngredients;
    private Response loginResponse;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        userClient.createUser(user);
        IngredientsAll ingredientsAll = new IngredientsAll();
        Response ingredientResponse = ingredientsAll.getIngredients();
        allIngredients = ingredientResponse.as(IngredientResponse.class).getData();
        loginResponse = userClient.loginUser(UserCreds.credsForm(user));
        token = loginResponse.body().as(LoginResponse.class).getAccessToken();
        Random random = new Random();
    }

    @Test
    public void getOrdersOneOrder() {
        Order order = new Order();
        IngredientsForOrder ingredientsForOrder = new IngredientsForOrder();
        ingredientsForOrder.ingredients.add(allIngredients.get(2)._id);
        order.createOrderWithAuth(token, ingredientsForOrder);
        Response getOrders = userClient.getInfoOrderWithAuth(token);
        getOrders.then()
                .statusCode(200)
                .and()
                .body("success", equalTo(true));
        float totalUser = getOrders.body().as(ListOrdersResponse.class).getOrders().size();
        Assert.assertEquals(1, totalUser, 0);
 //       Response createOrder = order.createOrderWithAuth(token, ingredientsForOrder);
 //       createOrder.then()
 //               .statusCode(200)
//                .and()
 //               .body("success", equalTo(true))
 //               .and()
  //              .body("name", notNullValue());

    }

    @Test
    public void getOrderFewOrder() {
        Order orderOne = new Order();
        IngredientsForOrder ingredientsForOrder = new IngredientsForOrder();
        for (int i=0; i<=3; i++){
            ingredientsForOrder.ingredients.add(allIngredients.get(i)._id);
        }
        orderOne.createOrderWithAuth(token, ingredientsForOrder);
        Order orderTwo = new Order();
        orderTwo.createOrderWithAuth(token, ingredientsForOrder);
        Response getOrders = userClient.getInfoOrderWithAuth(token);
        getOrders.then()
                .body("success", equalTo(true))
                .and()
                .statusCode(200);
        float totalUser = getOrders.body().as(ListOrdersResponse.class).getOrders().size();
        Assert.assertEquals(2, totalUser, 0);
    }

    @Test
    public void getOrderNullOrder() {
        Response getOrder = userClient.getInfoOrderWithAuth(token);
        getOrder.then()
                .body("success", equalTo(true))
                .and()
                .statusCode(200);
        float totalUser = getOrder.body().as(ListOrdersResponse.class).getOrders().size();
        Assert.assertEquals(0, totalUser, 0);
    }

    public void tearDown() {
        if (loginResponse.statusCode() == 200) {
            Response deleteResponse = userClient.deleteUser(token.substring(7));
            System.out.println(token);
            Assert.assertEquals("Пользователь не удален", 202, deleteResponse.statusCode());

        }
    }


}
