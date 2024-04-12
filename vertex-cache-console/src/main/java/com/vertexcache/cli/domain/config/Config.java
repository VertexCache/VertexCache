package com.vertexcache.cli.domain.config;

import com.vertexcache.common.cli.CommandLineArgsParser;
import com.vertexcache.common.config.ConfigBase;
import com.vertexcache.common.config.reader.PropertiesLoader;
import com.vertexcache.common.log.LogUtil;
import com.vertexcache.common.security.KeyPairHelper;

import java.security.PrivateKey;
import java.security.PublicKey;

public class Config extends ConfigBase {

    public static final String APP_NAME = "VertexCache Console";
    private boolean configLoaded = false;
    private boolean configError = false;
    private String configFilePath;

    private boolean encryptMessage = false;
    private PublicKey publicKey;

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

                // Encrypt Message Layer
                if (propertiesLoader.isExist(ConfigKey.ENABLE_ENCRYPT_MESSAGE) && Boolean.parseBoolean(propertiesLoader.getProperty(ConfigKey.ENABLE_ENCRYPT_MESSAGE))) {
                    this.encryptMessage = true;
                    this.publicKey = KeyPairHelper.decodePublicKey(propertiesLoader.getProperty(ConfigKey.PUBLIC_KEY));
                }

                // Encrypt Transport Layer
                //if (propertiesLoader.isExist(ConfigKey.ENABLE_ENCRYPT_TRANSPORT) && Boolean.parseBoolean(propertiesLoader.getProperty(ConfigKey.ENABLE_ENCRYPT_TRANSPORT))) {
                //    this.encryptTransport = true;
                //    this.keystoreFilePath = propertiesLoader.getProperty(ConfigKey.KEYSTORE_FILEPATH);
               //     this.keystorePassword = propertiesLoader.getProperty(ConfigKey.KEYSTORE_PASSWORD);
               // }
            }

        } catch (Exception exception) {
            this.configLoaded = false;
            this.configError = true;
        }
    }
}
