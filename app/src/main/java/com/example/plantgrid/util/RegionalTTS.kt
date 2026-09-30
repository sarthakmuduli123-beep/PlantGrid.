package com.example.plantgrid.util

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class RegionalTTS(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isReady = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isReady = true
        }
    }

    fun speak(text: String, lang: String = "hi") {
        if (isReady && tts != null) {
            val locale = when (lang) {
                "hi" -> Locale("hi", "IN")
                "or" -> Locale("or", "IN")
                else -> Locale.ENGLISH
            }
            tts?.language = locale
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "PlantGridTTS")
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}

