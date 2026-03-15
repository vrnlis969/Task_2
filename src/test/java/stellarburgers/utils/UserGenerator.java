package stellarburgers.utils;

import org.apache.commons.lang3.RandomStringUtils;
import stellarburgers.model.User;

/**
 * Генератор случайных данных для пользователя.
 * Используется для создания уникальных тестовых данных.
 */
public class UserGenerator {

    /**
     * Создаёт пользователя со случайными email, password и name.
     *
     * @return объект User
     */
    public static User getRandomUser() {
        // Генерируем случайную строку из букв длиной 8 для email (до @)
        String email = RandomStringUtils.randomAlphabetic(8).toLowerCase() + "@yandex.ru";
        // Генерируем случайный пароль из букв и цифр длиной 10
        String password = RandomStringUtils.randomAlphanumeric(10);
        // Генерируем случайное имя из букв длиной 8
        String name = RandomStringUtils.randomAlphabetic(8);
        return new User(email, password, name);
    }
}