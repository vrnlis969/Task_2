package stellarburgers.model;

import lombok.Data;

@Data
public class ApiResponse {
    private boolean success;
    private String message;
    private String accessToken;
    private String refreshToken;
    private User user;
}