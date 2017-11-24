package com.xiaotian.framework.util;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.method.NumberKeyListener;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * @author Administrator
 * @version 1.0.0
 * @name UtilEditText
 * @description EditText Util
 * @date 2015-7-2
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class UtilEditText {
    // 输入法,输入字符过滤器
    public DigitsKeyListener getKeyListenerFilterInteger() {
        return DigitsKeyListener.getInstance("0123456789");
    }

    public DigitsKeyListener getKeyListenerFilterLetter() {
        return DigitsKeyListener.getInstance("abcdefghijklmnopqrstuvwsyzABCDEFGHIJKLMNOPQRSTUVWSYZ");
    }

    public DigitsKeyListener getKeyListenerFilterLetterInteger() {
        return DigitsKeyListener.getInstance("0123456789abcdefghijklmnopqrstuvwsyzABCDEFGHIJKLMNOPQRSTUVWSYZ");
    }

    public DigitsKeyListener getKeyListenerFilterDigit(boolean sign, boolean decimal) {
        return DigitsKeyListener.getInstance(sign, decimal); // 数字过滤,sign:整数,decimal:小数
    }

    // 选中文本
    public void selecteText(EditText editText) {
        editText.setSelection(0, editText.getText().length());
    }

    public void selecteText(EditText editText, int start, int end) {
        editText.setSelection(start, end);
    }

    public void showSoftKeyboard(View focusedView) {
        focusedView.requestFocus();
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        InputMethodManager imm = (InputMethodManager) focusedView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void hideSoftKeyboard(View focusedView) {
        InputMethodManager inputManager = (InputMethodManager) focusedView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
    }

    /********************************* Inner Class *********************************/
    public static class PhoneNumberKeyListener extends NumberKeyListener {

        @Override
        public int getInputType() {
            return android.text.InputType.TYPE_CLASS_PHONE;
        }

        @Override
        protected char[] getAcceptedChars() {
            return new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        }

    }

    // 中文字符过滤
    public static class ChineseKeyListener<T> implements TextWatcher {
        CharSequence preText;
        String RES_CHINESE = "\u4E00-\u9FA5";
        EditText mEditText;
        T[] initParamater;

        public ChineseKeyListener(EditText editText, T... params) {
            this.mEditText = editText;
            this.initParamater = params;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            preText = s.toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String pres = preText.toString();
            String curs = s.toString();
            if (curs.equals(pres)) return;
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < curs.length(); i++) {
                if (isChinese(curs.charAt(i))) {
                    sb.append(curs.charAt(i));
                }
            }
            preText = sb.toString();
            mEditText.setText(preText);
            mEditText.setSelection(preText.length());
        }

        public boolean isChinese(char c) {
            Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
            if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                    || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                return true;
            }
            return false;
        }
    }

    // 是否包含小数位
    public boolean isDecimals(Double value) {
        return value % 1 != 0;
    }
    // XML 属性
    // android:capitalize 首字母大写
    // android:autoText 自动拼写帮助
    // android:maxLength 字符最大长度
    //android:ellipsize="end" 自动隐藏尾部溢出数据
}
