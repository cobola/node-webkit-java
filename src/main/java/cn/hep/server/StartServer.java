package cn.hep.server;


import org.apache.catalina.Server;
import org.apache.catalina.startup.Tomcat;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Date;


public class StartServer {
    private static final Logger log = Logger.getLogger(StartServer.class);

    private static int TOMCAT_PORT = 9087;
    final static Tomcat tomcat = new Tomcat();
    private Server server;


    // 开始嵌入式Tomcat服务器
    private static void startTomcat(String basedir) throws Exception {
        try {

            final String webappDirLocation = basedir;


            //The port that we should run on can be set into an environment variable
            //Look for that variable and default to 8080 if it isn't there.


            tomcat.setPort(TOMCAT_PORT);

            tomcat.addWebapp("/", new File(webappDirLocation).getAbsolutePath());
            log.info("configuring app with basedir: " + new File(webappDirLocation).getAbsolutePath());


            Thread startThread = new Thread() {
                public void run() {
                    try {
                        tomcat.start();
                        log.info("HepSample Server started at " + new Date());
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }
            };

            startThread.start();
            try {
                startThread.join();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            Thread controlThread = new Thread() {
                public void run() {
                    for (; ; ) {
                        try {
                            Thread.sleep(2000);
                            File f = new File(new File(webappDirLocation).getAbsolutePath() + "/tomcat.stop");
                            if (f.exists()) {
                                try {
                                    tomcat.stop();
                                    log.info("HepSample Server  stopped at " + new Date());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    throw new RuntimeException(e);
                                }
                                f.delete();
                                break;
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            };
            controlThread.start();
            try {
                controlThread.join();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            tomcat.getServer().await();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 开始嵌入式Tomcat服务器
    private static void stopTomcat(String basedir) throws Exception {
        try {

            Thread controlThread = new Thread() {
                public void run() {
                    for (; ; ) {
                        try {
                            File f = new File(new File("").getAbsolutePath() + "/tomcat.stop");
                            if (f.exists()) {
                                try {
                                    tomcat.stop();
                                    log.info("HepSample Server  stopped at " + new Date());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    throw new RuntimeException(e);
                                }
                                f.delete();
                                break;
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            };
            controlThread.start();
            try {
                controlThread.join();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start(String web) {
        StartServer start = new StartServer();
        try {

            start.startTomcat(web);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void stop(String web) {
        StartServer stop = new StartServer();
        try {

            stop.stopTomcat(web);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        StartServer start = new StartServer();
        String web = "web";
        if (args != null && args.length > 1) {
            web = args[0];
            if (args[1].equals("stop")) {

                start.stop(web);
            } else {
                start.start(web);
            }
        }

    }
}
