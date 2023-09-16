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
//        token = null;
        userClient.createUser(user);
        Ingredients ingredients = new Ingredients();
        Response ingredientResponse = ingredients.getIngredients();
        allIngredients = ingredientResponse.as(IngredientResponse.class).getData();
        loginResponse = userClient.loginUser(UserCreds.credsForm(user));
        token = loginResponse.body().as(LoginResponse.class).getAccessToken();
        Random random = new Random();
    }

    @Test
    public void createOrderOneIngredient() {
//        User userFake = user;
//        String newEmail = faker.internet().emailAddress();
//        userFake.setEmail(newEmail);
 //       int numberIngredient = random(14);
        Order order = new Order();
        ArrayList<Ingredient> createIngredientsOrder = new ArrayList<>();
        createIngredientsOrder.add(allIngredients.get(2));
        Response createOrder = order.createOrderWithAuth(token, createIngredientsOrder);
        createOrder.then()
                .body("success", equalTo(true))
                .and()
                .statusCode(200)
                .and()
                .body("name", notNullValue());
 //       Response loginResponseNewEmail = userClient.loginUser(UserCreds.credsForm(user));
 //       loginResponseNewEmail.then().statusCode(200);
    }

    @Test
    public void createOrderFewIngredients() {
        Order order = new Order();
        ArrayList<Ingredient> createIngredientsOrder = new ArrayList<>();
        for (int i=0; i<=3; i++){
            createIngredientsOrder.add(allIngredients.get(i));
        }
        Response createOrder = order.createOrderWithAuth(token, createIngredientsOrder);
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
        ArrayList<Ingredient> createIngredientsOrder = new ArrayList<>();
        Response createOrder = order.createOrderWithAuth(token, createIngredientsOrder);
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
        ArrayList<Ingredient> createIngredientsOrder = new ArrayList<>();
        for (int i=0; i<=3; i++){
            createIngredientsOrder.add(allIngredients.get(i));
        }
        createIngredientsOrder.get(0).id= createIngredientsOrder.get(0).id + "test";
        Response createOrder = order.createOrderWithAuth(token, createIngredientsOrder);
        createOrder.then()
                .body("success", equalTo(false))
                .and()
                .statusCode(500)
                .and()
                .body("message", equalTo("Internal server error"));
    }

    public void tearDown() {
 //       Response loginResponseNew = userClient.loginUser(UserCreds.credsForm(user));
        if (loginResponse.statusCode() == 200) {
      //      token = loginResponse.body().as(LoginResponse.class).getAccessToken();
//            UserClient userClient = new UserClient();
            Response deleteResponse = userClient.deleteUser(token.substring(7));
            System.out.println(token);
            Assert.assertEquals("Пользователь не удален", 202, deleteResponse.statusCode());

        }
    }


}
