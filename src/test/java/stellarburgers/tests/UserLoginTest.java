package stellarburgers.tests;

import io.qameta.allure.Step;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgers.client.UserClient;
import stellarburgers.model.User;
import stellarburgers.model.UserCredentials;
import stellarburgers.utils.UserGenerator;
import stellarburgers.utils.Constants;

import static org.hamcrest.Matchers.*;

public class UserLoginTest {

    private UserClient userClient;
    private String createdUserToken;
    private User createdUser;

    @Before
    public void setUp() {
        userClient = new UserClient();
        createdUser = UserGenerator.getRandomUser();
        createdUserToken = registerUser(createdUser);
    }

    @After
    public void tearDown() {
        if (createdUserToken != null) {
            userClient.deleteUser(createdUserToken)
                    .statusCode(202);
        }
    }

    @Step("Регистрация пользователя: {user}")
    private String registerUser(User user) {
        return userClient.register(user)
                .statusCode(200)
                .extract()
                .path("accessToken");
    }

    @Step("Логин с учётными данными: {credentials}")
    private void login(UserCredentials credentials, int expectedStatusCode) {
        userClient.login(credentials)
                .statusCode(expectedStatusCode);
    }

    @Test
    @DisplayName("Логин под существующим пользователем")
    @Description("Проверяем успешный вход с правильными email и password. Ожидаем код 200 и токены.")
    public void testLoginExistingUser() {
        UserCredentials credentials = new UserCredentials(createdUser.getEmail(), createdUser.getPassword());
        userClient.login(credentials)
                .statusCode(200)
                .body("success", is(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("user.email", equalTo(createdUser.getEmail()))
                .body("user.name", equalTo(createdUser.getName()));
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    @Description("Пароль не совпадает с зарегистрированным. Ожидаем код 401.")
    public void testLoginWithWrongPassword() {
        UserCredentials credentials = new UserCredentials(createdUser.getEmail(), "wrongPassword");
        login(credentials, 401);
    }

    @Test
    @DisplayName("Логин с неверным email")
    @Description("Email не существует. Ожидаем код 401.")
    public void testLoginWithWrongEmail() {
        UserCredentials credentials = new UserCredentials("nonexistent@mail.ru", createdUser.getPassword());
        login(credentials, 401);
    }

    @Test
    @DisplayName("Логин с пустым email")
    @Description("Поле email не передано. Ожидаем код 401.")
    public void testLoginWithEmptyEmail() {
        UserCredentials credentials = new UserCredentials(null, createdUser.getPassword());
        login(credentials, 401);
    }
}