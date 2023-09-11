import io.restassured.response.Response;

import static constant.Api.*;
import static io.restassured.RestAssured.given;

public class UserClient {
    public UserClient(){

    }
    public Response createUser(User user){
        return given()
        .header("Content-type","application/json")
                .and()
                .body(user)
                .when()
                .post(CREATE_USER_API);
    }

    public Response loginUser(UserCreds userCreds){
        return given()
                .header("Content-type","application/json")
                .and()
                .body(userCreds)
                .when()
                .post(LOGIN_USER_API);
    }
    public Response deleteUser(User user){
        return given()
                .delete(UPDATE_DELETE_USER_API,user);
    }
    public Response updateUser(){
        return given()
                .patch(UPDATE_DELETE_USER_API);
    }


}
