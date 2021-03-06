/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.hfut.dmic.webcollector.util;

import cn.edu.hfut.dmic.webcollector.handler.Handler;
import cn.edu.hfut.dmic.webcollector.handler.Message;
import java.io.PrintWriter;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 *
 * @author hu
 */
public class Log {

    public static Handler handler = null;

    public static void Infos(String... infos) {
        sends(INFO, infos);
    }

    public static void Errors(String... infos) {
        sends(ERROR, infos);
    }

    public static Logger defaultLogger = Logger.getLogger(Log.class);

    static {
//        defaultLogger = org.apache.log4j.Logger.getLogger("default");
//        ConsoleAppender ca = new ConsoleAppender();
//        ca.setWriter(new PrintWriter(System.out));
//        ca.setLayout(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %p %c %x - %m%n "));
//        defaultLogger.addAppender(ca);
    }

    public static final int DEBUG = 0;
    public static final int INFO = 1;
    public static final int ERROR = 2;



    private static void sends(int type, String... infos) {
        if (handler == null) {
            String infostr = "";
            for (String info : infos) {
                infostr += info + " ";
            }

            switch (type) {
                case Log.DEBUG:
                    defaultLogger.debug(infostr);
                    break;
                case Log.INFO:
                    defaultLogger.info(infostr);
                    break;
                case Log.ERROR:
                    defaultLogger.error(infostr);
                    break;
            }
        } else {
            Message msg = new Message();
            msg.what = type;
            msg.obj = infos;
            handler.sendMessage(msg);
        }
    }
}
