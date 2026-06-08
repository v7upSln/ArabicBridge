package mas.minecraft.foxs.arabicFixer;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.geysermc.floodgate.api.FloodgateApi;

public class ChatHandler implements Listener {

    private final Plugin plugin;
    private final ArabicShaping arabicShaping;
    private final boolean floodgateAvailable;

    private static final int SHAPE_OPTIONS =
            ArabicShaping.LETTERS_SHAPE |
            ArabicShaping.LENGTH_GROW_SHRINK;

    public ChatHandler(Plugin plugin) {
        this.plugin = plugin;
        this.arabicShaping = new ArabicShaping(SHAPE_OPTIONS);
        this.floodgateAvailable = Bukkit.getPluginManager().getPlugin("floodgate") != null;

        if (floodgateAvailable) {
            plugin.getLogger().info("Floodgate detected. Bedrock compatibility enabled.");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {

        String original = PlainTextComponentSerializer.plainText().serialize(event.message());

        if (!containsArabic(original)) return;

        String shaped = reshapeArabic(original);
        String reversed = new StringBuilder(shaped).reverse().toString();

        ChatRenderer originalRenderer = event.renderer();

        event.renderer((source, sourceDisplayName, message, viewer) -> {

            boolean isBedrock = false;

            if (floodgateAvailable && viewer instanceof Player player) {
                isBedrock = FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId());
            }

            String finalText = isBedrock ? reversed : shaped;

            Component formattedMessage = Component.text(finalText, NamedTextColor.WHITE);

            Component prefix = Component.text("✦ ", NamedTextColor.GOLD)
                    .append(sourceDisplayName.color(NamedTextColor.AQUA))
                    .append(Component.text(" » ", NamedTextColor.DARK_GRAY));

            return prefix.append(formattedMessage);
        });
    }

    private String reshapeArabic(String text) {
        try {
            return arabicShaping.shape(text);
        } catch (ArabicShapingException e) {
            plugin.getLogger().warning("Arabic shaping failed: " + e.getMessage());
            return text;
        }
    }

    private boolean containsArabic(String text) {
        return text.matches(".*[\\u0600-\\u06FF].*");
    }
}
