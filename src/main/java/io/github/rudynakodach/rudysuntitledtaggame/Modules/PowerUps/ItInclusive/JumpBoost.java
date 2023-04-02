package io.github.rudynakodach.rudysuntitledtaggame.Modules.PowerUps.ItInclusive;

import io.github.rudynakodach.rudysuntitledtaggame.Modules.PowerUps.PowerUp;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.format.NamedTextColor.*;

public class JumpBoost implements PowerUp {

    private final JavaPlugin plugin;
    public static long JUMP_BOOST_DELAY = 30000;
    public JumpBoost(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull ItemStack getItem() {
        ItemStack stack = new ItemStack(Material.RABBIT_FOOT, 1);
        ItemMeta meta = stack.getItemMeta();

        meta.displayName(Component.text("Króliczy ").decorate(TextDecoration.ITALIC).append(Component.text("Skok").color(AQUA).append(Component.text(" 2").color(RED).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false))).append(this::getButton));
        meta.setCustomModelData(2137);

        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "jumpboost");
        dataContainer.set(key, PersistentDataType.INTEGER, 1);

        stack.setItemMeta(meta);
        return stack;
    }
}
