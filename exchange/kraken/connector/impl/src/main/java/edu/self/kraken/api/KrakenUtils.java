/*
 * Copyright (c) 2015, nyg
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
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

    private KrakenUtils() {
    }

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
}
