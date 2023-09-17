package user;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import user.*;

import static constant.Api.BASE_URL;
import static org.hamcrest.core.IsEqual.equalTo;

public class UserUpdateExistEmailTest {
    private String token;
    private UserClient userClientOne = new UserClient();
    private UserClient userClientTwo = new UserClient();
    private User userOne = UserGenerator.requiredFields();
    private User userTwo = UserGenerator.requiredFields();

    private Response loginResponse;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        userClientOne.createUser(userOne);
        userClientTwo.createUser(userTwo);
        loginResponse = userClientOne.loginUser(UserCreds.credsForm(userOne));
        token = loginResponse.body().as(LoginResponse.class).getAccessToken();
    }

    @DisplayName("Обновление почты на уже занятую с авторизацией")
    @Test
    public void userUpdateEmailWithAuth() {
        User userFake = userOne;
        String newEmail = userTwo.getEmail();
        userFake.setEmail(newEmail);
        Response updateUser = userClientOne.updateUserWithAuth(token.substring(7), userFake);
        updateUser.then()
                .body("success", equalTo(false))
                .and()
                .statusCode(403)
                .and()
                .body("message", equalTo("User with such email already exists"));
        Response loginResponseNewEmail = userClientOne.loginUser(UserCreds.credsForm(userFake));
        loginResponseNewEmail.then().statusCode(401)
                .and()
                .body("message", equalTo("email or password are incorrect"))
                .and()
                .body("success", equalTo(false));
    }

    @After
    public void tearDown() {
        Response loginResponseNew = userClientOne.loginUser(UserCreds.credsForm(userOne));
        Response loginResponseUserTwo = userClientTwo.loginUser(UserCreds.credsForm(userTwo));
        if (loginResponseNew.statusCode() == 200) {
            String tokenOne = loginResponseNew.body().as(LoginResponse.class).getAccessToken();
            Response deleteResponse = userClientOne.deleteUser(tokenOne.substring(7));
            System.out.println(tokenOne);
            Assert.assertEquals("Пользователь не удален", 202, deleteResponse.statusCode());
        }
        if (loginResponseUserTwo.statusCode() == 200) {
            String tokenTwo = loginResponseUserTwo.body().as(LoginResponse.class).getAccessToken();
            Response deleteResponse = userClientTwo.deleteUser(tokenTwo.substring(7));
            Assert.assertEquals("Пользователь не удален", 202, deleteResponse.statusCode());
        }
    }

}
