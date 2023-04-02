package io.github.rudynakodach.rudysuntitledtaggame.Modules.PowerUps;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface PowerUp {


    @NotNull
    default ItemStack getItem() {
        ItemStack stack = new ItemStack(Material.AIR, 0);
        ItemMeta meta = stack.getItemMeta();

        return stack;
    }

    default Component getButton() {
        return Component.empty().append(Component.text(" [").color(NamedTextColor.GRAY))
                .append(Component.text("RMB")
                .color(NamedTextColor.GREEN)
                .decorate(TextDecoration.BOLD))
                .append(Component.text("]").color(NamedTextColor.GRAY).decoration(TextDecoration.BOLD, false));
    }
}
