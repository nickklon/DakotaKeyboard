@file:Suppress("DEPRECATION") // FIXME replace deprecated methods

package com.nickklonne.dakotakeyboard

import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener
import android.view.KeyEvent
import android.view.inputmethod.InputConnection
import com.nickklonne.dakotakeyboard.DakotaKeyboardInputMethodService.Companion.KEYCODE_DOUBLE_SPACE
import com.nickklonne.dakotakeyboard.DakotaKeyboardInputMethodService.Companion.KEYCODE_CAPS_LOCK
import com.nickklonne.dakotakeyboard.DakotaKeyboardInputMethodService.Companion.KEYCODE_SHIFT_PUNCTUATION

class DakotaKeyboardListener(
    private val inputConnectionSupplier: () -> InputConnection,
    private val doKeyboardShift: (unshiftOnly: Boolean, setCapsLock: Boolean) -> Unit,
    private val keyboardIsShifted: () -> Boolean,
    private val doKeyboardAlt: () -> Unit,
    private val doKeyboardModeChange: () -> Unit,
) : OnKeyboardActionListener {

    private var shouldUnshift = false
    private var lastKeyWasShift = false

    /**
     * Set whether we should unshift the keyboard after the next character.
     */
    fun setUnshiftOnNextChar(value: Boolean) {
        shouldUnshift = value
    }

    override fun onKey(primaryCode: Int, keyCodes: IntArray) {

        when (primaryCode) {
            Keyboard.KEYCODE_ALT -> onAlt()
            Keyboard.KEYCODE_MODE_CHANGE -> onModeChange()
            Keyboard.KEYCODE_DELETE -> onDelete()
            Keyboard.KEYCODE_DONE -> onReturn()
            KEYCODE_DOUBLE_SPACE -> onDoubleSpace()
            Keyboard.KEYCODE_SHIFT, KEYCODE_CAPS_LOCK, KEYCODE_SHIFT_PUNCTUATION -> onShift(
                forceUnshift = false,
                setCapsLock = primaryCode == KEYCODE_CAPS_LOCK
            )

            else -> onEverythingElse(primaryCode)
        }

        lastKeyWasShift = primaryCode in setOf(Keyboard.KEYCODE_SHIFT, KEYCODE_CAPS_LOCK)
    }


    private fun getCurrentIC() = inputConnectionSupplier()

    private fun onDelete() {
        // This IF is a hack, but only delete if the last key wasn't shift to avoid caps-lock causing
        // a delete. See KeyboardView#detectAndSendKey mInMultiTap block
        if (!lastKeyWasShift) {
            getCurrentIC().deleteSurroundingText(1, 0)
        }
    }

    private fun onShift(forceUnshift: Boolean, setCapsLock: Boolean) {
        doKeyboardShift(forceUnshift, setCapsLock)
    }

    private fun onModeChange() {
        doKeyboardModeChange()
    }

    private fun onAlt() {
        doKeyboardAlt()
    }

    private fun onReturn() {
        getCurrentIC().sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
    }

    private fun onDoubleSpace() {
        getCurrentIC().commitText(". ", 1)
    }

    // Handle a non-special key
    private fun onEverythingElse(primaryCode: Int) {
        var c = primaryCode.toChar()
        if (c.isLetter() && keyboardIsShifted()) {
            c = Character.toUpperCase(c)
        }

        getCurrentIC().commitText(c.toString(), 1)

        // If previous key was shift, unshift now
        if (shouldUnshift) {
            shouldUnshift = false
            onShift(forceUnshift = true, setCapsLock = false)
        }
    }

    // Remainder of the interface methods are no-ops
    override fun onPress(primaryCode: Int) {
        /* no-op */
    }

    override fun onRelease(primaryCode: Int) {
        /* no-op */
    }

    override fun onText(text: CharSequence?) {
        /* no-op */
    }

    override fun swipeLeft() {
        /* no-op */
    }

    override fun swipeRight() {
        /* no-op */
    }

    override fun swipeDown() {
        /* no-op */
    }

    override fun swipeUp() {
        /* no-op */
    }
}