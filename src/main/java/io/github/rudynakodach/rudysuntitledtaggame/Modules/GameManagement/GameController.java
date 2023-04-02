package io.github.rudynakodach.rudysuntitledtaggame.Modules.GameManagement;

import io.github.rudynakodach.rudysuntitledtaggame.Modules.DeathEffects.*;
import io.github.rudynakodach.rudysuntitledtaggame.Modules.Events.Generic.GameEventHandler;
import io.github.rudynakodach.rudysuntitledtaggame.Modules.Events.RoundStart.RoundStartListeners;
import io.github.rudynakodach.rudysuntitledtaggame.Modules.PowerUps.DoubleJump;
import io.github.rudynakodach.rudysuntitledtaggame.Modules.PowerUps.ItInclusive.JumpBoost;
import io.github.rudynakodach.rudysuntitledtaggame.Modules.PowerUps.ItInclusive.PlayerPuller;
import io.github.rudynakodach.rudysuntitledtaggame.Modules.PowerUps.RunnerInclusive.Stun;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static io.github.rudynakodach.rudysuntitledtaggame.RudysUntitledTagGame.*;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

public class GameController {
    private static GameController instance;

    private final JavaPlugin plugin;
    public List<Player> spectatorList = new ArrayList<>();
    public List<Player> playersAlive;
    public final List<Player> totalPlayers;
    public Player playerToKill;
    private boolean isEnabled = true;
    public BukkitTask mainAction;
    public boolean isWarmup = false;
    private final Location startingPosition;
    private final World world;
    public final int roundDelay;
    public boolean isAwaitingExecution = false;
    private final boolean isTimeLeftVisible;
    private final boolean isGlowColored;
    private Team IT;
    private Team runners;
    /*
    * WORLD BORDER FIELDS
    */
    private final Location oldCenter;
    private final double oldSize;
    private final int oldWarningDistance;
    /**
     * The time it takes between hits to be allowed. Measurred in milliseconds. Equivalent to around {@code 2/3} of a second.
     */
    public static final long HIT_DELAY = 666;
    public final HashMap<Player, Long> hitMap;
    private final ScoreboardController scoreboardController;
    public GameController(@NotNull JavaPlugin plugin, @NotNull Player initiator, int delay, int borderSize, boolean isTimeLeftVisible, boolean isGlowColored) {
        this.hitMap = new HashMap<>();
        this.isTimeLeftVisible = isTimeLeftVisible;
        this.isGlowColored = isGlowColored;
        this.roundDelay = delay;
        startingPosition = initiator.getLocation();
        this.world = initiator.getWorld();

        Collection<?> e = plugin.getServer().getOnlinePlayers();
        playersAlive = e.stream()
                .filter(Player.class::isInstance)
                .map(Player.class::cast)
                .collect(Collectors.toList());
        totalPlayers = e.stream()
                .filter(Player.class::isInstance)
                .map(Player.class::cast)
                .collect(Collectors.toList());
        this.plugin = plugin;

        WorldBorder border = world.getWorldBorder();
        oldCenter = border.getCenter();
        oldSize = border.getSize();
        oldWarningDistance = border.getWarningDistance();

        int x = initiator.getLocation().getBlockX();
        int z = initiator.getLocation().getBlockZ();

        world.getWorldBorder().setCenter(x, z);
        if(world.getWorldBorder().getSize() > borderSize) {
            world.getWorldBorder().setSize(400);
        }
        world.getWorldBorder().setSize(borderSize, 3);
        world.getWorldBorder().setWarningDistance(2);

        this.scoreboardController = new ScoreboardController(this);

        instance = this;
    }

    /**
     * Starts the game.
     */
    public void startGame() {
        preparePlayers();
        prepareTeams();
        isGameOn = true;
        assignRandomIT(RandomAssignReason.GAME_BEGIN);
        scoreboardController.prepareScoreboard();
        scoreboardController.displayToPlayers();
        teleportAll();
        addGlowing();
        startNewRound(false);
        sendStartMessage();
    }

