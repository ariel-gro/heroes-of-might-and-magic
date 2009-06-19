package heroes.rap;

import java.io.File;
import java.io.InputStream;

public class Configuration {

        public static final Class<Configuration> resourcesClass = Configuration.class;
        public static final InputStream mapStream = resourcesClass.getResourceAsStream("world_map.map");
        
        public static InputStream getHelpStream(String helpFileName)
        {
        	return resourcesClass.getResourceAsStream(helpFileName);
        }
        
        public static final String homeDir = System.getProperty("user.home") + File.separator + "Heroes";
        public static final String saveDir = Configuration.homeDir;
}