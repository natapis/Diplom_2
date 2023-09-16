package constant;

public class Api {
    public static final String BASE_URL = "https://stellarburgers.nomoreparties.site/";
    public static final String CREATE_USER_API = "/api/auth/register";
    public static final String LOGIN_USER_API = "/api/auth/login";
    public static final String CREATE_ORDER_API = "/api/orders";
    public static final String GET_ORDER_USER_API = "/api/orders/{:token}";
    public static final String UPDATE_DELETE_USER_API = "/api/auth/user";
    public static final String GET_INGREDIENTS = "/api/ingredients";
}
