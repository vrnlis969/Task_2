package stellarburgers.model;

/**
 * Модель пользователя для запросов регистрации и обновления данных.
 * Соответствует JSON-объекту, который отправляется на сервер.
 */
public class User {
    private String email;
    private String password;
    private String name;

    // Пустой конструктор необходим для десериализации (например, из JSON в объект)
    public User() {
    }

    // Конструктор для создания пользователя со всеми полями
    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    // Геттеры и сеттеры — стандартные методы доступа к полям
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}