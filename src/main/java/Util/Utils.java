package Util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Utils {
    private static Logger logger = LogManager.getLogger(Utils.class);
    private static Properties prop;

    public static String getProps(String key) {
        if(prop == null)
        {
            String result = "";
            prop = new Properties();
            String propFileName = "config.properties";
            InputStream is = new Utils().getClass().getClassLoader().getResourceAsStream(propFileName);
            if(is != null) {
                try {
                    prop.load(is);
                } catch (IOException e) {
                    logger.error("Cannot load Properties");
                }
            }
        }
        return prop.getProperty(key);
    }
}

