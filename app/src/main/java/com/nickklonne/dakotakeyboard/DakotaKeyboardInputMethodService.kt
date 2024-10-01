package com.nickklonne.dakotakeyboard

import android.annotation.SuppressLint
import android.inputmethodservice.InputMethodService
import android.view.View

class DakotaKeyboardInputMethodService : InputMethodService() {

    @SuppressLint("InflateParams")
    override fun onCreateInputView(): View {
        val view = layoutInflater.inflate(R.layout.delete_me, null)
        return view
    }

}