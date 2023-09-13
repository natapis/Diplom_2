import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static constant.Api.BASE_URL;
import static org.hamcrest.core.IsEqual.equalTo;

public class UserLoginTest {
//    private Response loginResponse;
    private String token;
    private UserClient userClient = new UserClient();
    private User user = UserGenerator.requiredFields();
    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
 //       loginResponse = null;
        token = null;
        userClient.createUser(user);
    }

    @Test
    public void loginExistUserTest() {

//        Response createResponse = userClient.createUser(user);
//        userClient.createUser(user);
//        Assert.assertEquals("неверный статус ответа", 200, createResponse.statusCode());
        Response loginResponse = userClient.loginUser(UserCreds.credsForm(user));
        loginResponse.then().body("success", equalTo(true)).and().statusCode(200);
//        Assert.assertEquals("Не логинится", 200, loginResponse.statusCode());
    }

    @Test
    public void loginUserWrongLoginTest() {
        UserClient userClient = new UserClient();
        User user = UserGenerator.requiredFields();
//        Response createResponse = userClient.createUser(user);
        userClient.createUser(user);
//        Assert.assertEquals("неверный статус ответа", 200, createResponse.statusCode());
        loginResponse = userClient.loginUser(UserCreds.credsForm(user));
        loginResponse.then().body("success", equalTo(true)).and().statusCode(200);
//        Assert.assertEquals("Не логинится", 200, loginResponse.statusCode());
    }

    public void tearDown() {
        Response loginResponseOld = userClient.loginUser(UserCreds.credsForm(user));
        if (loginResponseOld.statusCode() == 200) {
            token = loginResponseOld.body().as(LoginResponse.class).getAccessToken();
//            UserClient userClient = new UserClient();
            Response deleteResponse = userClient.deleteUser(token.substring(7));
            System.out.println(token);
            Assert.assertEquals("Пользователь не удален", 202, deleteResponse.statusCode());

        }
    }
}
