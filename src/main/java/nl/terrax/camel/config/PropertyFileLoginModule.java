package nl.terrax.camel.config;

import org.eclipse.jetty.jaas.spi.AbstractLoginModule;
import org.eclipse.jetty.jaas.spi.UserInfo;
import org.eclipse.jetty.security.PropertyUserStore;
import org.eclipse.jetty.server.UserIdentity;
import org.eclipse.jetty.util.security.Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Sample PropertyFileLoginModule.
 */
public class PropertyFileLoginModule extends AbstractLoginModule {
    public static final String DEFAULT_FILENAME = "realm.properties";

    private static final Logger LOG = LoggerFactory.getLogger(PropertyFileLoginModule.class);
    private static final ConcurrentHashMap<String, PropertyUserStore> PROPERTY_USERSTORES = new ConcurrentHashMap<>();

    private boolean hotReload = false;
    private String filename = null;

    /**
     * Read contents of the configured property file.
     *
     * @param subject - the Subject to be authenticated
     * @param callbackHandler - a CallbackHandler for communicating with the end user (prompting for usernames and passwords, for example)
     * @param sharedState - state shared with other configured LoginModules
     * @param options - options specified in the login Configuration for this particular LoginModule
     * @see javax.security.auth.spi.LoginModule#initialize(Subject, CallbackHandler, Map, Map)
     */
    @Override
    public void initialize(final Subject subject, final CallbackHandler callbackHandler,
                           final Map<String, ?> sharedState, final Map<String, ?> options) {
        super.initialize(subject, callbackHandler, sharedState, options);
        setupPropertyUserStore(options);
    }

    private void setupPropertyUserStore(final Map<String, ?> options) {
        parseConfig(options);

        if (PROPERTY_USERSTORES.get(filename) == null) {
            final PropertyUserStore propertyUserStore = new PropertyUserStore();
            propertyUserStore.setConfig(filename);
            propertyUserStore.setHotReload(hotReload);

            final PropertyUserStore prev = PROPERTY_USERSTORES.putIfAbsent(filename, propertyUserStore);
            if (prev == null) {
                LOG.info("setupPropertyUserStore: Starting new PropertyUserStore. PropertiesFile: {} hotReload: {}"
                        , filename, hotReload);

                try {
                    propertyUserStore.start();
                } catch (Exception e) {
                    LOG.warn("Exception while starting propertyUserStore: ", e);
                }
            }
        }
    }

    private void parseConfig(final Map<String, ?> options) {
        String tmp = (String) options.get("file");
        filename = (tmp == null ? DEFAULT_FILENAME : tmp);
        filename = System.getProperty("login.file", filename);
        tmp = (String) options.get("hotReload");
        hotReload = tmp == null ? hotReload : Boolean.parseBoolean(tmp);
    }

    @Override
    public UserInfo getUserInfo(final String userName) {
        final PropertyUserStore propertyUserStore = PROPERTY_USERSTORES.get(filename);
        if (propertyUserStore == null) {
            throw new IllegalStateException("PropertyUserStore should never be null here!");
        }

        LOG.trace("Checking PropertyUserStore {} for {}", filename, userName);
        final UserIdentity userIdentity = propertyUserStore.getUserIdentity(userName);
        if (userIdentity == null) {
            return null;
        }

        final Set<Principal> principals = userIdentity.getSubject().getPrincipals();
        final List<String> roles = new ArrayList<>();
        for (final Principal principal : principals) {
            roles.add(principal.getName());
        }

        final Credential credential = (Credential) userIdentity.getSubject().getPrivateCredentials().iterator().next();
        LOG.trace("Found: {} in PropertyUserStore {}", userName, filename);
        return new UserInfo(userName, credential, roles);
    }

    @Override
    public boolean logout() {
        return true;
    }
}