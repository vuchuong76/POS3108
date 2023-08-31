package com.example.pos1

import android.text.InputFilter
import android.text.Spanned
import java.util.regex.Pattern

class DecimalDigitsInputFilter(digitsAfterZero: Int) : InputFilter {
    //tạo ra mẫu tiêu chuẩn của edittext
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
        //chuỗi mới trong đó source là phần được thêm mới
        val newString = dest.substring(0, dstart) + source.subSequence(start, end) + dest.substring(dend)
        val matcher = mPattern.matcher(newString)

        if (!matcher.matches() || newString.length > 6) {
            return ""
        }
        return null
    }
}
