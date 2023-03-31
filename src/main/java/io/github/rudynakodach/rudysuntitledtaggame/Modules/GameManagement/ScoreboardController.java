package io.github.rudynakodach.rudysuntitledtaggame.Modules.GameManagement;

import io.github.rudynakodach.rudysuntitledtaggame.Modules.Events.Generic.GameEventHandler;
import io.github.rudynakodach.rudysuntitledtaggame.Modules.Events.Generic.GameEventListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.List;

public class ScoreboardController implements GameEventListener {

    private final List<Player> players;
    private final Scoreboard scoreboard;
    private Objective objective;
    private Score it;
    private Score playerCount;
    private final GameController controller;
    public ScoreboardController(GameController controller) {
        this.controller = controller;
        GameEventHandler.registerListener(this);
        this.scoreboard = createNewScoreboard();
        this.players = controller.playersAlive;
    }

    private Scoreboard createNewScoreboard() {
        return Bukkit.getServer().getScoreboardManager().getNewScoreboard();
    }

    public void displayToPlayers() {
        for (Player p : players) {
            p.setScoreboard(scoreboard);
        }
    }

    public void prepareScoreboard() {
        objective = scoreboard.registerNewObjective("game", Criteria.DUMMY, Component.text("Gra w ").append(Component.text("KONIA").color(NamedTextColor.RED).decorate(TextDecoration.BOLD)));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score filler = objective.getScore("");
        filler.setScore(14);

        Score itLabel = objective.getScore(ChatColor.RED + "" + ChatColor.BOLD + "KOŃ");
        itLabel.setScore(13);

        it = objective.getScore(controller.playerToKill.getName());
        it.setScore(12);

        Score filler2 = objective.getScore("");
        filler2.setScore(11);

        Score playersAliveLabel = objective.getScore("Pozostałe ofiary losu");
        playersAliveLabel.setScore(10);

        playerCount = objective.getScore(String.valueOf(controller.playersAlive.size()));
        playerCount.setScore(9);

    }

    @Override
    public void onItChange(GameController controller, Player player) {
        if(it != null) {
            it.resetScore();
        }
        it = objective.getScore(player.getName());
        it.setScore(12);
    }

    @Override
    public void onPlayerEliminated(GameController controller, Player player) {
        if(playerCount != null) {
            playerCount.resetScore();
        }
        playerCount = objective.getScore(String.valueOf(controller.playersAlive.size()));
        playerCount.setScore(9);
    }

    @Override
    public void onRoundStart(GameController controller) {

    }

    @Override
    public void onPlayerRevived(GameController controller, Player player) {
        if(playerCount != null) {
            playerCount.resetScore();
        }
        playerCount = objective.getScore(String.valueOf(controller.playersAlive.size()));
        playerCount.setScore(9);
    }
}
