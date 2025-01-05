package tech.harvest.core.util;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class LoginApi {
    public static boolean logged = false;
    public static String userName = null;

    public static void checkLogin() {
        if (auth()) {
            logged = true;
        }
    }

    private static boolean auth() {
        String hwid = getHwid();
        try {
            URL url = new URL("https://pastebin.com/raw/rCJJpi8m");
            Scanner s = new Scanner(url.openStream());
            while (s.hasNext()) {
                String[] s2 = s.nextLine().split(":");
                userName = s2[0];
                if (hwid.equalsIgnoreCase(s2[1])) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getHwid() {
        try {
            StringBuilder s = new StringBuilder();
            String main = System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("COMPUTERNAME") + System.getProperty("user.name").trim();
            byte[] bytes = main.getBytes(StandardCharsets.UTF_8);
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] md5 = messageDigest.digest(bytes);
            int i = 0;
            for (byte b : md5) {
                s.append(Integer.toHexString((b & 255) | 768), 0, 3);
                if (i != md5.length - 1) {
                    s.append("-");
                }
                i++;
            }
            return s.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "Error :(";
        }
    }
}
