package order;

import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import user.*;

import java.util.ArrayList;

import static constant.Api.BASE_URL;
import static org.hamcrest.core.IsEqual.equalTo;

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
    }

    @DisplayName("Получение списка из одного заказа конкретного пользователя с авторизацией")
    @Test
    public void getOrdersOneOrder() {
        Order order = new Order();
        int numberIngredient = faker.number().numberBetween(0, allIngredients.size() - 1);
        IngredientsForOrder ingredientsForOrder = new IngredientsForOrder();
        ingredientsForOrder.getIngredients().add(allIngredients.get(numberIngredient).getId());
        order.createOrderWithAuth(token, ingredientsForOrder);
        Response getOrders = userClient.getInfoOrderWithAuth(token);
        getOrders.then()
                .statusCode(200)
                .and()
                .body("success", equalTo(true));
        float totalUser = getOrders.body().as(ListOrdersResponse.class).getOrders().size();
        Assert.assertEquals(1, totalUser, 0);
    }

    @DisplayName("Получение списка из нескольких заказов конкретного пользователя с авторизацией")
    @Test
    public void getOrderFewOrder() {
        Order orderOne = new Order();
        int countIngredient = faker.number().numberBetween(1, allIngredients.size() - 1);
        IngredientsForOrder ingredientsForOrder = new IngredientsForOrder();
        for (int i = 0; i <= countIngredient; i++) {
            int numberIngredient = faker.number().numberBetween(0, allIngredients.size() - 1);
            ingredientsForOrder.getIngredients().add(allIngredients.get(numberIngredient).getId());
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

    @DisplayName("Получение пустого списка заказов конкретного пользователя с авторизацией")
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

    @After
    public void tearDown() {
        if (loginResponse.statusCode() == 200) {
            Response deleteResponse = userClient.deleteUser(token.substring(7));
            Assert.assertEquals("Пользователь не удален", 202, deleteResponse.statusCode());
        }
    }
}
