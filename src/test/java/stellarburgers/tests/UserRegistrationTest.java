package stellarburgers.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgers.model.User;
import stellarburgers.utils.Constants;
import stellarburgers.utils.UserGenerator;

import static org.hamcrest.Matchers.*;

public class UserRegistrationTest extends BaseTest {

    private String createdUserToken;
    private User createdUser;

    @After
    public void tearDown() {
        if (createdUserToken != null) {
            userClient.deleteUser(createdUserToken)
                    .statusCode(202);
        }
    }

    @Step("Попытка регистрации пользователя (ожидаемый статус: {expectedStatusCode})")
    private void registerUserWithExpectedStatus(User user, int expectedStatusCode) {
        userClient.register(user)
                .statusCode(expectedStatusCode);
    }

    @Test
    @DisplayName("Успешная регистрация нового пользователя")
    @Description("Проверяем, что можно зарегистрироваться с уникальными email, password и name.")
    public void testCreateUniqueUser() {
        createdUser = UserGenerator.getRandomUser();
        createdUserToken = registerUser(createdUser);
        // Проверка, что токен получен, уже выполнена в методе registerUser()
    }

    @Test
    @DisplayName("Регистрация уже существующего пользователя")
    @Description("Пытаемся зарегистрироваться дважды с одними и теми же данными.")
    public void testCreateDuplicateUser() {
        createdUser = UserGenerator.getRandomUser();
        createdUserToken = registerUser(createdUser);

        userClient.register(createdUser)
                .statusCode(403)
                .body("success", is(false))
                .body("message", equalTo(Constants.USER_ALREADY_EXISTS));
    }

    @Test
    @DisplayName("Регистрация без обязательного поля (email)")
    @Description("Поле email отсутствует (null).")
    public void testCreateUserWithoutEmail() {
        User user = UserGenerator.getRandomUser();
        user.setEmail(null);
        registerUserWithExpectedStatus(user, 403);
    }

    @Test
    @DisplayName("Регистрация без обязательного поля (password)")
    @Description("Поле password отсутствует.")
    public void testCreateUserWithoutPassword() {
        User user = UserGenerator.getRandomUser();
        user.setPassword(null);
        registerUserWithExpectedStatus(user, 403);
    }

    @Test
    @DisplayName("Регистрация без обязательного поля (name)")
    @Description("Поле name отсутствует.")
    public void testCreateUserWithoutName() {
        User user = UserGenerator.getRandomUser();
        user.setName(null);
        registerUserWithExpectedStatus(user, 403);
    }
}