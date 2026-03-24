package stellarburgers.utils;

import net.datafaker.Faker;
import stellarburgers.model.User;

public class UserGenerator {

    private static final Faker faker = new Faker();

    public static User getRandomUser() {
        String email = faker.internet().emailAddress();
        String password = faker.internet().password(6, 12);
        String name = faker.name().firstName();
        return new User(email, password, name);
    }
}