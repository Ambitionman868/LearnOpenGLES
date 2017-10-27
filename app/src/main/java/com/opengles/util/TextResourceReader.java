package com.opengles.util;

import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;

/**
 * Created by Jianpan on 2017/10/27.
 */

public class TextResourceReader {

    public static String readTextFileFromResource(Context context, int resourceId) {

        StringBuilder body = new StringBuilder();
        try {

            InputStream inputStream = context.getResources().openRawResource(resourceId);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String nextLine;


            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Resources.NotFoundException e) {
            throw new RuntimeException("resource not found :" + resourceId, e);
        }
        return body.toString();

    }
}
