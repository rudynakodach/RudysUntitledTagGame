package io.github.rudynakodach.rudysuntitledtaggame.Modules.PowerUps.RunnerInclusive;

import io.github.rudynakodach.rudysuntitledtaggame.Modules.PowerUps.PowerUp;
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

public class Stun implements PowerUp {
    private final JavaPlugin plugin;
    /**
     * Delay of the Stun power-up. Provided in milliseconds.
     */
    public static long STUN_DELAY = 15000;
    public Stun(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull ItemStack getItem() {
        ItemStack stack = new ItemStack(Material.BLAZE_ROD, 1);
        ItemMeta meta = stack.getItemMeta();

        meta.setCustomModelData(2137);
        meta.displayName(Component.text("Pała.").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD));

        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "stun");
        dataContainer.set(key, PersistentDataType.INTEGER, 1);

        stack.setItemMeta(meta);
        return stack;
    }
}
