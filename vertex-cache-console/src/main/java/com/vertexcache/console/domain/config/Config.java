package com.vertexcache.console.domain.config;

import com.vertexcache.common.cli.CommandLineArgsParser;
import com.vertexcache.common.config.ConfigBase;
import com.vertexcache.common.config.reader.ConfigLoader;
import com.vertexcache.common.config.reader.ConfigLoaderFactory;
import com.vertexcache.common.security.KeyPairHelper;

import java.security.PublicKey;

public class Config extends ConfigBase {

    public static final String APP_NAME = "VertexCache Console";
    private boolean configLoaded = false;
    private boolean configError = false;
    private String configFilePath;

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

                ConfigLoader configLoader = ConfigLoaderFactory.getLoader(this.configFilePath);
                if (configLoader.loadFromPath(this.configFilePath)) {
                    this.configLoaded = true;
                }

                // Host
                if (configLoader.isExist(ConfigKey.SERVER_HOST)) {
                    this.serverHost = configLoader.getProperty(ConfigKey.SERVER_HOST);
                }

                // Port
                if (configLoader.isExist(ConfigKey.SERVER_PORT)) {
                    this.serverPort = Integer.parseInt(configLoader.getProperty(ConfigKey.SERVER_PORT));
                }

                // Encrypt Message Layer
                if (configLoader.isExist(ConfigKey.ENABLE_ENCRYPT_MESSAGE) && Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_ENCRYPT_MESSAGE))) {
                    this.encryptMessage = true;
                    this.publicKey = KeyPairHelper.decodePublicKey(configLoader.getProperty(ConfigKey.PUBLIC_KEY));
                }

                // Encrypt Transport Layer
                if (configLoader.isExist(ConfigKey.ENABLE_ENCRYPT_TRANSPORT) && Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_ENCRYPT_TRANSPORT))) {
                   this.encryptTransport = true;

                    if (configLoader.isExist(ConfigKey.ENABLE_VERIFY_SERVER_CERTIFICATE) && Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_VERIFY_SERVER_CERTIFICATE))) {
                        this.verifyServerCertificate = true;
                        if (configLoader.isExist(ConfigKey.SERVER_CERTIFICATE_FILEPATH)) {
                            this.serverCertificatePath = configLoader.getProperty(ConfigKey.SERVER_CERTIFICATE_FILEPATH);
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
}
