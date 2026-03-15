package stellarburgers.tests;

import io.qameta.allure.Step;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgers.client.IngredientClient;
import stellarburgers.client.OrderClient;
import stellarburgers.client.UserClient;
import stellarburgers.model.User;
import stellarburgers.utils.UserGenerator;
import stellarburgers.utils.Constants;

import java.util.List;

import static org.hamcrest.Matchers.*;

public class OrderCreateTest {

    private OrderClient orderClient;
    private UserClient userClient;
    private IngredientClient ingredientClient;
    private String createdUserToken; // для удаления после теста

    @Before
    public void setUp() {
        orderClient = new OrderClient();
        userClient = new UserClient();
        ingredientClient = new IngredientClient();
    }

    @After
    public void tearDown() {
        if (createdUserToken != null) {
            userClient.deleteUser(createdUserToken)
                    .statusCode(202);
        }
    }

    @Step("Регистрация нового пользователя")
    private String registerNewUser() {
        User user = UserGenerator.getRandomUser();
        return userClient.register(user)
                .statusCode(200)
                .extract()
                .path("accessToken");
    }

    @Step("Получение валидных ID ингредиентов (первые два)")
    private List<String> getTwoIngredientIds() {
        List<String> ingredients = ingredientClient.getValidIngredientIds();
        return ingredients.subList(0, 2);
    }

    @Step("Создание заказа с авторизацией, ингредиенты: {ingredients}")
    private void createOrderWithAuth(String token, List<String> ingredients, int expectedStatusCode) {
        orderClient.createOrder(token, ingredients)
                .statusCode(expectedStatusCode);
    }

    @Step("Создание заказа без авторизации, ингредиенты: {ingredients}")
    private void createOrderWithoutAuth(List<String> ingredients, int expectedStatusCode) {
        orderClient.createOrderWithoutAuth(ingredients)
                .statusCode(expectedStatusCode);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и валидными ингредиентами")
    @Description("Авторизованный пользователь создаёт заказ с двумя ингредиентами. Ожидаем код 200, номер заказа.")
    public void testCreateOrderWithAuthAndIngredients() {
        createdUserToken = registerNewUser();
        List<String> ingredients = getTwoIngredientIds();

        orderClient.createOrder(createdUserToken, ingredients)
                .statusCode(200)
                .body("success", is(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Неавторизованный пользователь пытается создать заказ. Ожидаем код 200 (сервер разрешает).")
    public void testCreateOrderWithoutAuth() {
        List<String> ingredients = getTwoIngredientIds();
        createOrderWithoutAuth(ingredients, 200);
        // Пользователь не создавался, токен не сохраняем
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов (пустой список)")
    @Description("Авторизованный пользователь отправляет пустой список ingredients. Ожидаем код 400.")
    public void testCreateOrderWithoutIngredients() {
        createdUserToken = registerNewUser();

        orderClient.createOrder(createdUserToken, List.of())
                .statusCode(400)
                .body("success", is(false))
                .body("message", equalTo(Constants.INGREDIENT_IDS_REQUIRED));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиента")
    @Description("Передаём несуществующие ID ингредиентов. Ожидаем код 500 Internal Server Error.")
    public void testCreateOrderWithInvalidHash() {
        createdUserToken = registerNewUser();

        List<String> invalidIngredients = List.of("invalidHash123", "alsoInvalid456");

        orderClient.createOrder(createdUserToken, invalidIngredients)
                .statusCode(500);
    }

    @Test
    @DisplayName("Создание заказа с одним ингредиентом")
    @Description("Проверяем, что можно создать заказ с одним ингредиентом (например, только булка).")
    public void testCreateOrderWithOneIngredient() {
        createdUserToken = registerNewUser();

        List<String> ingredients = ingredientClient.getValidIngredientIds();
        List<String> oneIngredient = List.of(ingredients.get(0));

        orderClient.createOrder(createdUserToken, oneIngredient)
                .statusCode(200)
                .body("success", is(true))
                .body("order.number", notNullValue());
    }
}