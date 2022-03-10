package org.jabref.preferences;

import com.github.javakeyring.BackendNotSupportedException;
import com.github.javakeyring.Keyring;
import com.github.javakeyring.PasswordAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecretStore {

    private static final Logger logger = LoggerFactory.getLogger(SecretStore.class);
    private static final String PREFIX = "org.jabref";

    public String get(String service) throws PasswordAccessException {
        try (Keyring keyring = createKeyring()) {
            return keyring.getPassword(getServiceName(service), "");
        } catch (PasswordAccessException p) {
            throw p;
        } catch (Exception e) {
            throw new RuntimeException("Encountered problem with keyring: " + e.getMessage(), e);
        }
    }

    public void put(String key, String password) throws PasswordAccessException {
        if (password.isEmpty()) {
            throw new IllegalArgumentException("Password must not be empty");
        }

        try (Keyring keyring = createKeyring()) {
            keyring.setPassword(getServiceName(key), "", password);
            logger.info("Successfully set password");
        } catch (PasswordAccessException p) {
            throw p;
        } catch (Exception e) {
            throw new RuntimeException("Encountered problem with keyring: " + e.getMessage(), e);
        }
    }

    public void delete(String key) throws PasswordAccessException {
        try (Keyring keyring = createKeyring()) {
            keyring.deletePassword(getServiceName(key), "");
            logger.info("Successfully deleted password");
        } catch (PasswordAccessException p) {
            throw p;
        } catch (Exception e) {
            throw new RuntimeException("Encountered problem with keyring: " + e.getMessage(), e);
        }
    }

    private Keyring createKeyring() {
        try {
            return Keyring.create();
        } catch (BackendNotSupportedException e) {
            throw new RuntimeException("Could not load native keyring: " + e.getMessage(), e);
        }
    }

    private String getServiceName(String key) {
        return PREFIX + ":" + key;
    }
}
