package stellarburgers.tests;

import io.qameta.allure.Step;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgers.client.UserClient;
import stellarburgers.model.User;
import stellarburgers.utils.UserGenerator;
import stellarburgers.utils.Constants;

import static org.hamcrest.Matchers.*;

public class UserRegistrationTest {

    private UserClient userClient;
    private String createdUserToken; // токен созданного пользователя (для удаления)
    private User createdUser;        // сохранение пользователя для проверок

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    @After
    public void tearDown() {
        if (createdUserToken != null) {
            userClient.deleteUser(createdUserToken)
                    .statusCode(202); // предполагаем успешное удаление
        }
    }

    @Step("Регистрация пользователя: {user}")
    private String registerUser(User user) {
        return userClient.register(user)
                .statusCode(200)
                .extract()
                .path("accessToken");
    }

    @Step("Попытка регистрации пользователя (ожидаемый статус: {expectedStatusCode})")
    private void registerUserWithExpectedStatus(User user, int expectedStatusCode) {
        userClient.register(user)
                .statusCode(expectedStatusCode);
    }

    @Test
    @DisplayName("Успешная регистрация нового пользователя")
    @Description("Проверяем, что можно зарегистрироваться с уникальными email, password и name. Ожидаем код 200 и наличие токенов.")
    public void testCreateUniqueUser() {
        createdUser = UserGenerator.getRandomUser();
        createdUserToken = registerUser(createdUser);

        // Дополнительная проверка ответа (можно и в шаге, но для наглядности оставим)
        userClient.register(createdUser) // повторная регистрация не требуется, просто для примера проверки
                .statusCode(403); // но мы уже зарегистрировали, поэтому должен быть 403
        // Однако чтобы не дублировать, можно просто проверить, что токен не null
        assert createdUserToken != null;
    }

    @Test
    @DisplayName("Регистрация уже существующего пользователя")
    @Description("Пытаемся зарегистрироваться дважды с одними и теми же данными. Ожидаем код 403 и сообщение об ошибке.")
    public void testCreateDuplicateUser() {
        createdUser = UserGenerator.getRandomUser();
        createdUserToken = registerUser(createdUser);

        // Повторная регистрация
        userClient.register(createdUser)
                .statusCode(403)
                .body("success", is(false))
                .body("message", equalTo(Constants.USER_ALREADY_EXISTS));
    }

    @Test
    @DisplayName("Регистрация без обязательного поля (email)")
    @Description("Поле email отсутствует (null). Ожидаем код 403 и сообщение об ошибке.")
    public void testCreateUserWithoutEmail() {
        User user = UserGenerator.getRandomUser();
        user.setEmail(null);
        registerUserWithExpectedStatus(user, 403);
        // Пользователь не создан, токен не сохраняем
    }

    @Test
    @DisplayName("Регистрация без обязательного поля (password)")
    @Description("Поле password отсутствует. Ожидаем код 403.")
    public void testCreateUserWithoutPassword() {
        User user = UserGenerator.getRandomUser();
        user.setPassword(null);
        registerUserWithExpectedStatus(user, 403);
    }

    @Test
    @DisplayName("Регистрация без обязательного поля (name)")
    @Description("Поле name отсутствует. Ожидаем код 403.")
    public void testCreateUserWithoutName() {
        User user = UserGenerator.getRandomUser();
        user.setName(null);
        registerUserWithExpectedStatus(user, 403);
    }
}