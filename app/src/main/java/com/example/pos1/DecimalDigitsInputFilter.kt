package com.example.pos1

import android.text.InputFilter
import android.text.Spanned
import java.util.regex.Pattern

class DecimalDigitsInputFilter(digitsAfterZero: Int) : InputFilter {
    private var mPattern: Pattern = Pattern.compile(
//        "[0-9]*+((,[0-9]{0,$digitsAfterZero})?|(\\.[0-9]{0,$digitsAfterZero})?)"
        "[0-9]*+(\\.[0-9]{0,$digitsAfterZero})?"

    )

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val matcher = mPattern.matcher(dest)
        if (!matcher.matches()) return ""
        return null
    }
}
