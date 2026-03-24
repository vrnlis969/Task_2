package stellarburgers.client;

import io.restassured.response.ValidatableResponse;
import stellarburgers.model.User;
import stellarburgers.model.UserCredentials;

import static io.restassured.RestAssured.given;

/**
 * Клиент для работы с пользователями: регистрация, логин, обновление данных.
 */
public class UserClient extends RestClient {

    private static final String REGISTER_PATH = "/auth/register";
    private static final String LOGIN_PATH = "/auth/login";
    private static final String USER_PATH = "/auth/user";

    /**
     * Регистрация нового пользователя.
     *
     * @param user объект пользователя с email, password, name
     * @return ответ сервера, который можно проверять дальше
     */
    public ValidatableResponse register(User user) {
        return given()
                .spec(getBaseSpec())       // берём базовую спецификацию (URL, Content-Type)
                .body(user)                 // тело запроса — объект user автоматически сериализуется в JSON
                .when()
                .post(REGISTER_PATH)        // выполняем POST-запрос
                .then();                     // возвращаем объект для дальнейших проверок (валидации)
    }

    /**
     * Авторизация пользователя.
     *
     * @param credentials объект с email и password
     * @return ответ сервера
     */
    public ValidatableResponse login(UserCredentials credentials) {
        return given()
                .spec(getBaseSpec())
                .body(credentials)
                .when()
                .post(LOGIN_PATH)
                .then();
    }

    /**
     * Обновление данных пользователя (с авторизацией).
     *
     * @param token   accessToken, полученный при логине/регистрации (включая префикс "Bearer ")
     * @param user    объект с теми полями, которые нужно обновить (можно частично заполненный)
     * @return ответ сервера
     */
    public ValidatableResponse updateUser(String token, User user) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", token)   // добавляем заголовок с токеном
                .body(user)
                .when()
                .patch(USER_PATH)                  // согласно документации, обновление через PATCH
                .then();
    }

    /**
     * Обновление данных пользователя без авторизации (ожидаем ошибку).
     *
     * @param user объект с данными для обновления
     * @return ответ сервера
     */
    public ValidatableResponse updateUserWithoutAuth(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .patch(USER_PATH)
                .then();
    }
    public ValidatableResponse getUser(String token) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", token)
                .when()
                .get(USER_PATH)
                .then();
    }
    //Метод для удаления пользователя
    public ValidatableResponse deleteUser(String token) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", token)
                .when()
                .delete(USER_PATH)
                .then();
    }
}