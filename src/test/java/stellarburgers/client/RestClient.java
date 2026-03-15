package stellarburgers.client;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/**
 * Базовый класс для всех клиентов API.
 * Содержит общую настройку спецификации запроса: базовый URL, Content-Type и базовый путь.
 */
public class RestClient {
    // Базовый URL сервиса (без /api)
    private static final String BASE_URL = "https://stellarburgers.education-services.ru/";

    /**
     * Создаёт и возвращает базовую спецификацию запроса.
     * Все клиенты будут использовать эту спецификацию, добавляя при необходимости свои заголовки.
     *
     * @return RequestSpecification с установленными базовыми параметрами
     */
    protected RequestSpecification getBaseSpec() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)        // все запросы и ответы в формате JSON
                .setBaseUri(BASE_URL)                    // устанавливаем базовый URI
                .setBasePath("/api")                      // все эндпоинты начинаются с /api
                .build();
    }
}