package org.yarek.fasttestapp.model.database;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LoaderSQL {

    private static String path = "/sql/";
    private static String extention = ".sql";

    public static void setPath(String path) {
        LoaderSQL.path = path;
    }

    public static void setExtention(String extention) {
        LoaderSQL.extention = extention;
    }

    public static String load(String queryName) {
        StringBuilder content = new StringBuilder();
        try (InputStream inputStream = LoaderSQL.class.getResourceAsStream(path + queryName + extention);
             DataInputStream dataInputStream = new DataInputStream(inputStream)) {

            int byteData;
            while ((byteData = dataInputStream.read()) != -1) {
                content.append((char) byteData);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading SQL query: " + queryName, e);
        }

        return content.toString();
    }

}