    /**
     * Prepares all players to the game.
     * Clears their inventory,
     */
    private void preparePlayers() {
        DoubleJump doubleJumpPowerup = new DoubleJump(plugin);
        Stun stunPowerup = new Stun(plugin);
        for (Player p : playersAlive) {
            p.setHealth(20);
            p.setFoodLevel(20);
            p.setSaturation(4);

            p.getInventory().clear();
            p.getInventory().setItem(5, doubleJumpPowerup.getItem());
            p.getInventory().setItem(3, stunPowerup.getItem());
        }
    }

    public static GameController getInstance() {
        return instance;
    }

    /**
     * Prepares the teams for colored glow effect.
     * This will do nothing if isGlowingColored is false.
     */
    private void prepareTeams() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        if(scoreboard.getTeam("it") == null) {
            IT = scoreboard.registerNewTeam("it");
        } else {
            IT = scoreboard.getTeam("it");
        }
        assert IT != null;
        IT.prefix(Component.text("[KOŃ] ").color(NamedTextColor.RED).decorate(TextDecoration.BOLD).append(Component.text().color(NamedTextColor.WHITE)));

        if(scoreboard.getTeam("runners") == null) {
            runners = scoreboard.registerNewTeam("runners");
        } else {
            runners = scoreboard.getTeam("runners");
        }

        for (String entry : IT.getEntries()) {
            if(entry != null) {
                IT.removeEntry(entry);
            }
        }

