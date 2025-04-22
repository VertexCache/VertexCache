package com.vertexcache.core.setting;

import com.vertexcache.common.config.VertexCacheConfigException;
import com.vertexcache.common.config.reader.ConfigLoader;
import com.vertexcache.common.log.LogHelper;
import com.vertexcache.common.protocol.EncryptionMode;
import com.vertexcache.common.security.KeyPairHelper;

import java.security.PrivateKey;

public class ConfigSecurity {

    private ConfigLoader configLoader;

    private EncryptionMode encryptionMode = EncryptionMode.NONE;
    private boolean encryptWithPrivateKey = false;
    private boolean encryptWithSharedKey = false;
    private PrivateKey privateKey;

    private String sharedEncryptionKey;
    private String encryptNote = "";

    private boolean encryptTransport = false;
    private String tlsCertificate;
    private String tlsPrivateKey;
    private String tlsKeyStorePassword;


    public void loadFromConfigLoader() {
        if (configLoader.isExist(ConfigKey.ENABLE_ENCRYPT_MESSAGE) && Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_ENCRYPT_MESSAGE))) {
            try {

                String privateKeyString = configLoader.getProperty(ConfigKey.PRIVATE_KEY);
                this.sharedEncryptionKey = configLoader.getProperty(ConfigKey.SHARED_ENCRYPTION_KEY);

                boolean hasPrivateKey = privateKeyString != null && !privateKeyString.isBlank();
                boolean hasSharedKey = sharedEncryptionKey != null && !sharedEncryptionKey.isBlank();

                if (hasPrivateKey && hasSharedKey) {
                    this.encryptNote = ", Only one of 'private_key' or 'shared_encryption_key' may be set when 'enable_encrypt_message=true'";
                    LogHelper.getInstance().logWarn("Only one of 'private_key' or 'shared_encryption_key' may be set when 'enable_encrypt_message=true'");
                    throw new VertexCacheConfigException("Only one of 'private_key' or 'shared_encryption_key' may be set when 'enable_encrypt_message=true'");
                }

                if (!hasPrivateKey && !hasSharedKey) {
                    this.encryptNote = ", Missing encryption configuration: you must set either 'private_key' or 'shared_encryption_key' when 'enable_encrypt_message=true'";
                    LogHelper.getInstance().logWarn("Missing encryption configuration: you must set either 'private_key' or 'shared_encryption_key' when 'enable_encrypt_message=true'");
                    throw new VertexCacheConfigException("Missing encryption configuration: you must set either 'private_key' or 'shared_encryption_key' when 'enable_encrypt_message=true'");
                }

                if (hasPrivateKey) {
                    this.privateKey = KeyPairHelper.loadPrivateKey(privateKeyString);
                    this.sharedEncryptionKey = null;
                    this.encryptWithPrivateKey = true;
                    this.encryptionMode = EncryptionMode.ASYMMETRIC;
                }

                if (hasSharedKey) {
                    this.privateKey = null;
                    this.encryptWithSharedKey = true;
                    this.encryptionMode = EncryptionMode.SYMMETRIC;
                }

            } catch (Exception e) {
                this.encryptionMode = EncryptionMode.NONE;
            }
        }

        // Encrypt Transport Layer
        if (configLoader.isExist(ConfigKey.ENABLE_ENCRYPT_TRANSPORT) && Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_ENCRYPT_TRANSPORT))) {
            if (configLoader.isExist(ConfigKey.TLS_CERTIFICATE) && configLoader.isExist(ConfigKey.TLS_PRIVATE_KEY)) {
                this.tlsCertificate = configLoader.getProperty(ConfigKey.TLS_CERTIFICATE);
                this.tlsPrivateKey = configLoader.getProperty(ConfigKey.TLS_PRIVATE_KEY);
                this.tlsKeyStorePassword = configLoader.getProperty(ConfigKey.TLS_KEY_STORE_PASSWORD);
                this.encryptTransport = true;
            }
        }

    }

    protected void loadEncryptionSettings() {
        if (configLoader.isExist(ConfigKey.ENABLE_ENCRYPT_MESSAGE) &&
                Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_ENCRYPT_MESSAGE))) {
            try {
                String privateKeyString = configLoader.getProperty(ConfigKey.PRIVATE_KEY);
                this.sharedEncryptionKey = configLoader.getProperty(ConfigKey.SHARED_ENCRYPTION_KEY);

                boolean hasPrivateKey = privateKeyString != null && !privateKeyString.isBlank();
                boolean hasSharedKey = sharedEncryptionKey != null && !sharedEncryptionKey.isBlank();

                if (hasPrivateKey && hasSharedKey) {
                    this.encryptNote = ", Only one of 'private_key' or 'shared_encryption_key' may be set when 'enable_encrypt_message=true'";
                    throw new VertexCacheConfigException("Only one of 'private_key' or 'shared_encryption_key' may be set when 'enable_encrypt_message=true'");
                }

                if (!hasPrivateKey && !hasSharedKey) {
                    this.encryptNote = ", Missing encryption configuration";
                    throw new VertexCacheConfigException("Missing encryption configuration");
                }

                if (hasPrivateKey) {
                    this.privateKey = KeyPairHelper.loadPrivateKey(privateKeyString);
                    this.sharedEncryptionKey = null;
                    this.encryptWithPrivateKey = true;
                    this.encryptionMode = EncryptionMode.ASYMMETRIC;
                }

                if (hasSharedKey) {
                    this.privateKey = null;
                    this.encryptWithSharedKey = true;
                    this.encryptionMode = EncryptionMode.SYMMETRIC;
                }

            } catch (Exception e) {
                this.encryptionMode = EncryptionMode.NONE;
            }
        }
    }

    protected void loadTransportSettings() {
        if (configLoader.isExist(ConfigKey.ENABLE_ENCRYPT_TRANSPORT) &&
                Boolean.parseBoolean(configLoader.getProperty(ConfigKey.ENABLE_ENCRYPT_TRANSPORT))) {
            if (configLoader.isExist(ConfigKey.TLS_CERTIFICATE) && configLoader.isExist(ConfigKey.TLS_PRIVATE_KEY)) {
                this.tlsCertificate = configLoader.getProperty(ConfigKey.TLS_CERTIFICATE);
                this.tlsPrivateKey = configLoader.getProperty(ConfigKey.TLS_PRIVATE_KEY);
                this.tlsKeyStorePassword = configLoader.getProperty(ConfigKey.TLS_KEY_STORE_PASSWORD);
                this.encryptTransport = true;
            }
        }
    }

    public void setConfigLoader(ConfigLoader configLoader) {
        this.configLoader = configLoader;
    }


    public EncryptionMode getEncryptionMode() {
        return encryptionMode;
    }

    public void setEncryptionMode(EncryptionMode encryptionMode) {
        this.encryptionMode = encryptionMode;
    }

    public boolean isEncryptWithPrivateKey() {
        return encryptWithPrivateKey;
    }

    public void setEncryptWithPrivateKey(boolean encryptWithPrivateKey) {
        this.encryptWithPrivateKey = encryptWithPrivateKey;
    }

    public boolean isEncryptWithSharedKey() {
        return encryptWithSharedKey;
    }

    public void setEncryptWithSharedKey(boolean encryptWithSharedKey) {
        this.encryptWithSharedKey = encryptWithSharedKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public String getSharedEncryptionKey() {
        return sharedEncryptionKey;
    }

    public void setSharedEncryptionKey(String sharedEncryptionKey) {
        this.sharedEncryptionKey = sharedEncryptionKey;
    }

    public String getEncryptNote() {
        return encryptNote;
    }

    public void setEncryptNote(String encryptNote) {
        this.encryptNote = encryptNote;
    }

    public boolean isEncryptTransport() {
        return encryptTransport;
    }

    public void setEncryptTransport(boolean encryptTransport) {
        this.encryptTransport = encryptTransport;
    }

    public String getTlsCertificate() {
        return tlsCertificate;
    }

    public void setTlsCertificate(String tlsCertificate) {
        this.tlsCertificate = tlsCertificate;
    }

    public String getTlsPrivateKey() {
        return tlsPrivateKey;
    }

    public void setTlsPrivateKey(String tlsPrivateKey) {
        this.tlsPrivateKey = tlsPrivateKey;
    }

    public String getTlsKeyStorePassword() {
        return tlsKeyStorePassword;
    }

    public void setTlsKeyStorePassword(String tlsKeyStorePassword) {
        this.tlsKeyStorePassword = tlsKeyStorePassword;
    }
}
