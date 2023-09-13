import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static constant.Api.BASE_URL;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

public class UserUpdateTest {
    private String token;
    private UserClient userClient = new UserClient();
    private User user = UserGenerator.requiredFields();
    private Faker faker = new Faker();
    private Response loginResponse;
    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
//        token = null;
        userClient.createUser(user);
        loginResponse = userClient.loginUser(UserCreds.credsForm(user));
        token = loginResponse.body().as(LoginResponse.class).getAccessToken();
    }
    @Test
    public void userUpdateEmailWithAuth(){
        User userFake = user;
        String newEmail = faker.internet().emailAddress();
        userFake.setEmail(newEmail);
        Response updateUser = userClient.updateUserWithAuth(token.substring(7), userFake);
        updateUser.then()
                .body("success", equalTo(true))
                .and()
                .statusCode(200)
                .and()
                .body("user", notNullValue());
        Response loginResponseNewEmail = userClient.loginUser(UserCreds.credsForm(user));
        loginResponseNewEmail.then().statusCode(200);
    }

    @Test
    public void userUpdateNameWithAuth(){
        User userFake = user;
        String newName = faker.name().username();
        userFake.setName(newName);
        Response updateUser = userClient.updateUserWithAuth(token.substring(7), userFake);
        updateUser.then()
                .body("success", equalTo(true))
                .and()
                .statusCode(200)
                .and()
                .body("user", notNullValue());
        Response loginResponseNewName = userClient.loginUser(UserCreds.credsForm(user));
        loginResponseNewName.then().statusCode(200);
    }

    @Test
    public void userUpdatePasswordWithAuth(){
        User userFake = user;
        String newPassword = faker.internet().password();
        userFake.setName(newPassword);
        Response updateUser = userClient.updateUserWithAuth(token.substring(7), userFake);
        updateUser.then()
                .body("success", equalTo(true))
                .and()
                .statusCode(200)
                .and()
                .body("user", notNullValue());
        Response loginResponseNewPassword = userClient.loginUser(UserCreds.credsForm(user));
        loginResponseNewPassword.then().statusCode(200);
    }
    public void tearDown() {
        Response loginResponseNew = userClient.loginUser(UserCreds.credsForm(user));
        if (loginResponseNew.statusCode() == 200) {
            token = loginResponseNew.body().as(LoginResponse.class).getAccessToken();
//            UserClient userClient = new UserClient();
            Response deleteResponse = userClient.deleteUser(token.substring(7));
            System.out.println(token);
            Assert.assertEquals("Пользователь не удален", 202, deleteResponse.statusCode());

        }
    }
}
