public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private boolean succes;
    private UserResponse user;
    public LoginResponse(){

    }
    public String getAccessToken(){
        return accessToken;
    }
}