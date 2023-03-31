package io.github.rudynakodach.rudysuntitledtaggame.Modules.PowerUps;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class DoubleJump implements PowerUp {
    private final JavaPlugin plugin;
    /**
     * Delay of the double jump power up. Provided in millis.
     */
    public static long DOUBLE_JUMP_DELAY = 5000;
    public DoubleJump(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public @NotNull ItemStack getItem() {
        ItemStack doubleJumpStack = new ItemStack(Material.FEATHER, 1);
        ItemMeta doubleJumpMeta = doubleJumpStack.getItemMeta();

        doubleJumpMeta.setCustomModelData(2137);
        doubleJumpMeta.displayName(Component.text("Króliczy ").append(Component.text("Skok").color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD)));

        PersistentDataContainer dataContainer = doubleJumpMeta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "doublejump");
        dataContainer.set(key, PersistentDataType.INTEGER, 1);

        doubleJumpStack.setItemMeta(doubleJumpMeta);
        return doubleJumpStack;
    }
}
