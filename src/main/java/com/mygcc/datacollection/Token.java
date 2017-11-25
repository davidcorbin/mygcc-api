package com.mygcc.datacollection;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Token object.
 *
 * <h3>Usage</h3>
 * <p>The Token object is meant to be a developer friendly, secure way to
 * transfer myGCC username and password from client to server.</p>
 */
public class Token {
    /**
     * myGCC username.
     */
    private String username;

    /**
     * myGCC password.
     */
    private String password;

    /**
     * Set token data.
     * @param un username
     * @param pw password
     */
    public Token(final String un, final String pw) {
        this.username = un;
        this.password = pw;
    }

    /**
     * Set token data from token.
     * @param token token string
     * @throws InvalidCredentialsException token formatted incorrectly
     */
    public Token(final String token) throws InvalidCredentialsException {
        String[] decry = decrypt(token);
        setUsername(decry[0]);
        setPassword(decry[1]);
    }

    /**
     * Get username.
     * @return username
     */
    public final String getUsername() {
        return username;
    }

    /**
     * Set username.
     * @param un username
     */
    public final void setUsername(final String un) {
        this.username = un;
    }

    /**
     * Get password.
     * @return password
     */
    public final String getPassword() {
        return password;
    }

    /**
     * Set password.
     * @param pw password
     */
    public final void setPassword(final String pw) {
        this.password = pw;
    }

    /**
     * Generate a token to be delivered to user.
     *
     * The token contains the username, password, sessionID, and ASPXAUTH
     * separated by a pipe (|) character.
     * @param rawUsername username
     * @param rawPassword password
     * @return encrypted token
     * @throws InvalidCredentialsException bad credentials
     * @throws UnexpectedResponseException unhandled response from myGCC
     */
    public static String encrypt(final String rawUsername,
                                 final String rawPassword) throws
            InvalidCredentialsException, UnexpectedResponseException {
        // Check that password doesn't contain '&#124;'
        if (rawPassword.contains("&#124;")) {
            throw new InvalidCredentialsException(
                    "Password may not contain '&#124;'");
        }

        // Escape pipes
        String pw = escapePipe(rawPassword);
        String un = escapePipe(rawUsername);

        String tkraw = un + "|" + pw;

        try {
            IvParameterSpec iv = new IvParameterSpec(
                    getInitVector().getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(
                    getEncryptionKey().getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(tkraw.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException
                | NoSuchPaddingException | InvalidKeyException
                | InvalidAlgorithmParameterException | IllegalBlockSizeException
                | BadPaddingException e) {
            e.printStackTrace();
            throw new InvalidCredentialsException("Error encrypting token");
        }
    }

    /**
     * Encrypt instance username and password.
     * @return encrypted string
     * @throws InvalidCredentialsException invalid credentials
     * @throws UnexpectedResponseException unexpected response from myGCC
     */
    public final String encrypt() throws InvalidCredentialsException,
            UnexpectedResponseException {
        return encrypt(getUsername(), getPassword());
    }

    /**
     * Decrypt token.
     * @param token result of encryptToken
     * @throws InvalidCredentialsException invalid token - means that required
     * data could not be retrieved from token
     * @return Array of four Strings in the form
     * {@code [username, password, sessionid, aspxauth]}
     */
    private static String[] decrypt(final String token) throws
            InvalidCredentialsException {
        final int expectedTokenLength = 2;

        // Check that token is a string
        if (token == null || token.trim().isEmpty()) {
            throw new InvalidCredentialsException("Token invalid");
        }

        try {
            IvParameterSpec iv = new IvParameterSpec(
                    getInitVector().getBytes("UTF-8"));
            SecretKeySpec sKeySpec = new SecretKeySpec(
                    getEncryptionKey().getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, sKeySpec, iv);

            byte[] original;
            try {
                original = cipher.doFinal(Base64.getDecoder().decode(token));
            } catch (IllegalBlockSizeException e) {
                throw new InvalidCredentialsException("Token invalid");
            }

            String decoded = new String(original, "UTF-8");
            String[] keyvalues = decoded.split("\\|");

            // Verify that there are 2 values in the token
            if (keyvalues.length != expectedTokenLength) {
                throw new InvalidCredentialsException("Expected 2 values in "
                        + "token");
            }

            // Unescape each parameter in the token
            for (int i = 0; i < keyvalues.length; i++) {
                keyvalues[i] = unescapePipe(keyvalues[i]);
            }

            return keyvalues;
        } catch (UnsupportedEncodingException | InvalidKeyException
                | NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidParameterException | InvalidAlgorithmParameterException
                | BadPaddingException ex) {
            throw new InvalidCredentialsException("Token invalid");
        }
    }

    /**
     * Escape pipe character in string.
     * Replaces pipe ASCII character with HTML character code.
     * @param unescaped unescaped string
     * @return escaped string
     */
    private static String escapePipe(final String unescaped) {
        return unescaped.replace("|", "&#124;");
    }

    /**
     * Unescape pipe character in string.
     * Replaces HTML character code with pipe ASCII character.
     * @param escaped escaped string
     * @return unescaped string
     */
    private static String unescapePipe(final String escaped) {
        return escaped.replace("&#124;", "|");
    }

    /**
     * Get initial vector for encryption.
     * Expects a 16 character string.
     * @return vector string
     */
    private static String getInitVector() {
        if (System.getenv("initvect") != null) {
            return System.getenv("initvect");
        } else {
            throw new RuntimeException("Environment variable 'initvect' "
                    + "not found");
        }
    }

    /**
     * Get encryption key.
     * Expects a 16 character string.
     * @return encryption key
     */
    private static String getEncryptionKey() {
        if (System.getenv("enckey") != null) {
            return System.getenv("enckey");
        } else {
            throw new RuntimeException("Environment variable 'enckey' "
                    + "not found");
        }
    }
}
