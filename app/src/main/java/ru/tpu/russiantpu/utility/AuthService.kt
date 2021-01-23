package ru.tpu.russiantpu.utility

import ru.tpu.russiantpu.dto.TokensDTO
import ru.tpu.russiantpu.utility.notifications.FirebaseNotificationService
import ru.tpu.russiantpu.utility.requests.RequestService

// Класс выполняет операции, совершаемые при логине
// и выходе из учетной записи
object AuthService {

    // Метод входа в учетную запись - подписываем на все группы уведомлений и сохраняем токен
    fun login(tokens: TokensDTO, requestService: RequestService,
              sharedPreferencesService: SharedPreferencesService) {
        // Подпись на все группы уведомлений
        FirebaseNotificationService.subscribeEverything(tokens, requestService)

        // Сохранение токена
        sharedPreferencesService.setCredentials(tokens.token, tokens.refreshToken, tokens.user)
    }

    // Выход из учетной записи - отписываемся от всех групп уведомлений и удаляем данные юзера из памяти
    fun logout(requestService: RequestService, sharedPreferencesService: SharedPreferencesService) {
        //выходим из учетной записи
        FirebaseNotificationService.unsubscribeToAllNotifications() //отписка от группы news_all
        FirebaseNotificationService.unsubscribeFromNotifications(sharedPreferencesService.languageName) //отписываемся от рассылки уведомлений по языку
        FirebaseNotificationService.unsubscribeUserFromNotifications(requestService, sharedPreferencesService.email, sharedPreferencesService.languageId) //отписываеся от уведомлений для конкретного юзера

        sharedPreferencesService.clearCredentials() //удаляем из памяти инфу о юзере

    }
}