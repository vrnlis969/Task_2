package stellarburgers.model;

import java.util.List;

/**
 * Модель заказа.
 * Содержит список идентификаторов ингредиентов.
 */
public class Order {
    // Название поля должно точно соответствовать тому, что ждёт сервер: "ingredients"
    private List<String> ingredients;

    public Order() {
    }

    public Order(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}