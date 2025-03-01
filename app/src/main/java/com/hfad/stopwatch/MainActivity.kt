package com.hfad.stopwatch

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.os.SystemClock
import android.util.Log
import android.widget.Button
import android.widget.Chronometer

class MainActivity : AppCompatActivity() {

    lateinit var stopwatch: Chronometer // Хронометр
    var running = false // Хронометр работает?
    var offset: Long = 0 //Базовое смещение

    //Добавление строк для ключей, используемых с Bundle
    val OFFSET_KEY = "offset"
    val RUNNING_KEY = "running"
    val BASE_KEY = "base"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        //Получение ссылки на секундомер
        stopwatch = findViewById<Chronometer>(R.id.stopwatch)

        //Восстановление предыдущего состояния
        if (savedInstanceState != null) {
            offset = savedInstanceState.getLong(OFFSET_KEY)
            running = savedInstanceState.getBoolean(RUNNING_KEY)
            if (running) {
                stopwatch.base = savedInstanceState.getLong(BASE_KEY)
                stopwatch.start()
            } else setBaseTime()
        }

        //Кнопка start запускает секундомер, если он не работал
        val startButton = findViewById<Button>(R.id.start_button)
        startButton.setOnClickListener {
            if (!running) {
                setBaseTime()
                stopwatch.start()
                running = true
            }
        }

        //Кнопка pause останавливает секундомер, если он работал
        val pauseButton = findViewById<Button>(R.id.pause_button)
        pauseButton.setOnClickListener {
            if (running) {
                saveOffset()
                stopwatch.stop()
                running = false
            }
        }
        //Кнопка reset обнуляет offset и базовое время
        val resetButton = findViewById<Button>(R.id.reset_button)
        resetButton.setOnClickListener {
            if (running) {    //добавила остановку таймера, потому что...
                saveOffset()
                stopwatch.stop()
                running = false
            }
            offset = 0
            setBaseTime()
        }
    }

    //Сохранение свойств
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putLong(OFFSET_KEY, offset)
        savedInstanceState.putBoolean(RUNNING_KEY, running)
        savedInstanceState.putLong(BASE_KEY, stopwatch.base)
        super.onSaveInstanceState(savedInstanceState)
    }
    override fun onStop() {
        super.onStop()
        //Код, выполняемый при остановке активности
        Log.d("MainActivity", "onStop called")
        if (running) {
            saveOffset()
            stopwatch.stop()
        }
    }
//    override fun onPause() {
//        super.onPause()
//        // Остановка хронометра при приостановке активности
//        if (running) {
//            saveOffset()
//            stopwatch.stop()
//        }
//    }
override fun onPause() {
    super.onPause()
    // Проверка вызова onPause
    Log.d("MainActivity", "onPause called")

    // Остановка хронометра при приостановке активности
    if (running) {
        saveOffset()
        stopwatch.stop()
        Log.d("MainActivity", "Chronometer stopped")
    }
}
    //onPause() — вызывается, когда активность уходит с переднего плана, но еще может быть видна частично
    //(например, при сворачивании приложения или открытии диалогового окна). Здесь можно остановить хронометр,
    //так как пользователю больше не требуется взаимодействовать с ним.

    override fun onRestart() {
        super.onRestart()
        //Код, выполняемый при перезапуске активности
        if (running) {
            setBaseTime()
            stopwatch.start()
            offset = 0
        }
    }

    //Обновляет время stopwatch.base
    fun setBaseTime() {
        stopwatch.base = SystemClock.elapsedRealtime() - offset
    }

    //Сохраняет offset
    fun saveOffset() {
        offset = SystemClock.elapsedRealtime() - stopwatch.base
    }
    }
