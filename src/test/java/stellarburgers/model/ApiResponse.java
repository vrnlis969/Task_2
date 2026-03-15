package stellarburgers.model;

/**
 * Общая модель ответа от API.
 * Содержит поля, которые присутствуют во многих ответах: success, message и т.д.
 * Можно расширять под конкретные нужды, но в тестах мы чаще проверяем конкретные поля через Hamcrest.
 * Этот класс можно использовать для более удобной десериализации, если нужно.
 */
public class ApiResponse {
    private boolean success;
    private String message;
    private String accessToken;
    private String refreshToken;
    private User user;

    // геттеры и сеттеры
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}