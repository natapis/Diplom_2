public class User {
    private String email;
    private String password;
    private String name;
    public User(){

    }
    public User withEmail(String email){
        this.email = email;
        return this;
    }
    public User withPassword(String password){
        this.password = password;
        return this;
    }
    public User withName(String name){
        this.name = name;
        return this;
    }
    public String getName(){
        return name;
    }
    public String getPassword(){
        return password;
    }
    public String getEmail(){
        return email;
    }
}
