package io.github.rudynakodach.rudyshotpotato.Modules;

import io.github.rudynakodach.rudyshotpotato.Modules.DeathEffects.FireworkDeath;
import io.github.rudynakodach.rudyshotpotato.Modules.DeathEffects.RemoteDetonatorDeath;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static io.github.rudynakodach.rudyshotpotato.RudysHotPotato.*;

public class GameController {
    public List<Player> spectatorList = new ArrayList<>();
    public List<Player> playersAlive;
    private final List<Player> playerList = new ArrayList<>();
    private final JavaPlugin plugin;
    public Player playerToKill;
    private boolean isEnabled = true;
    public BukkitTask mainAction;
    public boolean isWarmup = true;
    int countdown = -1;
    private final Location startingPosition;
    private final World world;
    private final int delay;
    public boolean isAwaitingExecution = false;
    /*
    * WORLD BORDER VALUES
    */
    private final Location oldCenter;
    private final double oldSize;
    private final int oldWarningDistance;
    public GameController(JavaPlugin plugin, Player initiator, int delay, int borderSize) {
        this.delay = delay;
        isGameOn = true;
        startingPosition = initiator.getLocation();
        this.world = initiator.getWorld();

        Collection<?> e = plugin.getServer().getOnlinePlayers();
        playersAlive = e.stream()
                .filter(Player.class::isInstance)
                .map(Player.class::cast)
                .collect(Collectors.toList());

        playerList.addAll(playersAlive);

        this.plugin = plugin;

        WorldBorder border = world.getWorldBorder();
        oldCenter = border.getCenter();
        oldSize = border.getSize();
        oldWarningDistance = border.getWarningDistance();

        int x = initiator.getLocation().getBlockX();
        int z = initiator.getLocation().getBlockZ();

        /*plugin.getServer().dispatchCommand(
                plugin.getServer().getConsoleSender(),
                String.format("worldborder center %d %d", x, z)
        );
        plugin.getServer().dispatchCommand(
                plugin.getServer().getConsoleSender(),
                String.format("worldborder set %d", borderSize));*/

        world.getWorldBorder().setCenter(x, z);
        world.getWorldBorder().setSize(borderSize * 2);
        world.getWorldBorder().setWarningDistance(10);

        teleportAll();
        //roundCountdown(false);
        sendStartMessage();
        assignRandomIT(RandomAssignReason.GAME_BEGIN);
        addGlowing();
        Main();
    }

