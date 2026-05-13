package ru.blps.googleplay.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public class AppSecurityProperties {

    private Jwt jwt = new Jwt();
    private String usersXml;

    public Jwt getJwt() {
        return jwt;
    }

    public void setJwt(Jwt jwt) {
        this.jwt = jwt;
    }

    public String getUsersXml() {
        return usersXml;
    }

    public void setUsersXml(String usersXml) {
        this.usersXml = usersXml;
    }

    public static class Jwt {
        private String secret;
        private long ttlSeconds = 3600;

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public long getTtlSeconds() {
            return ttlSeconds;
        }

        public void setTtlSeconds(long ttlSeconds) {
            this.ttlSeconds = ttlSeconds;
        }
    }
}

