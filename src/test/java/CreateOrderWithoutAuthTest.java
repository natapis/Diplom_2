import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;


import static constant.Api.BASE_URL;
import static org.hamcrest.core.IsEqual.equalTo;

public class CreateOrderWithoutAuthTest {


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

    @DisplayName("Создание заказа с одним ингредиентом под неавторизованным пользователем")
    @Test
    public void createOrderOneIngredient() {
        Order order = new Order();
        int numberIngredient = faker.number().numberBetween(0, allIngredients.size() - 1);
        IngredientsForOrder ingredientsForOrder = new IngredientsForOrder();
        ingredientsForOrder.ingredients.add(allIngredients.get(numberIngredient)._id);
        Response createOrder = order.createOrderWithoutAuth(ingredientsForOrder);
        createOrder.then()
                .statusCode(401)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"));
    }

    @DisplayName("Создание заказа без ингредиентов под неавторизованным пользователем")
    @Test
    public void createOrderWithoutIngredients() {
        Order order = new Order();
        IngredientsForOrder ingredientsForOrder = new IngredientsForOrder();
        Response createOrder = order.createOrderWithoutAuth(ingredientsForOrder);
        createOrder.then()
                .body("success", equalTo(false))
                .and()
                .statusCode(401)
                .and()
                .body("message", equalTo("You should be authorised"));
    }

    @DisplayName("Создание заказа с неправильным хэшем ингредиента под неавторизованным пользователем")
    @Test
    public void createOrderWithWrongIngredients() {
        Order order = new Order();
        int countIngredient = faker.number().numberBetween(2, allIngredients.size() - 1);
        IngredientsForOrder ingredientsForOrder = new IngredientsForOrder();
        for (int i = 0; i <= countIngredient; i++) {
            int numberIngredient = faker.number().numberBetween(0, allIngredients.size() - 1);
            ingredientsForOrder.ingredients.add(allIngredients.get(numberIngredient)._id);
        }
        ingredientsForOrder.ingredients.set(0, ingredientsForOrder.ingredients.get(0) + "test");
        Response createOrder = order.createOrderWithoutAuth(ingredientsForOrder);
        createOrder.then()
                .statusCode(401)
                .and()
                .body("message", equalTo("You should be authorized"))
                .and()
                .body("success", equalTo(false));
    }

    @After
    public void tearDown() {
        if (loginResponse.statusCode() == 200) {
            Response deleteResponse = userClient.deleteUser(token.substring(7));
            Assert.assertEquals("Пользователь не удален", 202, deleteResponse.statusCode());
        }
    }
}
