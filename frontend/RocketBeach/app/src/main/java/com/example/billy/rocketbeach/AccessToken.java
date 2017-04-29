package com.example.billy.rocketbeach;

import java.util.Locale;

class AccessToken {
    static String makeToken(String key) {
        return String.format(Locale.ENGLISH, "Token token=%s", key);
    }
}
