package mas.minecraft.foxs.arabicFixer;

import org.bukkit.plugin.java.JavaPlugin;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new ChatHandler(this), this);
        getLogger().info("ArabicBridge enabled!");
        getLogger().info("Version: " + getDescription().getVersion());
        getLogger().info("Server: " + getServer().getVersion());
        getLogger().info("Initialization status: OK");
        if (getCommand("arabicbridge") != null) {
            getCommand("arabicbridge").setExecutor(new ArabicBridgeCommand());
        }
        getServer().getScheduler().runTaskAsynchronously(this, this::checkForUpdates);
    }

    @Override
    public void onDisable() {
        getLogger().info("ArabicBridge disabled.");
    }

    private void checkForUpdates() {
        String current = getDescription().getVersion();
        try {
            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.github.com/repos/v7upSln/ArabicBridge/releases/latest"))
                    .header("Accept", "application/vnd.github+json")
                    .header("User-Agent", "ArabicBridge/" + current)
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() != 200) {
                getLogger().info("Update check: unavailable (" + response.statusCode() + ")");
                return;
            }
            String body = new String(response.body(), StandardCharsets.UTF_8);
            String remote = extractVersion(body);
            if (remote == null || remote.isEmpty()) {
                getLogger().info("Update check: could not parse latest version");
                return;
            }
            String normCurrent = normalizeVersion(current);
            String normRemote = normalizeVersion(remote);
            int cmp = compareVersions(normCurrent, normRemote);
            if (cmp < 0) {
                getLogger().info("Update available: " + current + " -> " + remote + " at https://github.com/v7upSln/ArabicBridge/releases/latest");
            } else if (cmp == 0) {
                getLogger().info("You are running the latest version: " + current);
            } else {
                getLogger().info("Running a newer version: " + current + " (latest: " + remote + ")");
            }
        } catch (Exception e) {
            getLogger().info("Update check failed: " + e.getMessage());
        }
    }

    private String extractVersion(String json) {
        Pattern tagPattern = Pattern.compile("\"tag_name\"\\s*:\\s*\"([^\"]+)\"");
        Matcher m = tagPattern.matcher(json);
        if (m.find()) return m.group(1);
        Pattern namePattern = Pattern.compile("\"name\"\\s*:\\s*\"([^\"]+)\"");
        Matcher m2 = namePattern.matcher(json);
        if (m2.find()) return m2.group(1);
        return null;
    }

    private String normalizeVersion(String v) {
        String s = v.trim();
        if (s.startsWith("v") || s.startsWith("V")) s = s.substring(1);
        return s.replaceAll("[^0-9.]", "");
    }

    private int compareVersions(String a, String b) {
        String[] aa = a.split("\\.");
        String[] bb = b.split("\\.");
        int n = Math.max(aa.length, bb.length);
        for (int i = 0; i < n; i++) {
            int ai = i < aa.length ? parseIntSafe(aa[i]) : 0;
            int bi = i < bb.length ? parseIntSafe(bb[i]) : 0;
            if (ai != bi) return Integer.compare(ai, bi);
        }
        return 0;
    }

    private int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
