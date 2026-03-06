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
        sender.sendMessage(Component.text("ArabicBridge ", NamedTextColor.GOLD)
                .append(Component.text("v1.2", NamedTextColor.WHITE)));
        sender.sendMessage(Component.text("Author: ", NamedTextColor.GRAY)
                .append(Component.text("7UPf", NamedTextColor.AQUA)));
        sender.sendMessage(Component.text("Detection: ", NamedTextColor.YELLOW)
                .append(Component.text("Arabic-only messages are processed; mixed text untouched.", NamedTextColor.WHITE)));
        sender.sendMessage(Component.text("Reshaping: ", NamedTextColor.YELLOW)
                .append(Component.text("Letters are contextualized for proper joining.", NamedTextColor.WHITE)));
        sender.sendMessage(Component.text("Reversal: ", NamedTextColor.YELLOW)
                .append(Component.text("Text reversed for right-to-left display on Java.", NamedTextColor.WHITE)));
        if (floodgateAvailable) {
            sender.sendMessage(Component.text("Platform: ", NamedTextColor.YELLOW)
                    .append(Component.text("Bedrock sees original text; Java sees reshaped+reversed.", NamedTextColor.WHITE)));
        } else {
            sender.sendMessage(Component.text("Platform: ", NamedTextColor.YELLOW)
                    .append(Component.text("Floodgate not found; all players see reshaped+reversed.", NamedTextColor.WHITE)));
        }
        sender.sendMessage(Component.text("Example: ", NamedTextColor.YELLOW)
                .append(Component.text("الله -> ﷲ", NamedTextColor.WHITE)));
        sender.sendMessage(Component.text("Usage: ", NamedTextColor.GRAY)
                .append(Component.text("/arabicbridge", NamedTextColor.WHITE)));
        return true;
    }
}
