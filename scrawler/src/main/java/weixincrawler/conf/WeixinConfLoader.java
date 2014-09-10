package weixincrawler.conf;

import cn.edu.hfut.dmic.webcollector.util.Log;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

/**
 * Created by leilongyan on 2014/9/9.
 */
public class WeixinConfLoader {
    public static PropertiesConfiguration prop = null;

    static {
        loadConf();
    }

    public static void loadConf(){
        Log.Infos("info", "loading weixincrawler conf...");
        try {
            prop = new PropertiesConfiguration();
            prop.setEncoding("utf8");
            prop.load("weixincrawl.properties");
            FileChangedReloadingStrategy strategy  =new FileChangedReloadingStrategy(){
                public void reloadingPerformed(){
                    super.reloadingPerformed();
                    //reloadConf();
                    Log.Infos("info", "weixincrawl.properties file reloading...");
                }
            };
            prop.setReloadingStrategy(strategy);

            //reloadConf();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key, String defaultValue){
        if(prop == null){
            return defaultValue;
        }
        Object proValue = prop.getProperty(key);
        if(null != proValue){
            return proValue.toString();
        }
        return defaultValue;
    }

    public static void main(String []args) {
        System.out.println(getProperty(WeixinConfConstant.PAGENUM,"null"));
        System.out.println(getProperty(WeixinConfConstant.SEEDWORDS,"null"));
        System.out.println(getProperty(WeixinConfConstant.SEEDREGEX,"null"));
    }
}
