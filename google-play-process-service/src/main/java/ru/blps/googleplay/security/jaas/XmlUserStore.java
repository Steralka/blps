package ru.blps.googleplay.security.jaas;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class XmlUserStore {

    private final Map<String, XmlUserRecord> usersByUsername = new ConcurrentHashMap<>();

    public void load(String location) {
        try (InputStream inputStream = open(location)) {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
            NodeList userNodes = document.getDocumentElement().getElementsByTagName("user");
            Map<String, XmlUserRecord> tmp = new ConcurrentHashMap<>();
            for (int i = 0; i < userNodes.getLength(); i++) {
                Element element = (Element) userNodes.item(i);
                String username = element.getAttribute("username");
                String password = element.getAttribute("password");
                List<String> roles = splitCsv(element.getAttribute("roles"));
                List<String> privileges = splitCsv(element.getAttribute("privileges"));
                if (username == null || username.isBlank()) {
                    continue;
                }
                tmp.put(username, new XmlUserRecord(username, password, roles, privileges));
            }
            usersByUsername.clear();
            usersByUsername.putAll(tmp);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read users XML: " + location, e);
        }
    }

    public XmlUserRecord findByUsername(String username) {
        return usersByUsername.get(Objects.requireNonNull(username));
    }

    private static InputStream open(String location) throws Exception {
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("usersXml location is blank");
        }
        if (location.startsWith("classpath:")) {
            String path = location.substring("classpath:".length());
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(stripLeadingSlash(path));
            if (is == null) {
                throw new IllegalStateException("Users XML not found on classpath: " + location);
            }
            return is;
        }
        if (location.startsWith("file:")) {
            return new FileInputStream(location.substring("file:".length()));
        }
        return new FileInputStream(location);
    }

    private static String stripLeadingSlash(String path) {
        if (path.startsWith("/")) {
            return path.substring(1);
        }
        return path;
    }

    private static List<String> splitCsv(String csv) {
        if (csv == null || csv.isBlank()) {
            return List.of();
        }
        return Arrays.stream(csv.split(","))
            .map(String::trim)
            .filter(s -> !s.isBlank())
            .toList();
    }
}
