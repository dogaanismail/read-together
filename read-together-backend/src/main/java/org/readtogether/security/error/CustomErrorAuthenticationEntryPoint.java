package org.readtogether.security.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.readtogether.common.exception.CustomError;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DateFormat;

@Component
public class CustomErrorAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    @Override
    public void commence(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse,
                         AuthenticationException authenticationException) throws IOException {

        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());

        // Determine specific error message based on the exception
        String errorMessage = determineErrorMessage(authenticationException);

        org.readtogether.common.exception.CustomError customError = CustomError.builder()
                .header(CustomError.Header.AUTH_ERROR.getName())
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .message(errorMessage)
                .isSuccess(false)
                .build();

        String responseBody = OBJECT_MAPPER
                .writer(DateFormat.getDateInstance())
                .writeValueAsString(customError);

        httpServletResponse.getOutputStream()
                .write(responseBody.getBytes());

    }

    private String determineErrorMessage(AuthenticationException authenticationException) {
        String message = authenticationException.getMessage();
        
        if (message != null) {
            if (message.contains("expired") || message.contains("Expired")) {
                return "Access token has expired";
            }
            if (message.contains("invalid") || message.contains("Invalid") || 
                message.contains("malformed") || message.contains("Malformed")) {
                return "Invalid or malformed access token";
            }
            if (message.contains("signature") || message.contains("Signature")) {
                return "Invalid token signature";
            }
        }
        
        return "Authentication required - please provide a valid access token";
    }

}
