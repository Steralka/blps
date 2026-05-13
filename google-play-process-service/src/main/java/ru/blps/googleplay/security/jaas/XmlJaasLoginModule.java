package ru.blps.googleplay.security.jaas;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.blps.googleplay.security.RolePrivileges;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class XmlJaasLoginModule implements LoginModule {

    private static final PasswordEncoder PASSWORD_ENCODER = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map<String, ?> options;

    private XmlUserStore userStore;
    private String username;
    private boolean succeeded;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.options = options;
        this.userStore = new XmlUserStore();
        String usersXml = (String) options.get("usersXml");
        this.userStore.load(usersXml);
    }

    @Override
    public boolean login() throws LoginException {
        NameCallback nameCallback = new NameCallback("username");
        PasswordCallback passwordCallback = new PasswordCallback("password", false);
        try {
            callbackHandler.handle(new Callback[]{nameCallback, passwordCallback});
        } catch (Exception e) {
            throw (LoginException) new LoginException("Failed to obtain credentials").initCause(e);
        }

        username = nameCallback.getName();
        char[] passwordChars = passwordCallback.getPassword();
        String password = passwordChars == null ? "" : new String(passwordChars);

        XmlUserRecord record = userStore.findByUsername(username);
        if (record == null) {
            throw new FailedLoginException("Invalid username or password");
        }
        if (!PASSWORD_ENCODER.matches(password, record.password())) {
            throw new FailedLoginException("Invalid username or password");
        }

        succeeded = true;
        return true;
    }

    @Override
    public boolean commit() throws LoginException {
        if (!succeeded) {
            return false;
        }

        XmlUserRecord record = userStore.findByUsername(username);
        if (record == null) {
            return false;
        }

        subject.getPrincipals().add(new UserPrincipal(username));

        List<String> roles = record.roles();
        for (String role : roles) {
            subject.getPrincipals().add(new AuthorityPrincipal(role));
        }

        Set<String> impliedPrivileges = RolePrivileges.privilegesForRoles(roles);
        for (String priv : impliedPrivileges) {
            subject.getPrincipals().add(new AuthorityPrincipal(priv));
        }

        for (String priv : record.privileges()) {
            subject.getPrincipals().add(new AuthorityPrincipal(priv));
        }

        return true;
    }

    @Override
    public boolean abort() {
        logoutQuiet();
        return true;
    }

    @Override
    public boolean logout() {
        logoutQuiet();
        return true;
    }

    private void logoutQuiet() {
        succeeded = false;
        username = null;
        if (subject != null) {
            subject.getPrincipals().removeIf(p -> p instanceof UserPrincipal || p instanceof AuthorityPrincipal);
        }
    }
}

