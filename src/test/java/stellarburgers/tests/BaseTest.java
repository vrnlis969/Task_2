package stellarburgers.tests;

import io.qameta.allure.Step;
import stellarburgers.client.UserClient;
import stellarburgers.model.User;
import stellarburgers.utils.UserGenerator;

public class BaseTest {
    protected UserClient userClient = new UserClient();

    @Step("Регистрация нового пользователя")
    protected String registerNewUser() {
        User user = UserGenerator.getRandomUser();
        return userClient.register(user)
                .statusCode(200)
                .extract()
                .path("accessToken");
    }

    @Step("Регистрация пользователя: {user}")
    protected String registerUser(User user) {
        return userClient.register(user)
                .statusCode(200)
                .extract()
                .path("accessToken");
    }
}