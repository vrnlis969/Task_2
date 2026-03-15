package stellarburgers.client;

import io.restassured.response.ValidatableResponse;
import stellarburgers.model.Order;

import java.util.List;

import static io.restassured.RestAssured.given;

/**
 * Клиент для работы с заказами: создание заказа, получение заказов пользователя.
 */
public class OrderClient extends RestClient {

    private static final String ORDERS_PATH = "/orders";

    /**
     * Создание заказа с авторизацией.
     *
     * @param token       accessToken пользователя
     * @param ingredients список ID ингредиентов
     * @return ответ сервера
     */
    public ValidatableResponse createOrder(String token, List<String> ingredients) {
        Order order = new Order(ingredients);
        return given()
                .spec(getBaseSpec())
                .header("Authorization", token)
                .body(order)
                .when()
                .post(ORDERS_PATH)
                .then();
    }

    /**
     * Создание заказа без авторизации.
     *
     * @param ingredients список ID ингредиентов
     * @return ответ сервера
     */
    public ValidatableResponse createOrderWithoutAuth(List<String> ingredients) {
        Order order = new Order(ingredients);
        return given()
                .spec(getBaseSpec())
                .body(order)
                .when()
                .post(ORDERS_PATH)
                .then();
    }

    /**
     * Получение заказов конкретного пользователя (с авторизацией).
     *
     * @param token accessToken пользователя
     * @return ответ сервера
     */
    public ValidatableResponse getUserOrders(String token) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", token)
                .when()
                .get(ORDERS_PATH)
                .then();
    }

    /**
     * Получение заказов без авторизации (ожидаем ошибку).
     *
     * @return ответ сервера
     */
    public ValidatableResponse getUserOrdersWithoutAuth() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get(ORDERS_PATH)
                .then();
    }
}