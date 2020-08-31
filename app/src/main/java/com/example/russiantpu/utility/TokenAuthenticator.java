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
    private final SharedPreferencesService sharedPreferencesService; //репозиторий токенов
    private final StartActivityService startActivityService; //сервис перехода между активити (нужно для перехода на логин при отсутствии рефреш)

    public TokenAuthenticator(SharedPreferencesService sharedPreferencesService, StartActivityService startActivityService) {
        this.sharedPreferencesService = sharedPreferencesService;
        this.startActivityService = startActivityService;
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
            if (!token.equals(newToken)) { //если токен обновлен в другом потоке
                return newRequestWithToken(response.request(), newToken);
            }

            String refreshToken = sharedPreferencesService.getRefreshToken();
            if (refreshToken.isEmpty()) { //если юзер не выбрал "запомнить меня"
                startActivityService.startAuthActivityTokenExpired(); //возвращаемся на активити логина
                return null;
            }

            //обновляем токен с помощью рефреш токена
            RequestService requestService = new RequestService();
            String language = sharedPreferencesService.getLanguage();
            String jsonBody = requestService.doPutRequestSync("token", refreshToken, language);
            if (jsonBody == null) { //если запрос не успешен
                startActivityService.startAuthActivityTokenExpired(); //возвращаемся на активити логина
                return null;
            }

            //сохраняем обновленные токены
            GsonService gsonService = new GsonService();
            TokensDTO dto = gsonService.fromJsonToObject(jsonBody, TokensDTO.class);
            sharedPreferencesService.setTokens(dto.getToken(), dto.getRefreshToken());
            return newRequestWithToken(response.request(), dto.getToken());
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
