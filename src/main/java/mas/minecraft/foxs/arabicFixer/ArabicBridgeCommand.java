package mas.minecraft.foxs.arabicFixer;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ArabicBridgeCommand implements CommandExecutor {
    private final boolean floodgateAvailable;

    public ArabicBridgeCommand() {
        this.floodgateAvailable = Bukkit.getPluginManager().getPlugin("floodgate") != null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(Component.text("ArabicBridge v1.3", NamedTextColor.GOLD)
                .append(Component.text(" by 7UPf", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text(
                floodgateAvailable
                        ? "Floodgate detected — Bedrock gets clean text, Java gets fixed."
                        : "No Floodgate — everyone gets fixed Arabic.",
                NamedTextColor.GRAY));
        return true;
    }
}