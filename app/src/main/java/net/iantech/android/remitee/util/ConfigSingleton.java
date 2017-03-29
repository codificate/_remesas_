package net.iantech.android.remitee.util;

import android.content.Context;

import net.iantech.android.remitee.enums.ConfigEnum;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by andres on 27/03/17.
 */

public class ConfigSingleton {

    private Properties confProperties;

    private static ConfigSingleton instance;

    private ConfigSingleton(){}

    /**
     * Método debe ser inicializado por un tercero antes de llamar a las propiedades, de lo contrario será NULL
     *
     * @param context
     */
    public void loadProperties(Context context){
        InputStream inputStream = null;
        confProperties = new Properties();
        try {
            inputStream = context.getAssets().open("config.properties");
            confProperties.load(inputStream);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     *
     * @param key
     * @return
     */
    public String getConfValue(ConfigEnum key){
        String result = "";
        try {
            result = confProperties.getProperty(key.name());
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     *
     * @return
     */
    public static synchronized ConfigSingleton getInstance() {
        if(instance == null) {
            instance = new ConfigSingleton();
        }
        return instance;
    }
}