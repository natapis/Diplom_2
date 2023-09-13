public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private boolean success;
    private UserResponse user;
    public LoginResponse(){

    }
    public String getAccessToken(){
        return accessToken;
    }
    public UserResponse getUser() {return user;}
}