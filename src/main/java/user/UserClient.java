package user;

import io.restassured.response.Response;
import user.User;

import static constant.Api.*;
import static io.restassured.RestAssured.given;

public class UserClient {
    public UserClient() {

    }

    public Response createUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(CREATE_USER_API);
    }

    public Response loginUser(UserCreds userCreds) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(userCreds)
                .when()
                .post(LOGIN_USER_API);
    }

    public Response deleteUser(String token) {
        return given()
                .auth().oauth2(token)
                .and()
                .delete(UPDATE_DELETE_USER_API);
    }

    public Response updateUserWithAuth(String token, User user) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .auth().oauth2(token)
                .and()
                .body(user)
                .when()
                .patch(UPDATE_DELETE_USER_API);
    }

    public Response updateUserWithoutAuth(User user) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .patch(UPDATE_DELETE_USER_API);
    }

    public Response getInfoOrderWithAuth(String token) {
        return given()
                .auth()
                .oauth2(token.substring(7))
                .and()
                .get(GET_ORDER_USER_API, token.substring(7));
    }

    public Response getInfoOrderWithoutAuth() {
        return given()
                .get(GET_ORDER_USER_API_WITHOUT_TOKEN);
    }


}
