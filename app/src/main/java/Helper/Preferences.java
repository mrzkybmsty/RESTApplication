package Helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
    /**
     * Pendeklarasian key-data berupa String, untuk sebagai wadah penyimpanan data.
     * Jadi setiap data mempunyai key yang berbeda satu sama lain
     */
    private static final String KEY_USER_TEREGISTER = "user_uname", KEY_PASS_TEREGISTER = "user_password";
    private static final String KEY_USERNAME_SEDANG_LOGIN = "Username_logged_in";
    private static final String KEY_STATUS_SEDANG_LOGIN = "Status_logged_in";
    private static final String KEY_USER_ID = "user_id";

    /**
     * Pendlakarasian Shared Preferences yang berdasarkan paramater context
     */
    private static SharedPreferences getSharedPreference(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setUserId(Context context, String id) {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(KEY_USER_ID, id);
        editor.apply();
    }

    public static String getUserId(Context context) {
        return getSharedPreference(context).getString(KEY_USER_ID, "");
    }

    /**
     * Deklarasi Edit Preferences dan mengubah data
     * yang memiliki key isi KEY_USER_TEREGISTER dengan parameter username
     */
    public static void setRegisteredUser(Context context, String username) {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(KEY_USER_TEREGISTER, username);
        editor.apply();
    }

    /**
     * Mengembalikan nilai dari key KEY_USER_TEREGISTER berupa String
     */
    public static String getRegisteredUser(Context context) {
        return getSharedPreference(context).getString(KEY_USER_TEREGISTER, "");
    }

    /**
     * Deklarasi Edit Preferences dan mengubah data
     * yang memiliki key KEY_PASS_TEREGISTER dengan parameter password
     */
    public static void setRegisteredPass(Context context, String password) {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(KEY_PASS_TEREGISTER, password);
        editor.apply();
    }

    /**
     * Mengembalikan nilai dari key KEY_PASS_TEREGISTER berupa String
     */
    public static String getRegisteredPass(Context context) {
        return getSharedPreference(context).getString(KEY_PASS_TEREGISTER, "");
    }

    /**
     * Deklarasi Edit Preferences dan mengubah data
     * yang memiliki key KEY_USERNAME_SEDANG_LOGIN dengan parameter username
     */
    public static void setLoggedInUser(Context context, String username) {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(KEY_USERNAME_SEDANG_LOGIN, username);
        editor.apply();
    }

    /**
     * Mengembalikan nilai dari key KEY_USERNAME_SEDANG_LOGIN berupa String
     */
    public static String getLoggedInUser(Context context) {
        return getSharedPreference(context).getString(KEY_USERNAME_SEDANG_LOGIN, "");
    }

    /**
     * Deklarasi Edit Preferences dan mengubah data
     * yang memiliki key KEY_STATUS_SEDANG_LOGIN dengan parameter status
     */
    public static void setLoggedInStatus(Context context, boolean status) {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putBoolean(KEY_STATUS_SEDANG_LOGIN, status);
        editor.apply();
    }

    /**
     * Mengembalikan nilai dari key KEY_STATUS_SEDANG_LOGIN berupa boolean
     */
    public static boolean getLoggedInStatus(Context context) {
        return getSharedPreference(context).getBoolean(KEY_STATUS_SEDANG_LOGIN, false);
    }

    /**
     * Deklarasi Edit Preferences dan menghapus data, sehingga menjadikannya bernilai default
     * khusus data yang memiliki key KEY_USERNAME_SEDANG_LOGIN dan KEY_STATUS_SEDANG_LOGIN
     */
    public static void clearLoggedInUser(Context context) {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.remove(KEY_USERNAME_SEDANG_LOGIN);
        editor.remove(KEY_STATUS_SEDANG_LOGIN);
        editor.apply();
    }
}
