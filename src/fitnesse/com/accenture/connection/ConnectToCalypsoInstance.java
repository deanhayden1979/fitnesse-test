package fitnesse.com.accenture.connection;

import com.calypso.tk.core.Log;
import com.calypso.tk.service.DSConnection;
import com.calypso.tk.util.ConnectException;
import com.calypso.tk.util.ConnectionUtil;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

public class ConnectToCalypsoInstance {

    private static ConnectToCalypsoInstance instance = new ConnectToCalypsoInstance();

    private final DSConnection ds;
    private String user;

    private ConnectToCalypsoInstance() {
        this("admin", "calypso", "TaskRunner");
    }

    private String getEnvironment() {
        Resource resource = new ClassPathResource("./resource/system.properties");
        String env = "";
        try {
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
            env = props.getProperty("environment.name");
        } catch (IOException ex) {
            Log.error(Log.CALYPSOX, "Unable get environment name", ex);
        }
        return env;
    }

    public ConnectToCalypsoInstance(String user, String pwd, String appName) {
        DSConnection ds = null;
        this.user = user;
        try {
            ds = ConnectionUtil.connect(user, pwd, appName, "v15");
        } catch (ConnectException ex) {
            Log.error(Log.CALYPSOX, "Unable to obtain connection to DataServer", ex);
            throw new RuntimeException("Unable to obtain connection to DataServer", ex);
        }

        this.ds = ds;
    }

    public static ConnectToCalypsoInstance getInstance() throws InterruptedException{
        return instance;
    }


    public DSConnection getDS() {
        return this.ds;
    }

    public static void main(String[] args) throws InterruptedException{
        DSConnection ds = ConnectToCalypsoInstance.getInstance().getDS();
    }

    public void shutdown() {
        ConnectionUtil.shutdown();
    }

    public String getUser()
    {
    	return this.user;
    }
}
