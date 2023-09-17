package order;

import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import order.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import user.*;

import java.util.ArrayList;

import static constant.Api.BASE_URL;
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
    }

    @DisplayName("Создание заказа с одним ингредиентом под авторизованным пользователем")
    @Test
    public void createOrderOneIngredient() {
        Order order = new Order();
        int numberIngredient = faker.number().numberBetween(0, allIngredients.size() - 1);
        IngredientsForOrder ingredientsForOrder = new IngredientsForOrder();
        ingredientsForOrder.getIngredients().add(allIngredients.get(numberIngredient).getId());
        Response createOrder = order.createOrderWithAuth(token, ingredientsForOrder);
        createOrder.then()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("name", notNullValue());
    }

    @DisplayName("Создание заказа с несколькими ингредиентами под авторизованным пользователем")
    @Test
    public void createOrderFewIngredients() {
        Order order = new Order();
        IngredientsForOrder ingredientsForOrder = new IngredientsForOrder();
        int countIngredient = faker.number().numberBetween(2, allIngredients.size() - 2);
        for (int i = 0; i <= countIngredient; i++) {
            int numberIngredient = faker.number().numberBetween(0, allIngredients.size() - 1);
            ingredientsForOrder.getIngredients().add(allIngredients.get(numberIngredient).getId());
        }
        Response createOrder = order.createOrderWithAuth(token, ingredientsForOrder);
        createOrder.then()
                .body("success", equalTo(true))
                .and()
                .statusCode(200)
                .and()
                .body("name", notNullValue());
    }

    @DisplayName("Создание заказа без ингредиентов под авторизованным пользователем")
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

    @DisplayName("Создание заказа с неправильным хэшем ингредиента под авторизованным пользователем")
    @Test
    public void createOrderWithWrongIngredients() {
        Order order = new Order();
        int countIngredient = faker.number().numberBetween(2, allIngredients.size() - 1);
        IngredientsForOrder ingredientsForOrder = new IngredientsForOrder();
        for (int i = 0; i <= countIngredient; i++) {
            int numberIngredient = faker.number().numberBetween(0, allIngredients.size() - 1);
            ingredientsForOrder.getIngredients().add(allIngredients.get(numberIngredient).getId());
        }
        ingredientsForOrder.getIngredients().set(0, ingredientsForOrder.getIngredients().get(0) + "test");
        Response createOrder = order.createOrderWithAuth(token, ingredientsForOrder);
        createOrder.then()
                .statusCode(500);
    }

    @After
    public void tearDown() {
        if (loginResponse.statusCode() == 200) {
            Response deleteResponse = userClient.deleteUser(token.substring(7));
            Assert.assertEquals("Пользователь не удален", 202, deleteResponse.statusCode());
        }
    }
}
