package com.nickklonne.dakotakeyboard

import android.annotation.SuppressLint
import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.view.View

class DakotaKeyboardInputMethodService : InputMethodService() {

    @SuppressLint("InflateParams")
    override fun onCreateInputView(): View {
        val view: KeyboardView = layoutInflater.inflate(R.layout.dakota_keyboard_view, null) as KeyboardView
        val keyboard = Keyboard(this, R.xml.dakota_keyboard_primary)
        view.keyboard = keyboard

        // TODO: Implement a real listener
        view.setOnKeyboardActionListener(object : KeyboardView.OnKeyboardActionListener {
            override fun onPress(primaryCode: Int) {
            }

            override fun onRelease(primaryCode: Int) {
            }

            override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
            }

            override fun onText(text: CharSequence?) {
            }

            override fun swipeLeft() {
            }

            override fun swipeRight() {
            }

            override fun swipeDown() {
            }

            override fun swipeUp() {
            }
        })
        return view
    }

}