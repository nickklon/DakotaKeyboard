@file:Suppress("DEPRECATION") // FIXME replace deprecated methods

package com.nickklonne.dakotakeyboard

import android.annotation.SuppressLint
import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.view.View
import android.view.ViewGroup
import androidx.emoji2.emojipicker.EmojiPickerView

class DakotaKeyboardInputMethodService : InputMethodService() {

    private var isEmojiPickerVisible = false

    private val keyboardListener = DakotaKeyboardListener(
        inputConnectionSupplier = this::getCurrentInputConnection,
        doKeyboardShift = this::toggleShift,
        keyboardIsShifted = this::isShifted,
        doKeyboardAlt = TODO("Boom... sorry"),
        doKeyboardModeChange = this::toggleEmojiPicker,
    )

    private val keyboardPrimary: Keyboard by lazy {
        Keyboard(this, R.xml.dakota_keyboard_primary)
    }

    @delegate:SuppressLint("InflateParams")
    private val keyboardView: KeyboardView by lazy {
        val k = layoutInflater.inflate(R.layout.dakota_keyboard_view, null) as KeyboardView
        k.setOnKeyboardActionListener(keyboardListener)
        k.keyboard = keyboardPrimary
        k
    }

    private val emojiPickerView: EmojiPickerView by lazy {
        // https://developer.android.com/develop/ui/views/text-and-emoji/emoji-picker
        // TODO this eats the whole screen. Should be wrapped in a container to collapse anyway
        // TODO also needs a listener
        EmojiPickerView(this).apply { // TODO move this to a layout file
            emojiGridColumns = 9
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onCreateInputView(): View = keyboardView

    private fun isShifted(): Boolean = keyboardView.isShifted

    private fun toggleEmojiPicker() {
        val newView = if (isEmojiPickerVisible) keyboardView else emojiPickerView
        super.setInputView(newView)
        isEmojiPickerVisible = !isEmojiPickerVisible
    }

    private fun toggleShift(unshiftOnly: Boolean, setCapsLock: Boolean) {

        if (unshiftOnly) {
            keyboardView.setShifted(false)
            return
        }

        if (setCapsLock) {
            keyboardView.setShifted(true)
            keyboardListener.setUnshiftOnNextChar(false)
            return
        }

        keyboardView.setShifted(!keyboardView.isShifted)
        if (keyboardView.isShifted) {
            keyboardListener.setUnshiftOnNextChar(true)
        }
    }

    companion object {
        const val KEYCODE_DOUBLE_SPACE = -132
        const val KEYCODE_CAPS_LOCK = -101
    }
}