import com.github.javafaker.Faker;

public class UserGenerator {
    public static User requiredFields() {
        Faker faker = new Faker();
        return new User()
                .withEmail(faker.internet().emailAddress())
                .withName(faker.name().username())
                .withPassword(faker.internet().password());
    }

    public static User withoutEmail() {
        Faker faker = new Faker();
        return new User()
                .withName(faker.name().username())
                .withPassword(faker.internet().password());
    }

    public static User withoutName() {
        Faker faker = new Faker();
        return new User()
                .withEmail(faker.internet().emailAddress())
                .withPassword(faker.internet().password());
    }

    public static User withoutPassword() {
        Faker faker = new Faker();
        return new User()
                .withEmail(faker.internet().emailAddress())
                .withName(faker.name().username());
    }

    public static User doubleUserEmail(String email) {
        Faker faker = new Faker();
        return new User()
                .withEmail(email)
                .withName(faker.name().username())
                .withPassword(faker.internet().password());
    }

    public static User doubleUserName(String name) {
        Faker faker = new Faker();
        return new User()
                .withEmail(faker.internet().emailAddress())
                .withName(name)
                .withPassword(faker.internet().password());
    }

}
