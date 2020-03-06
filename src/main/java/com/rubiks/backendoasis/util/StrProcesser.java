package com.rubiks.backendoasis.util;


public class StrProcesser {
    public  String escapeExprSpecialWord(String keyword) {
        String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|", "\"", "、" };
        for (String key : fbsArr) {
            if (keyword.contains(key)) {
                keyword = keyword.replace(key, "");
            }
        }
        return keyword;
    }
}
