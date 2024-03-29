package com.vertexcache.domain.config;

import com.vertexcache.common.config.ConfigBase;
import com.vertexcache.common.config.reader.PropertiesLoader;
import com.vertexcache.common.cli.CommandLineArgsParser;
import com.vertexcache.common.security.KeyPairHelper;

import java.security.PrivateKey;
import java.security.PublicKey;

public class Config extends ConfigBase {

    private static volatile Config instance;

    private static final String APP_NAME = "VertexCache";
    private boolean configLoaded = false;
    private boolean configError = false;
    private String configFilePath;
    private boolean enableVerbose = true;

    private int serverPort = ConfigKey.SERVER_PORT_DEFAULT;

    private boolean encryptMessage = false;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    private boolean encryptTransport = false;
    private String keystoreFilePath;
    private String keystorePassword;

    public Config() {
    }

    public Config loadPropertiesFromArgs(CommandLineArgsParser commandLineArgsParser) {

        try {

            if(commandLineArgsParser.isExist("--config")) {
                this.configFilePath = commandLineArgsParser.getValue("--config");

                PropertiesLoader propertiesLoader = new PropertiesLoader();
                if (propertiesLoader.loadFromPath(this.configFilePath)) {
                    this.configLoaded = true;

                    // Verbose
                    if (propertiesLoader.isExist(ConfigKey.ENABLE_VERBOSE)) {
                        this.enableVerbose = Boolean.parseBoolean(propertiesLoader.getProperty(ConfigKey.ENABLE_VERBOSE));
                    }

                    // Port
                    if (propertiesLoader.isExist(ConfigKey.SERVER_PORT)) {
                        this.serverPort = Integer.parseInt(propertiesLoader.getProperty(ConfigKey.SERVER_PORT));
                    }

                    // Encrypt Message Layer
                    if (propertiesLoader.isExist(ConfigKey.ENABLE_ENCRYPT_MESSAGE) && Boolean.parseBoolean(propertiesLoader.getProperty(ConfigKey.ENABLE_ENCRYPT_MESSAGE))) {
                        this.encryptMessage = true;
                        this.publicKey = KeyPairHelper.decodePublicKey(propertiesLoader.getProperty(ConfigKey.PUBLIC_KEY));
                        this.privateKey = KeyPairHelper.decodePrivateKey(propertiesLoader.getProperty(ConfigKey.PRIVATE_KEY));
                    }

                    // Encrypt Transport Layer
                    if (propertiesLoader.isExist(ConfigKey.ENABLE_ENCRYPT_TRANSPORT) && Boolean.parseBoolean(propertiesLoader.getProperty(ConfigKey.ENABLE_ENCRYPT_TRANSPORT))) {
                        this.encryptTransport = true;
                        this.keystoreFilePath = propertiesLoader.getProperty(ConfigKey.KEYSTORE_FILEPATH);
                        this.keystorePassword = propertiesLoader.getProperty(ConfigKey.KEYSTORE_PASSWORD);
                    }

                }
            }

        } catch (Exception exception) {
            this.configLoaded = false;
            this.configError = true;
        } finally {
            return this;
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

    public String getAppName() {
        return Config.APP_NAME;
    }

    public boolean isEnableVerbose() {
        return enableVerbose;
    }



    public int getServerPort() {
        return serverPort;
    }

    public boolean isEncryptTransport() {
        return encryptTransport;
    }

    public boolean isEncryptMessage() {
        return encryptMessage;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public String getKeystoreFilePath() {
        return keystoreFilePath;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }
}
