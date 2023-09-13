import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static constant.Api.BASE_URL;
import static org.hamcrest.core.IsEqual.equalTo;

public class UserUpdateTest {
    private String token;
    private UserClient userClient = new UserClient();
    private User user = UserGenerator.requiredFields();
    Faker faker = new Faker();
    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        token = null;
        userClient.createUser(user);
    }
    @Test
    public void userUpdateEmailWithAuth(){
        Response loginResponse = userClient.loginUser(UserCreds.credsForm(user));
        token = loginResponse.body().as(LoginResponse.class).getAccessToken();
        User userFake = user;
        String newEmail = faker.internet().emailAddress();
        userFake.setEmail(newEmail);
        Response updateUser = userClient.updateUserWithAuth(token.substring(7), userFake);
        updateUser.then()
                .body("success", equalTo(true))
                .and()
                .statusCode(200)
                .and()
                .body("email", equalTo(newEmail));
        Response loginResponseNewEmail = userClient.loginUser(UserCreds.credsForm(user));
        loginResponseNewEmail.then().statusCode(200);
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