        for (String entry : runners.getEntries()) {
            if(entry != null) {
                runners.removeEntry(entry);
            }
        }
        if(isGlowColored) {
            IT.color(NamedTextColor.RED);
            runners.color(NamedTextColor.BLUE);
        } else {
            IT.color(null);
            runners.color(null);
        }
    }

    /**
     * Puts all players to their respective teams.
     */
    public void rearrangeTeams() {
        for (Player p : playersAlive) {
            IT.removeEntities(p);
            runners.removeEntities(p);
            if(p == playerToKill) {
                IT.addEntities(p);
            } else {
                runners.addEntities(p);
            }
        }
    }


    /**
     * Starts a new round/game with a 3-second countdown in which players are teleported to the initiator's position and makes them unable to move for that time period.
     * @param isStartingNewRound Are we starting the game or a new round?
     */
    private void startNewRound(boolean isStartingNewRound) {
        RoundStartListeners.sendRoundStartEvent();
        GameEventHandler.sendRoundStartEvent(this);
        try {
            //this looks horrible, BUT works
            isWarmup = true;
            plugin.getServer().broadcast(Component.text((isStartingNewRound ? "Następna runda " : "Gra ") + "rozpocznie się za ").append(Component.text("3...").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD)));
            playTickSound(1);
            Thread.sleep(1000);
            playTickSound(1);
            plugin.getServer().broadcast(Component.text("2...").color(YELLOW).decorate(TextDecoration.BOLD));
            Thread.sleep(1000);
            playTickSound(1);
            plugin.getServer().broadcast(Component.text("1...").color(NamedTextColor.RED).decorate(TextDecoration.BOLD));
            Thread.sleep(1000);
            playTickSound(1.5F);
            isWarmup = false;
        } catch (InterruptedException e) {
            plugin.getLogger().log(Level.WARNING, "Exception occurred when executing roundCountdown: " + e.getMessage());
        }
    }

    /**
     * Plays the pling sound to all players of specified pitch. Used during the round countdown.
     * @param pitch The pitch we will be using.
     */
    private void playTickSound(float pitch) {
        for (Player p : playersAlive) {
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 2f, pitch);
        }
    }

    /**
     * Sends the start message to all alive players in-game.
     */
    private void sendStartMessage() {
        for (Player p : playersAlive) {
            p.playSound(p, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2, 2F);
            p.showTitle(Title.title(
                    Component.text("Niech gra się rozpocznie!").color(NamedTextColor.GREEN),
                    Component.text("Wygra tylko najlepszy...").color(NamedTextColor.DARK_GRAY).decorate(TextDecoration.ITALIC),
                    Title.Times.times(
                            java.time.Duration.ofMillis(500),
                            java.time.Duration.ofSeconds(2),
                            java.time.Duration.ofSeconds(3)
                    )
            ));
        }
    }

    /**
     * Adds the glowing effect to all alive players - prevents hiding.
     */
    public void addGlowing() {
        PotionEffectType glowingType = PotionEffectType.GLOWING;
        PotionEffect glowingEffect = new PotionEffect(glowingType, PotionEffect.INFINITE_DURATION, 1, false, false);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : playersAlive) {
                    p.addPotionEffect(glowingEffect);
                }
            }
        }.runTask(plugin);
    }

    /**
     * Assigns a random IT for the game for provided reason.
     * @param reason The reason for new person being chosen as IT.
     * @see RandomAssignReason
     */
    public void assignRandomIT(RandomAssignReason reason) {
        Random rand = new Random();
        Player newIT = playersAlive.get(rand.nextInt(playersAlive.size()));
        setIT(newIT);
        if (reason == RandomAssignReason.GAME_BEGIN) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    playerToKill.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                }
            }.runTask(plugin);
            playerToKill.sendMessage(Component.text("Zaczynasz jako goniący...").color(NamedTextColor.RED).decorate(TextDecoration.ITALIC));
        } else if(reason == RandomAssignReason.NEXT_ROUND) {
            playerToKill.sendMessage(Component.text("Będziesz nowym goniącym w tej rundzie!").color(NamedTextColor.RED).decorate(TextDecoration.ITALIC));
        } else if(reason == RandomAssignReason.PREVIOUS_IT_DIED) {
            plugin.getServer().broadcast(Component.text("Poprzedni goniący umarł! Wybieranie nowej ofiary losu..."));
            playerToKill.sendMessage(Component.text("Zostałeś wybrany jako nowa ofiara losu... Gonisz.").color(NamedTextColor.RED).decorate(TextDecoration.ITALIC));
        }

        rearrangeTeams();
        teleportAll();
        runEliminationTask();
    }

    /**
     * Teleports all players to the initiator's position when starting the game.
     */
    public void teleportAll() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : playersAlive) {
                    p.teleport(startingPosition);
                }
            }
        }.runTask(plugin);
    }
    /**
     * Eliminates the name from the ongoing game.
     * This will enable spectator mode for the specified name.
     * @param target The name we are eliminating from the game
     */
    public void eliminatePlayer(Player target) {
        GameEventHandler.sendPlayerEliminatedEvent(this, target);
        boolean isPlayerDead = target.isDead();
        target.setGameMode(GameMode.SPECTATOR);
        playersAlive.remove(target);
        spectatorList.add(target);

        if(playersAlive.size() > 1) {
            if(isPlayerDead) {
                if(target == playerToKill) {
                    assignRandomIT(RandomAssignReason.PREVIOUS_IT_DIED);
                }
            } else {
                assignRandomIT(RandomAssignReason.NEXT_ROUND);
            }


            for (Player p : plugin.getServer().getOnlinePlayers()) {
                p.playSound(p, Sound.ENTITY_ENDER_DRAGON_GROWL, 2, .9F);
                if (p == target) {
                    target.showTitle(Title.title(
                            Component.text("Odpadasz lol").color(NamedTextColor.RED),
                            Component.text("Możesz oglądać innych jak umierają."),
                            Title.Times.times(
                                    java.time.Duration.ofSeconds(1),
                                    java.time.Duration.ofSeconds(5),
                                    java.time.Duration.ofSeconds(2)
                            )
                    ));
                } else {
                    p.showTitle(Title.title(
                            Component.text(target.getName()).color(NamedTextColor.RED).append(Component.text(" zostaje zrobiona w konia!").color(NamedTextColor.RED)),
                            Component.text(String.format("Pozostaje %d graczy...", playersAlive.size())),
                            Title.Times.times(
                                    java.time.Duration.ofMillis(500),
                                    java.time.Duration.ofSeconds(2),
                                    java.time.Duration.ofSeconds(1)
                            )
                    ));
                }
            }

            plugin.getServer().broadcast(Component.text("Nowa runda niedługo sie zacznie...").color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC));
            if(target == playerToKill) {
                teleportAll();
                startNewRound(true);
                assignRandomIT(RandomAssignReason.NEXT_ROUND);
            }
        } else {
            target.showTitle(Title.title(
                    Component.text("Przegrana").color(NamedTextColor.RED),
                    Component.text("Zostałeś zrobiony w konia."),
                    Title.Times.times(
                            java.time.Duration.ofMillis(500),
                            java.time.Duration.ofSeconds(3),
                            java.time.Duration.ofSeconds(1))
            ));
            stopGame(GameStopReason.GAME_END);
        }
    }

    /**
     * Forcefully eliminates a name from the ongoing game.
     * @param player The name we're going to forcefully eliminate
     * @param method The method we will use to eliminate the specified name
     */
    public void forcefullyStartElimination(Player player, int method) {
        switch (method) {
            case 0 -> new LaunchDeath(plugin, player, GameController.this);
            case 1 -> new RemoteDetonatorDeath(plugin,player, GameController.this);
            case 2 -> new StrikeDeath(plugin, player, GameController.this);
            case 3 -> new FireworkDeath(plugin, player, GameController.this);
            case 4 -> new HorseDeath(plugin, player, GameController.this);
        }
    }

    /**
     * Returns the player back to the game only if they died and were playing.
     * @param player The player to revive.
     */
    public void revivePlayer(Player player) {
        if(!totalPlayers.contains(player)) {
            return;
        }

        if(playersAlive.contains(player)) {
            return;
        }

        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(4);

        new BukkitRunnable() {
            @Override
            public void run() {
                player.teleport(startingPosition);
                player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, PotionEffect.INFINITE_DURATION, 1, false, false));
                player.setGameMode(GameMode.SURVIVAL);

                playersAlive.add(player);
                rearrangeTeams();
                GameEventHandler.sendPlayerReviveEvent(GameController.this, player);
            }
        }.runTask(plugin);
    }

    /**
     * Stops the game for provided reason.
     * @param reason The reason for the game stop.
     * @see GameStopReason
     */
    public void stopGame(GameStopReason reason) {
        GameEventHandler.sendGameEndEvent(this);
        if(reason == GameStopReason.GAME_END) {
            plugin.getServer().broadcast(Component.text("Koniec gry.").decorate(TextDecoration.BOLD));
            if(playersAlive.size() > 0) {
                Player winner = playersAlive.get(0);
                winner.playSound(winner, Sound.ENTITY_PLAYER_LEVELUP, 2, .5F);
                winner.showTitle(Title.title(
                        Component.text("Wygrywasz!").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD),
                        Component.text("Reszta noobów zdechła xd").color(NamedTextColor.WHITE).decorate(TextDecoration.ITALIC),
                        Title.Times.times(
                                java.time.Duration.ofMillis(500),
                                java.time.Duration.ofSeconds(3),
                                java.time.Duration.ofMillis(2)
                        )
                ));
                plugin.getServer().broadcast(Component.text("Wygrywa " + winner.getName() + "!!!").color(YELLOW).decorate(TextDecoration.BOLD));
            } else {
                plugin.getServer().broadcast(Component.text("Nie wyłoniono zwycięzcy...").decorate(TextDecoration.ITALIC));
            }
            if(mainAction != null) {
                mainAction.cancel();
            }
            isEnabled = false;
            isGameOn = false;

        } else if(reason == GameStopReason.STOPPED) {
            plugin.getServer().broadcast(Component.text("Gra zatrzymana.").decorate(TextDecoration.BOLD));
            isEnabled = false;
            isGameOn = false;
            if(mainAction != null) {
                mainAction.cancel();
            }
        }

        for (Player p : totalPlayers) {
            p.getInventory().clear();
            p.setGameMode(GameMode.SURVIVAL);
        }

        world.getWorldBorder().setCenter(oldCenter);
        world.getWorldBorder().setWarningDistance(oldWarningDistance);
        world.getWorldBorder().setSize(oldSize);

        //Remove the teams
        IT.unregister();
        runners.unregister();
    }

    /**
     * Sets the name we will be eliminating from the game after the time runs out.
     * @param player The name to eliminate.
     */
    public void setIT(Player player) {
        if(playerToKill != null) {
            playerToKill.getInventory().setItem(0, new ItemStack(Material.AIR, 1));
            playerToKill.getInventory().setItem(1, new ItemStack(Material.AIR, 1));
        }

        ItemStack puller = new PlayerPuller(plugin).getItem();
        ItemStack jumpBoost = new JumpBoost(plugin).getItem();

        GameEventHandler.sendItChangeEvent(this, player);
        playerToKill = player;

        ItemStack itemInSlot0 = Objects.requireNonNullElse(player.getInventory().getItem(0), new ItemStack(Material.AIR, 1));
        ItemStack itemInSlot1 = Objects.requireNonNullElse(player.getInventory().getItem(1), new ItemStack(Material.AIR, 1));

        playerToKill.getInventory().setItem(0, puller);
        playerToKill.getInventory().setItem(1, jumpBoost);

        playerToKill.getInventory().addItem(itemInSlot0, itemInSlot1);
    }

    public int currentTime = 0;

    /**
     * The main method for handling the game.
     * Used for eliminating the players once time runs out.
     */
    public void runEliminationTask() {
        currentTime = 0;
        if(mainAction != null) {
            mainAction.cancel();
        }

        mainAction = new BukkitRunnable() {
            @Override
            public void run() {
                if(!isEnabled || !isGameOn) {this.cancel(); return;}
                else if (roundDelay < currentTime) {this.cancel(); return;}

                if(isTimeLeftVisible) {
                    for (Player p : totalPlayers) {
                        p.sendActionBar(Component.text("Pozostały czas: ").color(NamedTextColor.GREEN).append(
                                Component.text(roundDelay - currentTime + " s").color((roundDelay - currentTime <= 3) ? NamedTextColor.RED : YELLOW).decorate(TextDecoration.BOLD)
                        ));
                        if (roundDelay - currentTime <= 3) {
                            if (p == playerToKill) {
                                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 2, .5F);
                            } else {
                                p.playSound(p, Sound.UI_BUTTON_CLICK, 2, 2);
                            }
                        }
                    }
                }

                if(currentTime >= roundDelay) {
                    currentTime = 0;
                    plugin.getServer().broadcast(Component.text("Życie " + playerToKill.getName() + " dobiegło końca!").color(NamedTextColor.RED));
                    Random rand = new Random();
                    int deathEffect = rand.nextInt(0, 5);
                    switch (deathEffect) {
                        case 0 -> new LaunchDeath(plugin, playerToKill, GameController.this);
                        case 1 -> new RemoteDetonatorDeath(plugin, playerToKill, GameController.this);
                        case 2 -> new StrikeDeath(plugin, playerToKill, GameController.this);
                        case 3 -> new FireworkDeath(plugin, playerToKill, GameController.this);
                        case 4 -> new HorseDeath(plugin, playerToKill, GameController.this);
                    }
                    this.cancel();
                }
                GameEventHandler.sendGameTickEvent(GameController.this);
                currentTime += 1;
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }
}
