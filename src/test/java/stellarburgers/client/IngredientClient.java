package stellarburgers.client;

import io.restassured.response.ValidatableResponse;

import java.util.List;

import static io.restassured.RestAssured.given;

/**
 * Клиент для получения данных об ингредиентах.
 */
public class IngredientClient extends RestClient {

    private static final String INGREDIENTS_PATH = "/ingredients";

    /**
     * Получить список всех ингредиентов.
     *
     * @return ответ сервера
     */
    public ValidatableResponse getIngredients() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get(INGREDIENTS_PATH)
                .then();
    }

    /**
     * Удобный метод для получения списка валидных идентификаторов ингредиентов.
     * Используется в тестах для создания заказа.
     *
     * @return список строк (ID ингредиентов)
     */
    public List<String> getValidIngredientIds() {
        // Извлекаем из JSON-ответа все поля "data._id" и возвращаем как список строк
        return getIngredients()
                .statusCode(200)                     // убеждаемся, что запрос успешен
                .extract()
                .jsonPath()
                .getList("data._id", String.class);
    }
}