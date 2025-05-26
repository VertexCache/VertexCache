/*
 * Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vertexcache.console.domain.config;

import com.vertexcache.common.cli.CommandLineArgsParser;
import com.vertexcache.common.config.ConfigBase;
import com.vertexcache.common.config.VertexCacheConfigException;
import com.vertexcache.common.config.reader.ConfigLoader;
import com.vertexcache.common.config.reader.ConfigLoaderFactory;
import com.vertexcache.common.log.LogHelper;
import com.vertexcache.common.security.EncryptionMode;
import com.vertexcache.common.security.KeyPairHelper;

import java.security.PublicKey;

public class Config extends ConfigBase {

    public static final String APP_NAME = "VertexCache Console";
    private boolean configLoaded = false;
    private boolean configError = false;
    private String configFilePath;

    private String clientId = ConfigKey.CLIENT_ID_DEFAULT;
    private String clientToken = null;

    private String serverHost = ConfigKey.SERVER_HOST_DEFAULT;
    private int serverPort = ConfigKey.SERVER_PORT_DEFAULT;

    private EncryptionMode encryptionMode = EncryptionMode.NONE;
    private boolean encryptWithPublicKey = false;
    private boolean encryptWithSharedKey = false;
    private PublicKey publicKey;
    private String sharedEncryptionKey;
    private String encryptNote = "";

    private boolean encryptTransport = false;
    private boolean verifyTLSCertificate = false;
    private String tlsCertificate;

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

                // Client ID
                if (configLoader.isExist(ConfigKey.CLIENT_ID)) {
                    this.clientId = configLoader.getProperty(ConfigKey.CLIENT_ID);
                }

                // Client Token
                if (configLoader.isExist(ConfigKey.CLIENT_TOKEN)) {
                    this.clientToken = configLoader.getProperty(ConfigKey.CLIENT_TOKEN);
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
                if (configLoader.isExist(ConfigKey.ENABLE_ENCRYPT_MESSAGE)
                        && Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_ENCRYPT_MESSAGE))) {
                    try {

                        String publicKeyString =  configLoader.getProperty(ConfigKey.PUBLIC_KEY);
                        this.sharedEncryptionKey = configLoader.getProperty(ConfigKey.SHARED_ENCRYPTION_KEY);

                        boolean hasPublicKey = publicKeyString != null && !publicKeyString.isBlank();
                        boolean hasSharedKey = sharedEncryptionKey != null && !sharedEncryptionKey.isBlank();

                        if (hasPublicKey && hasSharedKey) {
                            this.encryptNote = ", Only one of 'public_key' or 'shared_encryption_key' may be set when 'enable_encrypt_message=true'";
                            LogHelper.getInstance().logWarn("Only one of 'public_key' or 'shared_encryption_key' may be set when 'enable_encrypt_message=true'");
                            throw new VertexCacheConfigException("Only one of 'public_key' or 'shared_encryption_key' may be set when 'enable_encrypt_message=true'");
                        }

                        if (!hasPublicKey && !hasSharedKey) {
                            this.encryptNote = ", Missing encryption configuration: you must set either 'public_key' or 'shared_encryption_key' when 'enable_encrypt_message=true'";
                            LogHelper.getInstance().logWarn("Missing encryption configuration: you must set either 'public_key' or 'shared_encryption_key' when 'enable_encrypt_message=true'");
                            throw new VertexCacheConfigException("Missing encryption configuration: you must set either 'public_key' or 'shared_encryption_key' when 'enable_encrypt_message=true'");
                        }


                        if(hasPublicKey) {
                            this.publicKey = KeyPairHelper.loadPublicKey(configLoader.getProperty(ConfigKey.PUBLIC_KEY));
                            this.sharedEncryptionKey = null;
                            this.encryptWithPublicKey = true;
                            this.encryptionMode = EncryptionMode.ASYMMETRIC;
                        }

                        if(hasSharedKey) {
                            this.publicKey = null;
                            this.encryptWithSharedKey = true;
                            this.encryptionMode = EncryptionMode.SYMMETRIC;
                        }

                    } catch (Exception e) {
                        // should be already false
                        this.encryptionMode = EncryptionMode.NONE;
                    }
                }

                // Encrypt Transport Layer
                if (configLoader.isExist(ConfigKey.ENABLE_ENCRYPT_TRANSPORT) && Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_ENCRYPT_TRANSPORT))) {
                   this.encryptTransport = true;

                    if (configLoader.isExist(ConfigKey.ENABLE_VERIFY_TLS_CERTIFICATE) && Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_VERIFY_TLS_CERTIFICATE))) {
                        this.verifyTLSCertificate = true;
                        if (configLoader.isExist(ConfigKey.TLS_CERTIFICATE)) {
                            this.tlsCertificate = configLoader.getProperty(ConfigKey.TLS_CERTIFICATE);
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

    public String getClientId() { return clientId; }

    public String getClientToken() { return clientToken; }

    public String getServerHost() {
        return serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    public EncryptionMode getEncryptionMode() {
        return encryptionMode;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public String getSharedEncryptionKey() {
        return sharedEncryptionKey;
    }

    public boolean isEncryptWithPublicKey() {
        return encryptWithPublicKey;
    }

    public boolean isEncryptWithSharedKey() {
        return encryptWithSharedKey;
    }

    public String getEncryptNote() {
        return this.encryptNote;
    }

    public boolean isEncryptTransport() {
        return encryptTransport;
    }

    public boolean isVerifyTLSCertificate() {
        return verifyTLSCertificate;
    }

    public String getTlsCertificate() { return tlsCertificate; }
}
