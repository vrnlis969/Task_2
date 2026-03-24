package stellarburgers.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgers.client.IngredientClient;
import stellarburgers.client.OrderClient;
import stellarburgers.utils.Constants;

import java.util.List;

import static org.hamcrest.Matchers.*;

public class UserOrdersTest extends BaseTest {

    private OrderClient orderClient;
    private IngredientClient ingredientClient;
    private String createdUserToken;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
        ingredientClient = new IngredientClient();
    }

    @After
    public void tearDown() {
        if (createdUserToken != null) {
            userClient.deleteUser(createdUserToken)
                    .statusCode(202);
        }
    }

    @Step("Создание заказа для пользователя (для наполнения истории)")
    private void createOrderForUser(String token) {
        List<String> ingredients = ingredientClient.getValidIngredientIds().subList(0, 2);
        orderClient.createOrder(token, ingredients).statusCode(200);
    }

    @Test
    @DisplayName("Получение заказов авторизованного пользователя (без заказов)")
    @Description("У нового пользователя ещё нет заказов.")
    public void testGetUserOrdersWithAuthNoOrders() {
        createdUserToken = registerNewUser();

        orderClient.getUserOrders(createdUserToken)
                .statusCode(200)
                .body("success", is(true))
                .body("orders", notNullValue())
                .body("orders.size()", is(0))
                .body("total", notNullValue())
                .body("totalToday", notNullValue());
    }

    @Test
    @DisplayName("Получение заказов авторизованного пользователя (с заказами)")
    @Description("Создаём один заказ, затем запрашиваем список заказов.")
    public void testGetUserOrdersWithAuthWithOrders() {
        createdUserToken = registerNewUser();
        createOrderForUser(createdUserToken);

        orderClient.getUserOrders(createdUserToken)
                .statusCode(200)
                .body("success", is(true))
                .body("orders", notNullValue())
                .body("orders.size()", greaterThan(0))
                .body("orders[0].number", notNullValue())
                .body("orders[0].status", notNullValue())
                .body("orders[0].createdAt", notNullValue());
    }

    @Test
    @DisplayName("Получение заказов без авторизации")
    @Description("Запрос без токена должен вернуть 401 Unauthorized.")
    public void testGetUserOrdersWithoutAuth() {
        orderClient.getUserOrdersWithoutAuth()
                .statusCode(401)
                .body("success", is(false))
                .body("message", equalTo(Constants.UNAUTHORISED_MESSAGE));
    }

    @Test
    @DisplayName("Получение заказов с невалидным токеном")
    @Description("Передаём токен, который не существует или просрочен.")
    public void testGetUserOrdersWithInvalidToken() {
        String invalidToken = "Bearer someInvalidToken";
        orderClient.getUserOrders(invalidToken)
                .statusCode(403)
                .body("success", is(false));
    }
}