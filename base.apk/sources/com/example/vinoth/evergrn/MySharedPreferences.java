package com.example.vinoth.evergrn;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MySharedPreferences {
    private static final String CHARSET = "UTF-8";
    private static final String KEY_TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String SECRET_KEY_HASH_TRANSFORMATION = "SHA-256";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static MySharedPreferences instance;
    private final boolean encryptKeys;
    private final Cipher keyWriter;
    private final SharedPreferences preferences;
    private final Cipher reader;
    private final Cipher writer;

    public static class SecurePreferencesException extends RuntimeException {
        public SecurePreferencesException(Throwable th) {
            super(th);
        }
    }

    public MySharedPreferences(Context context, String str, String str2, boolean z) throws SecurePreferencesException {
        try {
            this.writer = Cipher.getInstance(TRANSFORMATION);
            this.reader = Cipher.getInstance(TRANSFORMATION);
            this.keyWriter = Cipher.getInstance(KEY_TRANSFORMATION);
            initCiphers(str2);
            this.preferences = context.getSharedPreferences(str, null);
            this.encryptKeys = z;
        } catch (Context context2) {
            throw new SecurePreferencesException(context2);
        } catch (Context context22) {
            throw new SecurePreferencesException(context22);
        }
    }

    public static MySharedPreferences getInstance(Context context, String str) {
        if (instance == null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append("Key");
            instance = new MySharedPreferences(context, str, stringBuilder.toString(), true);
        }
        return instance;
    }

    protected void initCiphers(String str) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        AlgorithmParameterSpec iv = getIv();
        str = getSecretKey(str);
        this.writer.init(1, str, iv);
        this.reader.init(2, str, iv);
        this.keyWriter.init(1, str);
    }

    protected IvParameterSpec getIv() {
        Object obj = new byte[this.writer.getBlockSize()];
        System.arraycopy("fldsjfodasjifudslfjdsaofshaufihadsf".getBytes(), 0, obj, 0, this.writer.getBlockSize());
        return new IvParameterSpec(obj);
    }

    protected SecretKeySpec getSecretKey(String str) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return new SecretKeySpec(createKeyBytes(str), TRANSFORMATION);
    }

    protected byte[] createKeyBytes(String str) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MessageDigest instance = MessageDigest.getInstance(SECRET_KEY_HASH_TRANSFORMATION);
        instance.reset();
        return instance.digest(str.getBytes(CHARSET));
    }

    public void putString(String str, String str2) {
        if (str2 == null) {
            this.preferences.edit().remove(toKey(str)).commit();
        } else {
            putValue(toKey(str), str2);
        }
    }

    public void putBoolean(String str, Boolean bool) {
        if (bool == null) {
            this.preferences.edit().remove(toKey(str)).commit();
        } else {
            putValue(toKey(str), Boolean.toString(bool.booleanValue()));
        }
    }

    public void putLong(String str, long j) {
        putValue(toKey(str), Long.toString(j));
    }

    public void putInt(String str, int i) {
        putValue(toKey(str), Integer.toString(i));
    }

    public boolean containsKey(String str) {
        return this.preferences.contains(toKey(str));
    }

    public void removeValue(String str) {
        this.preferences.edit().remove(toKey(str)).commit();
    }

    public String getString(String str, String str2) throws SecurePreferencesException {
        return this.preferences.contains(toKey(str)) ? decrypt(this.preferences.getString(toKey(str), "")) : str2;
    }

    public long getLong(String str, long j) throws SecurePreferencesException {
        return this.preferences.contains(toKey(str)) ? Long.parseLong(decrypt(this.preferences.getString(toKey(str), ""))) : j;
    }

    public boolean getBoolean(String str, boolean z) throws SecurePreferencesException {
        return this.preferences.contains(toKey(str)) ? Boolean.parseBoolean(decrypt(this.preferences.getString(toKey(str), ""))) : z;
    }

    public int getInt(String str, int i) throws SecurePreferencesException {
        return this.preferences.contains(toKey(str)) ? Integer.parseInt(decrypt(this.preferences.getString(toKey(str), ""))) : i;
    }

    public void commit() {
        this.preferences.edit().commit();
    }

    public void clear() {
        this.preferences.edit().clear().commit();
    }

    private String toKey(String str) {
        return this.encryptKeys ? encrypt(str, this.keyWriter) : str;
    }

    private void putValue(String str, String str2) throws SecurePreferencesException {
        this.preferences.edit().putString(str, encrypt(str2, this.writer)).commit();
    }

    protected String encrypt(String str, Cipher cipher) throws SecurePreferencesException {
        try {
            return Base64.encodeToString(convert(cipher, str.getBytes(CHARSET)), 2);
        } catch (String str2) {
            throw new SecurePreferencesException(str2);
        }
    }

    protected String decrypt(String str) {
        try {
            return new String(convert(this.reader, Base64.decode(str, 2)), CHARSET);
        } catch (String str2) {
            throw new SecurePreferencesException(str2);
        }
    }

    private static byte[] convert(Cipher cipher, byte[] bArr) throws SecurePreferencesException {
        try {
            return cipher.doFinal(bArr);
        } catch (Cipher cipher2) {
            throw new SecurePreferencesException(cipher2);
        }
    }
}
