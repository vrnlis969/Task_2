package stellarburgers.tests;

import io.qameta.allure.Step;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgers.client.UserClient;
import stellarburgers.model.User;
import stellarburgers.model.UserCredentials;
import stellarburgers.utils.UserGenerator;

import static org.hamcrest.Matchers.*;

public class UserUpdateTest {

    private UserClient userClient;
    private String createdUserToken;
    private User createdUser;

    @Before
    public void setUp() {
        userClient = new UserClient();
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

    @Step("Получение данных пользователя (проверка токена)")
    private void getUser(String token) {
        userClient.getUser(token)
                .statusCode(200);
    }

    @Step("Обновление данных пользователя: {updatedData}")
    private void updateUser(String token, User updatedData, int expectedStatusCode) {
        userClient.updateUser(token, updatedData)
                .statusCode(expectedStatusCode);
    }

    @Step("Логин с новыми данными: email={email}, password={password}")
    private void login(String email, String password, int expectedStatusCode) {
        UserCredentials creds = new UserCredentials(email, password);
        userClient.login(creds)
                .statusCode(expectedStatusCode);
    }

    @Test
    @DisplayName("Изменение email с авторизацией")
    @Description("Проверяем, что авторизованный пользователь может изменить email. Ожидаем код 200 и обновлённые данные.")
    public void testUpdateEmailWithAuth() {
        createdUser = UserGenerator.getRandomUser();
        createdUserToken = registerUser(createdUser);

        getUser(createdUserToken);

        String newEmail = "new" + RandomStringUtils.randomAlphabetic(5).toLowerCase() + "@example.com";
        User updatedData = new User();
        updatedData.setEmail(newEmail);

        userClient.updateUser(createdUserToken, updatedData)
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", equalTo(newEmail))
                .body("user.name", equalTo(createdUser.getName()));
    }

    @Test
    @DisplayName("Изменение имени с авторизацией")
    @Description("Авторизованный пользователь меняет имя. Ожидаем успех.")
    public void testUpdateNameWithAuth() {
        createdUser = UserGenerator.getRandomUser();
        createdUserToken = registerUser(createdUser);

        User updatedData = new User();
        updatedData.setName("NewUserName");

        userClient.updateUser(createdUserToken, updatedData)
                .statusCode(200)
                .body("success", is(true))
                .body("user.name", equalTo("NewUserName"))
                .body("user.email", equalTo(createdUser.getEmail()));
    }

    @Test
    @DisplayName("Изменение пароля с авторизацией")
    @Description("Меняем пароль. После смены пароля можно залогиниться с новым паролем.")
    public void testUpdatePasswordWithAuth() {
        createdUser = UserGenerator.getRandomUser();
        createdUserToken = registerUser(createdUser);

        String newPassword = "newPass123";
        User updatedData = new User();
        updatedData.setPassword(newPassword);

        userClient.updateUser(createdUserToken, updatedData)
                .statusCode(200)
                .body("success", is(true));

        // Проверяем логин с новым паролем
        login(createdUser.getEmail(), newPassword, 200);
    }

    @Test
    @DisplayName("Изменение данных без авторизации")
    @Description("Пытаемся обновить данные, не передавая токен. Ожидаем код 401.")
    public void testUpdateWithoutAuth() {
        User user = UserGenerator.getRandomUser();
        User updatedData = new User();
        updatedData.setEmail("any@mail.ru");
        updatedData.setName("AnyName");

        userClient.updateUserWithoutAuth(updatedData)
                .statusCode(401)
                .body("success", is(false));
    }

    @Test
    @DisplayName("Изменение нескольких полей одновременно с авторизацией")
    @Description("Меняем email и name в одном запросе.")
    public void testUpdateMultipleFieldsWithAuth() {
        createdUser = UserGenerator.getRandomUser();
        createdUserToken = registerUser(createdUser);

        String newEmail = "combined" + RandomStringUtils.randomAlphabetic(5).toLowerCase() + "@example.com";
        String newName = "CombinedName" + RandomStringUtils.randomAlphabetic(3);

        User updatedData = new User();
        updatedData.setEmail(newEmail);
        updatedData.setName(newName);

        userClient.updateUser(createdUserToken, updatedData)
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", equalTo(newEmail))
                .body("user.name", equalTo(newName));
    }
}