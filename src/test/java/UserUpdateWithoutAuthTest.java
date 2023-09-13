import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static constant.Api.BASE_URL;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

public class UserUpdateWithoutAuthTest {
//    private String token;
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
 //       token = loginResponse.body().as(LoginResponse.class).getAccessToken();
    }
    @Test
    public void userUpdateEmailWithoutAuth(){
        User userFake = user;
        String newEmail = faker.internet().emailAddress();
        userFake.setEmail(newEmail);
        Response updateUser = userClient.updateUserWithoutAuth(userFake);
        updateUser.then()
                .body("success", equalTo(false))
                .and()
                .statusCode(401)
                .and()
                .body("message", equalTo("You should be authorised"));
        Response loginResponseNewEmail = userClient.loginUser(UserCreds.credsForm(userFake));
        loginResponseNewEmail.then().statusCode(401);
    }

    @Test
    public void userUpdateNameWithoutAuth(){
        User userFake = user;
        String oldName = user.getName();
        String newName = faker.name().username();
        userFake.setName(newName);
        Response updateUser = userClient.updateUserWithoutAuth(userFake);
        updateUser.then()
                .body("success", equalTo(false))
                .and()
                .statusCode(401)
                .and()
                .body("message", equalTo("You should be authorised"));
        Response loginResponseNewName = userClient.loginUser(UserCreds.credsForm(user));
        Assert.assertEquals(oldName, loginResponseNewName.as(LoginResponse.class).getUser().getName());
        loginResponseNewName.then().statusCode(200);
    }

    @Test
    public void userUpdatePasswordWithoutAuth(){
        User userFake = user;
        String newPassword = faker.internet().password();
        userFake.setPassword(newPassword);
        Response updateUser = userClient.updateUserWithoutAuth(userFake);
        updateUser.then()
                .body("success", equalTo(false))
                .and()
                .statusCode(401)
                .and()
                .body("message", equalTo("You should be authorised"));
        Response loginResponseNewPassword = userClient.loginUser(UserCreds.credsForm(userFake));
        loginResponseNewPassword.then().statusCode(401);
    }

    @Test
    public void userUpdateFullDataWithoutAuth(){
        User userFake = user;
        String newPassword = faker.internet().password();
        String newEmail = faker.internet().emailAddress();
        String newName = faker.name().username();
        userFake.setName(newName);
        userFake.setEmail(newEmail);
        userFake.setPassword(newPassword);
        Response updateUser = userClient.updateUserWithoutAuth(userFake);
        updateUser.then()
                .body("success", equalTo(false))
                .and()
                .statusCode(401)
                .and()
                .body("message", equalTo("You should be authorised"));
        Response loginResponseNewData = userClient.loginUser(UserCreds.credsForm(userFake));
        loginResponseNewData.then().statusCode(401);
    }
    public void tearDown() {
        Response loginResponseNew = userClient.loginUser(UserCreds.credsForm(user));
        if (loginResponseNew.statusCode() == 200) {
            String token = loginResponseNew.body().as(LoginResponse.class).getAccessToken();
//            UserClient userClient = new UserClient();
            Response deleteResponse = userClient.deleteUser(token.substring(7));
            System.out.println(token);
            Assert.assertEquals("Пользователь не удален", 202, deleteResponse.statusCode());

        }
    }
}
