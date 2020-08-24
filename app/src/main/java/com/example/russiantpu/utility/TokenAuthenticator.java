package com.example.russiantpu.utility;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.russiantpu.dto.TokensDTO;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

//класс обновляет токен при 401 unauthorized
public class TokenAuthenticator implements Authenticator {
    private final SharedPreferencesService sharedPreferencesService;

    public TokenAuthenticator(SharedPreferencesService sharedPreferencesService) {
        this.sharedPreferencesService = sharedPreferencesService;
    }

    @Nullable
    @Override
    public Request authenticate(Route route, @NonNull Response response) {
        final String token = sharedPreferencesService.getToken();
        if (!isRequestWithToken(response) || token == null) {
            return null;
        }
        synchronized (this) {
            final String newToken = sharedPreferencesService.getToken();
            //токен обновлен в другом потоке
            if (!token.equals(newToken)) {
                return newRequestWithToken(response.request(), newToken);
            }

            //обновление токена с помощью рефреш токена
            RequestService requestService = new RequestService();
            String refreshToken = sharedPreferencesService.getRefreshToken();
            String language = sharedPreferencesService.getLanguage();
            String jsonBody = requestService.doPutRequestSync("token", refreshToken, language);
            if (jsonBody != null) { //если запрос успешен
                //сохраняем обновленные токены
                GsonService gsonService = new GsonService();
                TokensDTO dto = gsonService.fromJsonToObject(jsonBody, TokensDTO.class);
                sharedPreferencesService.setTokens(dto.getToken(), dto.getRefreshToken());
                return newRequestWithToken(response.request(), dto.getToken());
            }
            else { //если запрос не успешен (jsonBody == null)
                return null;
            }
        }
    }

    private boolean isRequestWithToken(@NonNull Response response) {
        String header = response.request().header("Authorization");
        return header != null && header.startsWith("Bearer");
    }

    @NonNull
    private Request newRequestWithToken(@NonNull Request request, @NonNull String token) {
        return request.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();
    }
}
