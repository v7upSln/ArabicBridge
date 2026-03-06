package mas.minecraft.foxs.arabicFixer;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.geysermc.floodgate.api.FloodgateApi;

public class ChatHandler implements Listener {
    private final Plugin plugin;
    private boolean floodgateAvailable = false;
    
    private static final int SHAPE_OPTIONS = 8 | 4 | 16;
    private final ArabicShaping arabicShaping;

    public ChatHandler(Plugin plugin) {
        this.plugin = plugin;
        
        this.arabicShaping = new ArabicShaping(SHAPE_OPTIONS);
        
        if (Bukkit.getPluginManager().getPlugin("floodgate") != null) {
            this.floodgateAvailable = true;
            plugin.getLogger().info("Floodgate detected! Bedrock players will see original Arabic.");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        String originalMessage = PlainTextComponentSerializer.plainText().serialize(event.message());
        
        if (!isArabicOnly(originalMessage)) return;

        String shaped = reshapeArabic(originalMessage);
        String javaFormatted = reverse(shaped);
        String bedrockFormatted = originalMessage;

        if (!floodgateAvailable) {
            Component transformedComponent = transformComponentPreservingStyle(event.message(), javaFormatted);
            event.message(transformedComponent);
            return;
        }

        ChatRenderer originalRenderer = event.renderer();
        event.renderer((source, sourceDisplayName, messageComponent, viewer) -> {
            boolean isBedrock = false;
            if (viewer instanceof Player) {
                isBedrock = FloodgateApi.getInstance().isFloodgatePlayer(((Player) viewer).getUniqueId());
            }
            
            String formattedText = isBedrock ? bedrockFormatted : javaFormatted;
            Component newMessage = transformComponentPreservingStyle(messageComponent, formattedText);
            
            return originalRenderer.render(source, sourceDisplayName, newMessage, viewer);
        });
    }

    private String reshapeArabic(String input) {
        try {
            return arabicShaping.shape(input);
        } catch (ArabicShapingException e) {
            plugin.getLogger().warning("Arabic shaping failed: " + e.getMessage());
            return input;
        }
    }

    private String reverse(String input) {
        return new StringBuilder(input).reverse().toString();
    }

    private boolean isArabicOnly(String text) {
        return text.matches("^[\\u0600-\\u06FF\\u0750-\\u077F\\u08A0-\\u08FF\\uFB50-\\uFDFF\\uFE70-\\uFEFF0-9\\s\\p{Punct}]+$");
    }

    private Component transformComponentPreservingStyle(Component original, String newText) {
        return original.replaceText(builder -> 
            builder.matchLiteral(PlainTextComponentSerializer.plainText().serialize(original))
                   .replacement(newText)
        );
    }
}
