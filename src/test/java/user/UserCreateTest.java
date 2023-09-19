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

public class UserCreateTest {
    private Response loginResponse;
    private String token;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        loginResponse = null;
        token = null;
    }

    @DisplayName("Создание пользователя с обязательными полями")
    @Test
    public void createUserFullDateTest() {
        UserClient userClient = new UserClient();
        User user = UserGenerator.requiredFields();
        Response createResponse = userClient.createUser(user);
        Assert.assertEquals("неверный статус ответа", 200, createResponse.statusCode());
        loginResponse = userClient.loginUser(UserCreds.credsForm(user));
        Assert.assertEquals("Не логинится", 200, loginResponse.statusCode());
    }

    @DisplayName("Создание пользователя с дублирующей почтой")
    @Test
    public void createUserDoubleEmailTest() {
        UserClient userClient = new UserClient();
        User user = UserGenerator.requiredFields();
        userClient.createUser(user);
        User doubleUser = UserGenerator.doubleUserEmail(user.getEmail());
        Response createResponse = userClient.createUser(doubleUser);
        loginResponse = userClient.loginUser(UserCreds.credsForm(doubleUser));
        createResponse.then().body("message", equalTo("User already exists")).and().statusCode(403);
        loginResponse.then().body("message", equalTo("email or password are incorrect")).and().statusCode(401);
    }

    @DisplayName("Создание пользователя с дублирующим именем")
    @Test
    public void createUserDoubleNameTest() {
        UserClient userClient = new UserClient();
        User user = UserGenerator.requiredFields();
        userClient.createUser(user);
        User doubleUser = UserGenerator.doubleUserName(user.getName());
        Response createResponse = userClient.createUser(doubleUser);
        loginResponse = userClient.loginUser(UserCreds.credsForm(doubleUser));
        Assert.assertEquals("неверный статус ответа", 403, createResponse.statusCode());
        loginResponse.then().body("message", equalTo("email or password are incorrect")).and().statusCode(401);
    }

    @DisplayName("Создание пользователя без указания имени")
    @Test
    public void createUserWithoutNameTest() {
        UserClient userClient = new UserClient();
        User user = UserGenerator.withoutName();
        Response createResponse = userClient.createUser(user);
        createResponse.then().body("message", equalTo("Email, password and name are required fields")).and().statusCode(403);
        loginResponse = userClient.loginUser(UserCreds.credsForm(user));
        loginResponse.then().body("message", equalTo("email or password are incorrect")).and().statusCode(401);
    }

    @DisplayName("Создание пользователя без указания email")
    @Test
    public void createUserWithoutEmailTest() {
        UserClient userClient = new UserClient();
        User user = UserGenerator.withoutEmail();
        Response createResponse = userClient.createUser(user);
        createResponse.then().body("message", equalTo("Email, password and name are required fields")).and().statusCode(403);
        loginResponse = userClient.loginUser(UserCreds.credsForm(user));
        loginResponse.then().body("message", equalTo("email or password are incorrect")).and().statusCode(401);
    }

    @DisplayName("Создание пользователя без указания пароля")
    @Test
    public void createUserWithoutPasswordTest() {
        UserClient userClient = new UserClient();
        User user = UserGenerator.withoutPassword();
        Response createResponse = userClient.createUser(user);
        createResponse.then().body("message", equalTo("Email, password and name are required fields")).and().statusCode(403);
        loginResponse = userClient.loginUser(UserCreds.credsForm(user));
        loginResponse.then().body("message", equalTo("email or password are incorrect")).and().statusCode(401);
    }

    @After
    public void tearDown() {
        if (loginResponse.statusCode() == 200) {
            token = loginResponse.body().as(LoginResponse.class).getAccessToken();
            UserClient userClient = new UserClient();
            Response deleteResponse = userClient.deleteUser(token.substring(7));
            Assert.assertEquals("Пользователь не удален", 202, deleteResponse.statusCode());

        }

    }
}
