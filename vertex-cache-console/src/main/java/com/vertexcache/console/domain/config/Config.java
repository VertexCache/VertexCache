package com.vertexcache.console.domain.config;

import com.vertexcache.common.cli.CommandLineArgsParser;
import com.vertexcache.common.config.ConfigBase;
import com.vertexcache.common.config.reader.PropertiesLoader;
import com.vertexcache.common.log.LogHelper;
import com.vertexcache.common.security.KeyPairHelper;

import java.security.PublicKey;

public class Config extends ConfigBase {

    public static final String APP_NAME = "VertexCache Console";
    public static final String APP_WELCOME = System.lineSeparator() + "Welcome to VertexCache Console Terminal: " + System.lineSeparator();
    private boolean configLoaded = false;
    private boolean configError = false;
    private String configFilePath;

    private boolean logLoaded = false;
    private String logFilePath;

    private String serverHost = ConfigKey.SERVER_HOST_DEFAULT;
    private int serverPort = ConfigKey.SERVER_PORT_DEFAULT;

    private boolean encryptMessage = false;
    private PublicKey publicKey;

    private boolean encryptTransport = false;
    private boolean verifyServerCertificate = false;
    private String serverCertificatePath;

    private static volatile Config instance;

    private Config() {
    }

    public static Config getInstance() {
        if (instance == null) {
            synchronized (Config.class) {
                if (instance == null) {
                    instance = new Config();
                }
            }
        }
        return instance;
    }

    @Override
    public void loadPropertiesFromArgs(CommandLineArgsParser commandLineArgsParser) {
        try {

            if(commandLineArgsParser.isExist("--config")) {
                this.configFilePath = commandLineArgsParser.getValue("--config");

                PropertiesLoader propertiesLoader = new PropertiesLoader();
                if (propertiesLoader.loadFromPath(this.configFilePath)) {
                    this.configLoaded = true;
                }

                // Host
                if (propertiesLoader.isExist(ConfigKey.SERVER_HOST)) {
                    this.serverHost = propertiesLoader.getProperty(ConfigKey.SERVER_HOST);
                }

                // Port
                if (propertiesLoader.isExist(ConfigKey.SERVER_PORT)) {
                    this.serverPort = Integer.parseInt(propertiesLoader.getProperty(ConfigKey.SERVER_PORT));
                }

                // Load Log4j2 property file path
                if (propertiesLoader.isExist(ConfigKey.LOG_FILEPATH)) {
                    this.logFilePath = propertiesLoader.getProperty(ConfigKey.LOG_FILEPATH);
                    this.logLoaded = LogHelper.getInstance().loadConfiguration(this.logFilePath);
                }

                // Encrypt Message Layer
                if (propertiesLoader.isExist(ConfigKey.ENABLE_ENCRYPT_MESSAGE) && Boolean.parseBoolean(propertiesLoader.getProperty(ConfigKey.ENABLE_ENCRYPT_MESSAGE))) {
                    this.encryptMessage = true;
                    this.publicKey = KeyPairHelper.decodePublicKey(propertiesLoader.getProperty(ConfigKey.PUBLIC_KEY));
                }

                // Encrypt Transport Layer
                if (propertiesLoader.isExist(ConfigKey.ENABLE_ENCRYPT_TRANSPORT) && Boolean.parseBoolean(propertiesLoader.getProperty(ConfigKey.ENABLE_ENCRYPT_TRANSPORT))) {
                   this.encryptTransport = true;

                    if (propertiesLoader.isExist(ConfigKey.ENABLE_VERIFY_SERVER_CERTIFICATE) && Boolean.parseBoolean(propertiesLoader.getProperty(ConfigKey.ENABLE_VERIFY_SERVER_CERTIFICATE))) {
                        this.verifyServerCertificate = true;
                        if (propertiesLoader.isExist(ConfigKey.SERVER_CERTIFICATE_FILEPATH)) {
                            this.serverCertificatePath = propertiesLoader.getProperty(ConfigKey.SERVER_CERTIFICATE_FILEPATH);
                        }
                    }
                }


            }

        } catch (Exception exception) {
            this.configLoaded = false;
            this.configError = true;
        }
    }

    public boolean isConfigLoaded() {
        return configLoaded;
    }

    public boolean isConfigError() {
        return configError;
    }

    public String getConfigFilePath() {
        return configFilePath;
    }

    public String getServerHost() {
        return serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    public boolean isEncryptMessage() {
        return encryptMessage;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public boolean isEncryptTransport() {
        return encryptTransport;
    }

    public boolean isVerifyServerCertificate() {
        return verifyServerCertificate;
    }

    public String getServerCertificatePath() {
        return serverCertificatePath;
    }

    public boolean isLogLoaded() {
        return logLoaded;
    }

    public String getLogFilePath() {
        return logFilePath;
    }
}
