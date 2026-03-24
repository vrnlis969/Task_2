package stellarburgers.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgers.model.User;
import stellarburgers.model.UserCredentials;
import stellarburgers.utils.UserGenerator;

import static org.hamcrest.Matchers.*;

public class UserUpdateTest extends BaseTest {

    private static final Faker faker = new Faker();

    private String createdUserToken;
    private User createdUser;

    @After
    public void tearDown() {
        if (createdUserToken != null) {
            userClient.deleteUser(createdUserToken)
                    .statusCode(202);
        }
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
    @Description("Авторизованный пользователь может изменить email.")
    public void testUpdateEmailWithAuth() {
        createdUser = UserGenerator.getRandomUser();
        createdUserToken = registerUser(createdUser);

        getUser(createdUserToken);

        String newEmail = faker.internet().emailAddress();
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
    @Description("Авторизованный пользователь меняет имя.")
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

        String newPassword = faker.internet().password();
        User updatedData = new User();
        updatedData.setPassword(newPassword);

        userClient.updateUser(createdUserToken, updatedData)
                .statusCode(200)
                .body("success", is(true));

        login(createdUser.getEmail(), newPassword, 200);
    }

    @Test
    @DisplayName("Изменение данных без авторизации")
    @Description("Пытаемся обновить данные, не передавая токен.")
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

        String newEmail = faker.internet().emailAddress();
        String newName = faker.name().firstName();

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