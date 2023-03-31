package io.github.rudynakodach.rudysuntitledtaggame.Modules.PowerUps.ItInclusive;

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

public class PlayerPuller implements PowerUp {

    private final JavaPlugin plugin;
    /**
     * Delay for the {@code PlayerPuller} item.
     * Provided in milliseconds.
     */
    public static long PLAYER_PULLER_DELAY = 12500;
    public PlayerPuller(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull ItemStack getItem() {
        ItemStack playerPullerStack = new ItemStack(Material.GOLDEN_HOE, 1);
        ItemMeta playerPullerMeta = playerPullerStack.getItemMeta();

        playerPullerMeta.setCustomModelData(2137);
        playerPullerMeta.displayName(Component.text("Przyciongacz").color(NamedTextColor.RED).decorate(TextDecoration.BOLD, TextDecoration.ITALIC));

        PersistentDataContainer dataContainer = playerPullerMeta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "przyciongacz");
        dataContainer.set(key, PersistentDataType.INTEGER, 1);

        playerPullerStack.setItemMeta(playerPullerMeta);
        return playerPullerStack;
    }
}
