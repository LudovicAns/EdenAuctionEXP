package fr.edencraft.edenauctionexp.utils;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class JsonUtils {

    public static void writeJsonData(File dataFile, Gson gson, Object data) {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(dataFile), StandardCharsets.UTF_8);
            String jsonContent = gson.toJson(data);

            writer.write(jsonContent);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
