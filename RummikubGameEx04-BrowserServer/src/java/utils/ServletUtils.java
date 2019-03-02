package utils;

import manager.ProxyManager;

public class ServletUtils {
    public static ProxyManager manager;
    public static ProxyManager getGameManager() {
        if (manager == null) {
            manager = new ProxyManager();
        }
        return manager;
    }
     
}
