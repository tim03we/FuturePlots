package com.intellectualcrafters.plot.util;

import com.intellectualcrafters.plot.config.Settings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class HttpUtil {

    public static String readUrl(String urlString) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(urlString).openStream()))) {
            StringBuilder buffer = new StringBuilder();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }
            return buffer.toString();
        } catch (IOException e) {
            if (Settings.DEBUG) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
