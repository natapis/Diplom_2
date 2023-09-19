package user;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static constant.Api.BASE_URL;
import static org.hamcrest.core.IsEqual.equalTo;

public class UserLoginTest {
    private String token;
    private UserClient userClient = new UserClient();
    private User user = UserGenerator.requiredFields();

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        token = null;
        userClient.createUser(user);
    }

    @DisplayName("Авторизация под существующим пользователем")
    @Test
    public void loginExistUserTest() {
        Response loginResponse = userClient.loginUser(UserCreds.credsForm(user));
        loginResponse.then().body("success", equalTo(true)).and().statusCode(200);
    }

    @DisplayName("Авторизация с неправильной email")
    @Test
    public void loginUserWrongEmailTest() {
        String wrongEmail = user.getEmail() + "t";
        String password = user.getPassword();
        UserCreds userCredsNew = new UserCreds(wrongEmail, password);
        Response loginResponse = userClient.loginUser(userCredsNew);
        loginResponse.then().body("message", equalTo("email or password are incorrect")).and().statusCode(401);
    }

    @DisplayName("Авторизация с неправильным паролем")
    @Test
    public void loginUserWrongPasswordTest() {
        String email = user.getEmail();
        String wrongPassword = user.getPassword() + "t";
        UserCreds userCredsNew = new UserCreds(email, wrongPassword);
        Response loginResponse = userClient.loginUser(userCredsNew);
        loginResponse.then().body("message", equalTo("email or password are incorrect")).and().statusCode(401);
    }

    @After
    public void tearDown() {
        Response loginResponseOld = userClient.loginUser(UserCreds.credsForm(user));
        if (loginResponseOld.statusCode() == 200) {
            token = loginResponseOld.body().as(LoginResponse.class).getAccessToken();
            Response deleteResponse = userClient.deleteUser(token.substring(7));
            Assert.assertEquals("Пользователь не удален", 202, deleteResponse.statusCode());
        }
    }
}
