import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class MyProp {
    private static final String PATH_TO_PROPERTIES = String.format("%s%ssrc%smain%sresources%sconfig.properties",
            System.getProperty("user.dir"), File.separator, File.separator, File.separator, File.separator);

    private static Properties properties;

    public static String getProperty(String property) {
        if (properties == null) {
            loadProperties();
        }
        return properties.getProperty(property);
    }

    private static void loadProperties() {
        properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(PATH_TO_PROPERTIES)) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
