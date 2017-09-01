package com.vedmitryapps.notes.untils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.EditText;

import com.vedmitryapps.notes.R;


public class RobotoFontEditText extends EditText {

    public RobotoFontEditText(Context context) {
        super(context);
    }

    public RobotoFontEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RobotoFontEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RobotoFontEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setFonts(attrs,context);
    }
    private void setFonts(AttributeSet attributeSet, Context context){
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attributeSet,
                R.styleable.CustomFontsTextView,
                0, 0);
        a.recycle();
        int fonts = a.getInt(R.styleable.CustomFontsTextView_fonts,-1);
        SingletonFonts singletonFonts = SingletonFonts.getInstance(context);
        switch (fonts){
            case (0):
                setTypeface(singletonFonts.getRobotoRegular());
                break;
            case (1):
                setTypeface(singletonFonts.getRobotoBold());
                break;
        }
    }
}
