package ru.tpu.russiantpu.utility.validation

import com.google.android.material.textfield.TextInputLayout
import com.mobsandgeeks.saripaar.adapter.ViewDataAdapter

class TextInputLayoutValidatorAdapter : ViewDataAdapter<TextInputLayout, String> {
    override fun getData(view: TextInputLayout?) = view?.editText?.let { it.text.toString() }
}