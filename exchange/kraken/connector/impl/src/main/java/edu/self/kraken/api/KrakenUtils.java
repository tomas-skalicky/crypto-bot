/*
 * A program to automatically trade cryptocurrencies.
 * Copyright (C) 2020 $user.name<$user.email>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package edu.self.kraken.api;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Provides basic utilities for hash, byte array and string manipulation.
 *
 * @author nyg
 */
final class KrakenUtils {

    private static final String ERROR_NULL_INPUT = "Input can't be null.";
    private static final String ERROR_NULL_ARRAYS = "Given arrays can't be null.";

    private static final String UTF8 = "UTF-8";

    private static final String SHA256 = "SHA-256";
    private static final String HMAC_SHA512 = "HmacSHA512";

    public static byte[] base64Decode(String input) {
        return Base64.getDecoder().decode(input);
    }

    public static String base64Encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    public static byte[] concatArrays(byte[] a,
                                      byte[] b) {

        if (a == null || b == null) {
            throw new IllegalArgumentException(ERROR_NULL_ARRAYS);
        }

        byte[] concat = new byte[a.length + b.length];
        for (int i = 0; i < concat.length; i++) {
            concat[i] = i < a.length ? a[i] : b[i - a.length];
        }

        return concat;
    }

    public static byte[] hmacSha512(byte[] key,
                                    byte[] message) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(HMAC_SHA512);
        mac.init(new SecretKeySpec(key, HMAC_SHA512));
        return mac.doFinal(message);
    }

    public static byte[] sha256(String message) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(SHA256);
        return md.digest(stringToBytes(message));
    }

    public static byte[] stringToBytes(String input) {

        if (input == null) {
            throw new IllegalArgumentException(ERROR_NULL_INPUT);
        }

        return input.getBytes(Charset.forName(UTF8));
    }

    public static String urlEncode(String input) throws UnsupportedEncodingException {
        return URLEncoder.encode(input, UTF8);
    }

    private KrakenUtils() {
    }
}
