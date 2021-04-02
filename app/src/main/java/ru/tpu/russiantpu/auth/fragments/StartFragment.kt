package ru.tpu.russiantpu.auth.fragments

import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.Fragment
import ru.tpu.russiantpu.R
import ru.tpu.russiantpu.main.activities.CalendarActivity
import ru.tpu.russiantpu.main.activities.MainActivity
import ru.tpu.russiantpu.main.activities.ProfileActivity
import ru.tpu.russiantpu.utility.SharedPreferencesService
import ru.tpu.russiantpu.utility.callbacks.GenericCallback
import ru.tpu.russiantpu.utility.notifications.NotificationResolver
import ru.tpu.russiantpu.utility.requests.RequestService
import java.util.*


class StartFragment : Fragment(), View.OnClickListener {

    companion object {
        private const val ANIMATION_KEY = "ANIMATION"
        const val APP_LINK_KEY = "APP_LINK"
    }

    // implement TextureView.SurfaceTextureListener to use TextureView
    private val fragmentTag = R.string.prev_auth_frag_tag.toString()

    private lateinit var loginButton: Button
    private lateinit var gotoRegisterButton: Button
    //private lateinit var videoView: ScalableVideoView
    private lateinit var splashView: View
    private lateinit var startUi: View

    private var requestService: RequestService? = null

    private var isAnimationFinished = true
    private var isTokenValid: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isAnimationFinished = savedInstanceState?.getBoolean(ANIMATION_KEY) ?: false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layoutInflater = inflater.inflate(R.layout.fragment_start, container, false)

/*        // Включаем видео на заднем плане из ресурсов
        videoView = layoutInflater.findViewById(R.id.video_view)

        try {
            videoView.setRawData(R.raw.tpu480)
            videoView.prepare {
                videoView.isLooping = true
                videoView.start()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }*/

        startUi = layoutInflater.findViewById(R.id.start_views)
        splashView = layoutInflater.findViewById(R.id.splash_layout) as View
        val progressBar = splashView.findViewById(R.id.progress_bar) as ContentLoadingProgressBar

        // Показываем анимацию и делаем запрос, только если анимация еще не проигралась
        if (!isAnimationFinished) {
            // Запуск анимации
            val imageView: ImageView = splashView.findViewById(R.id.splash_screen_image)
            val drawableAnimation = ContextCompat.getDrawable(requireContext(), R.drawable.logo_anim)
                    as AnimatedVectorDrawable?
            imageView.setImageDrawable(drawableAnimation)
            drawableAnimation?.start()

            // Переходим в основное приложение после отображения анимации
            Handler(Looper.getMainLooper()).postDelayed({
                isAnimationFinished = true
                Log.d("ANIMATION_STATUS", "Handler(): isTokenValid = $isTokenValid; " +
                        "isAnimationFinished = $isAnimationFinished")
                when (isTokenValid) { // Проверяем валидность токена
                    true -> loggedInAction() // В главное приложение
                    false -> notLoggedInAction() // Окно с кнопками на логин и регистрацию
                    null -> progressBar.show() // Запрос еще не вернул результат, ждём
                }
            }, 2000)

            val sharedPreferencesService = SharedPreferencesService(requireActivity())
            requestService = RequestService()
            val languageShortName = if (sharedPreferencesService.languageName.isEmpty()) {
                Locale.getDefault().language
            } else {
                sharedPreferencesService.languageName
            }
            //получение JWT токена
            val token: String = sharedPreferencesService.token
            val email: String = sharedPreferencesService.email

            //если запрос успешен (код 200), вызовется коллбэк с переходом в главную активити
            //(запрос успешен, если токен валиден)
            val callback: GenericCallback<String> = object : GenericCallback<String> {
                override fun onResponse(value: String) {
                    isTokenValid = true
                    // Если результат проверки токена возвращается после проигрывания анимации
                    // Иначе НЕ переходим в основное приложение
                    // (переход должен быть проигран только после анимации)
                    if (isAnimationFinished) {
                        loggedInAction()
                    }
                    Log.d("ANIMATION_STATUS", "onResponse(): isTokenValid = $isTokenValid; " +
                            "isAnimationFinished = $isAnimationFinished")
                }

                override fun onError(message: String) {
                    isTokenValid = false
                    if (isAnimationFinished) {
                        notLoggedInAction()
                    }
                    Log.d("ANIMATION_STATUS", "onError(): isTokenValid = $isTokenValid; " +
                            "isAnimationFinished = $isAnimationFinished")
                }

                override fun onFailure(message: String) {
                    isTokenValid = false
                    if (isAnimationFinished) {
                        notLoggedInAction()
                    }
                    Log.d("ANIMATION_STATUS", "onFailure(): isTokenValid = $isTokenValid; " +
                            "isAnimationFinished = $isAnimationFinished")
                }
            }

            requestService?.doRequest("token/status", languageShortName, callback, "token", token, "email", email)
        } else {
            notLoggedInAction()
        }

        loginButton = layoutInflater.findViewById(R.id.goto_login)
        gotoRegisterButton = layoutInflater.findViewById(R.id.goto_register)

        loginButton.setOnClickListener(this)
        gotoRegisterButton.setOnClickListener(this)

        return layoutInflater
    }

    // Если токен валиден, переходим в основное приложение
    private fun loggedInAction() {
        val linkTo = arguments?.getString(APP_LINK_KEY)
        context?.let {
            val intent = when (linkTo) {
                // Если приложение открыто через уведомление, открываем профиль
                NotificationResolver.EVENT -> {
                    Intent(it, CalendarActivity::class.java).apply {
                        putExtra(NotificationResolver.APP_LINK_KEY, linkTo)
                    }
                }
                NotificationResolver.DOCUMENT, NotificationResolver.NOTIFICATION -> {
                    Intent(it, ProfileActivity::class.java).apply {
                        putExtra(NotificationResolver.APP_LINK_KEY, linkTo)
                    }
                }
                else -> {
                    Intent(it, MainActivity::class.java)
                }
            }
            startActivity(intent)
            requireActivity().finish() //закрываем активити логина
        }
    }

    // Если токен не валиден, запускаем видео и прячем окно анимации
    private fun notLoggedInAction() {
        activity?.runOnUiThread { splashView.visibility = View.GONE }
    }

    override fun onClick(v: View) {
        val transaction = requireFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left,
                        R.animator.slide_in_right, R.animator.slide_out_left)
        when (v.id) {
            R.id.goto_login -> transaction.replace(R.id.fragment_container,
                    LoginFragment()).addToBackStack(fragmentTag).commit()
            R.id.goto_register -> transaction.replace(R.id.fragment_container,
                    RegisterFragment()).addToBackStack(fragmentTag).commit()
            else -> {
            }
        }
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        // Сохраняем, проиграна анимация или нет
        bundle.putBoolean(ANIMATION_KEY, isAnimationFinished)
    }

    override fun onDetach() {
        super.onDetach()
        //при закрытии фрагмента отменяем все запросы
        requestService?.cancelAllRequests()
    }

    override fun onResume() {
        super.onResume()
        // Заново запускаем видео
/*        if (videoView != null) {
            //videoView.start();
        }*/
    }
}