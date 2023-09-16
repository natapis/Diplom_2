import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static constant.Api.BASE_URL;
import static java.lang.Math.random;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

public class CreateOrderWithAuthTest {


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
    public void createOrderOneIngredient() {
        Order order = new Order();
        IngredientsForOrder ingredientsForOrder = new IngredientsForOrder();
        ingredientsForOrder.ingredients.add(allIngredients.get(2)._id);
        Response createOrder = order.createOrderWithAuth(token, ingredientsForOrder);
        createOrder.then()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("name", notNullValue());
    }

    @Test
    public void createOrderFewIngredients() {
        Order order = new Order();
        IngredientsForOrder ingredientsForOrder = new IngredientsForOrder();
        for (int i=0; i<=3; i++){
            ingredientsForOrder.ingredients.add(allIngredients.get(i)._id);
        }
        Response createOrder = order.createOrderWithAuth(token, ingredientsForOrder);
        createOrder.then()
                .body("success", equalTo(true))
                .and()
                .statusCode(200)
                .and()
                .body("name", notNullValue());
    }

    @Test
    public void createOrderWithoutIngredients() {
        Order order = new Order();
        IngredientsForOrder ingredientsForOrder = new IngredientsForOrder();
        Response createOrder = order.createOrderWithAuth(token, ingredientsForOrder);
        createOrder.then()
                .body("success", equalTo(false))
                .and()
                .statusCode(400)
                .and()
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    public void createOrderWithWrongIngredients() {
        Order order = new Order();
        IngredientsForOrder ingredientsForOrder = new IngredientsForOrder();
        for (int i=0; i<=3; i++){
            ingredientsForOrder.ingredients.add(allIngredients.get(i)._id);
        }
        ingredientsForOrder.ingredients.set(0, ingredientsForOrder.ingredients.get(0) + "test");
        Response createOrder = order.createOrderWithAuth(token, ingredientsForOrder);
        createOrder.then()
                .statusCode(500)
                .and()
                .body("message", equalTo("Internal server error"));
    }


    public void tearDown() {
        if (loginResponse.statusCode() == 200) {
            Response deleteResponse = userClient.deleteUser(token.substring(7));
            System.out.println(token);
            Assert.assertEquals("Пользователь не удален", 202, deleteResponse.statusCode());

        }
    }


}