    private void roundCountdown(boolean isNewRound) {
        try {
            isWarmup = true;
            plugin.getServer().broadcast(Component.text((isNewRound ? "Następna runda " : "Gra ") + "rozpocznie się za ").append(Component.text("3...").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD)));
            Thread.sleep(1000);
            plugin.getServer().broadcast(Component.text("2...").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD));
            Thread.sleep(1000);
            plugin.getServer().broadcast(Component.text("1...").color(NamedTextColor.RED).decorate(TextDecoration.BOLD));
            Thread.sleep(1000);
            isWarmup = false;
        } catch (InterruptedException e) {
            plugin.getLogger().log(Level.WARNING, "Exception occurred when executing roundCountdown: " + e.getMessage());
        }
    }

    private void sendStartMessage() {
        for (Player p : playersAlive) {
            p.playSound(p, Sound.EVENT_RAID_HORN, 2, .7F);
            p.sendTitle(ChatColor.GREEN + "Niech gra się rozpocznie!", ChatColor.ITALIC + "" + ChatColor.DARK_GRAY + "Wygra tylko najlepszy...");
        }
    }

    public void addGlowing() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        Team runners;
        //only if the team already exists
        if(scoreboard.getTeam("runners") == null) {
            runners = scoreboard.registerNewTeam("runners");
        } else {
            runners = scoreboard.getTeam("runners");
        }
        runners.setColor(ChatColor.AQUA);

        Team IT;
        if(scoreboard.getTeam("it") == null) {
            IT = scoreboard.registerNewTeam("it");
        } else {
            IT = scoreboard.getTeam("it");
        }
        IT.setColor(ChatColor.RED);

        PotionEffectType glowingType = PotionEffectType.GLOWING;
        PotionEffect glowingEffect = new PotionEffect(glowingType, 2, 0, false, false);

        for(Player p : playersAlive) {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), String.format("team leave it %s", p.getName()));
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), String.format("team leave runners %s", p.getName()));
            p.addPotionEffect(glowingEffect);
            if(p.getName().equalsIgnoreCase(playerToKill.getName())) {
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), String.format("team join it %s", p.getName()));
            } else {
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), String.format("team leave runners %s", p.getName()));
            }
        }
    }

    public void assignRandomIT(RandomAssignReason reason) {
        Random rand = new Random();
        playerToKill = playersAlive.get(rand.nextInt(playersAlive.size()));
        if (reason == RandomAssignReason.GAME_BEGIN) {
            playerToKill.sendMessage(Component.text("Zaczynasz jako goniący...").color(NamedTextColor.RED).decorate(TextDecoration.ITALIC));
        } else if(reason == RandomAssignReason.NEXT_ROUND) {
            playerToKill.sendMessage(Component.text("Będziesz nowym goniącym w tej rundzie!").color(NamedTextColor.RED).decorate(TextDecoration.ITALIC));
        } else if(reason == RandomAssignReason.PREVIOUS_RUNNER_DIED) {
            plugin.getServer().broadcast(Component.text("Poprzedni goniący umarł! Wybieranie nowej ofiary losu..."));
            playerToKill.sendMessage(Component.text("Zostałeś wybrany jako nowa ofiara losu... Gonisz.").color(NamedTextColor.RED).decorate(TextDecoration.ITALIC));
        }

        restartTask();
    }

    public void teleportAll() {
        for (Player p : playersAlive) {
            p.teleport(startingPosition);
        }
    }

    public void eliminatePlayer(Player playerToEliminate) {
        plugin.getLogger().log(Level.INFO, "Eliminating player " + playerToEliminate.getName());
        playerToEliminate.setGameMode(GameMode.SPECTATOR);
        spectatorList.add(playerToEliminate);
        playersAlive.remove(playerToEliminate);

        playerToEliminate.sendTitle(ChatColor.RED + "Zostałeś wyeliminowany!", "Możesz oglądać innych jak umierają.", 6, 12*4, 24);
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            p.playSound(p, Sound.ENTITY_ENDER_DRAGON_GROWL, 2, .9F);
            if(!p.getName().equalsIgnoreCase(playerToEliminate.getName())) {
                p.sendTitle(
                        ChatColor.YELLOW + playerToEliminate.getName() + ChatColor.RED + " został wyeliminowany.",
                        String.format("Pozostaje %d graczy...", playersAlive.size()),
                        3,
                        24 * 3,
                        12);
            }
        }
        if(playersAlive.size() > 1) {
            assignRandomIT(RandomAssignReason.NEXT_ROUND);
            //roundCountdown(true);
            teleportAll();
        } else {
            stopGame(StopReason.GAME_END);
        }
    }

    public void stopGame(StopReason reason) {
        if(reason == StopReason.GAME_END) {
            plugin.getServer().broadcast(Component.text("Koniec gry.").decorate(TextDecoration.BOLD));
            if(playersAlive.size() > 0) {
                Player winner = playersAlive.get(0);
                plugin.getServer().broadcast(Component.text("Wygrywa " + winner.getName() + "!!!").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD));
            } else {
                plugin.getServer().broadcast(Component.text("Nie wyłoniono zwycięzcy...").decorate(TextDecoration.ITALIC));
            }
            if(mainAction != null) {
                mainAction.cancel();
            }
            isEnabled = false;
            isGameOn = false;

        } else if(reason == StopReason.STOPPED) {
            plugin.getServer().broadcast(Component.text("Gra zatrzymana.").decorate(TextDecoration.BOLD));
            isEnabled = false;
            isGameOn = false;
            if(mainAction != null) {
                mainAction.cancel();
            }
        }

        for (Player p : spectatorList) {
            p.setGameMode(GameMode.SURVIVAL);
        }

        world.getWorldBorder().setCenter(oldCenter);
        world.getWorldBorder().setWarningDistance(oldWarningDistance);
        world.getWorldBorder().setSize(oldSize);

    }

    public void setPlayerToKill(Player player) {
        playerToKill = player;
    }

    public void Main() {
        mainAction = new BukkitRunnable() {
            @Override
            public void run() {
                if(!isEnabled) {this.cancel();}

                if(isEnabled || !isGameOn) {
                    plugin.getServer().broadcast(Component.text("Życie " + playerToKill.getName() + " dobiegło końca!").color(NamedTextColor.RED));
                    Random rand = new Random();
                    int deathEffect = rand.nextInt(0, 2);
                    if(deathEffect == 1) {
                        new FireworkDeath(plugin, playerToKill, GameController.this);
                    } else {
                        new RemoteDetonatorDeath(plugin, playerToKill, GameController.this);
                    }
                }
            }
        }.runTaskTimer(plugin, 24L * delay, 24L * delay + 12 * 14);
    }

    public void restartTask() {
        mainAction.cancel();
        mainAction = new BukkitRunnable() {
            @Override
            public void run() {
                if(!isEnabled) {this.cancel();}

                if(isEnabled || !isGameOn) {
                    plugin.getServer().broadcast(Component.text("Życie " + playerToKill.getName() + " dobiegło końca!").color(NamedTextColor.RED));
                    Random rand = new Random();
                    int deathEffect = rand.nextInt(0, 2);
                    switch (deathEffect) {
                        case 0 -> new FireworkDeath(plugin, playerToKill, GameController.this);
                        case 1 -> new RemoteDetonatorDeath(plugin,playerToKill, GameController.this);
                    }
                }
            }
        }.runTaskTimer(plugin, 24L * delay, 24L * delay + 12 * 14);
    }
}
