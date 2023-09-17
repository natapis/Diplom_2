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

public class GetOrderUserWithoutAuthTest {


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

    @DisplayName("Получение заказа конкретного пользователя без авторизации")
    @Test
    public void getOrdersOneOrder() {
        Order order = new Order();
        int numberIngredients = faker.number().numberBetween(0, allIngredients.size() - 1);
        IngredientsForOrder ingredientsForOrder = new IngredientsForOrder();
        ingredientsForOrder.getIngredients().add(allIngredients.get(numberIngredients).getId());
        order.createOrderWithAuth(token, ingredientsForOrder);
        Response getOrders = userClient.getInfoOrderWithoutAuth();
        getOrders.then()
                .statusCode(401)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"));
    }

    @After
    public void tearDown() {
        if (loginResponse.statusCode() == 200) {
            Response deleteResponse = userClient.deleteUser(token.substring(7));
            Assert.assertEquals("Пользователь не удален", 202, deleteResponse.statusCode());
        }
    }


}
