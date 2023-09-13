import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static constant.Api.BASE_URL;
import static org.hamcrest.core.IsEqual.equalTo;

public class UserCreateTest {
    private Response loginResponse;
    private String token;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        loginResponse = null;
        token = null;
    }

    @Test
    public void createUserFullDateTest() {
        UserClient userClient = new UserClient();
        User user = UserGenerator.requiredFields();
        Response createResponse = userClient.createUser(user);
        Assert.assertEquals("неверный статус ответа", 200, createResponse.statusCode());
        loginResponse = userClient.loginUser(UserCreds.credsForm(user));
        Assert.assertEquals("Не логинится", 200, loginResponse.statusCode());
    }
    @Test
    public void createUserDoubleNameTest() {
        UserClient userClient = new UserClient();
        User user = UserGenerator.requiredFields();
        userClient.createUser(user);
        User doubleUser = UserGenerator.doubleUserEmail(user.getEmail());
        Response createResponse = userClient.createUser(doubleUser);
        createResponse.then().body("message", equalTo("User already exists")).and().statusCode(403);
 //       Assert.assertEquals("неверный статус ответа", 403, createResponse.statusCode());
        loginResponse = userClient.loginUser(UserCreds.credsForm(doubleUser));
        loginResponse.then().body("message", equalTo("email or password are incorrect")).and().statusCode(401);
 //       Assert.assertEquals("Логинится, а не должен", 401, loginResponse.statusCode());
    }

    @After
    public void tearDown(){
        if (loginResponse.statusCode() == 200) {
//            idCourier = loginResponse.body().as(LoginAnswer.class).getId();
            token = loginResponse.body().as(LoginResponse.class).getAccessToken();
            UserClient userClient = new UserClient();
            Response deleteResponse = userClient.deleteUser(token.substring(7));
            System.out.println(token);
            Assert.assertEquals("Пользователь не удален", 202, deleteResponse.statusCode());

        }

    }
}
