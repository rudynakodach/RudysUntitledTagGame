package io.github.rudynakodach.rudysuntitledtaggame.Modules.GameManagement;

import io.github.rudynakodach.rudysuntitledtaggame.Modules.Events.GameTickEvent;
import io.github.rudynakodach.rudysuntitledtaggame.Modules.Events.Generic.GameEventHandler;
import io.github.rudynakodach.rudysuntitledtaggame.Modules.Events.Generic.GameEventListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ScoreboardController implements GameEventListener {

    private final List<Player> playersAlive;
    private final List<Player> totalPlayers;
    private final Scoreboard scoreboard;
    private final Objective objective;
    private final GameController controller;
    Score it;
    Score playerCount;
    Score timeLeft;
    private static final int IT_SCORE = 12;
    private static final int PLAYER_COUNT_SCORE = 9;
    private static final int GAME_TICK_SCORE = 6;
    public ScoreboardController(GameController controller) {
        this.controller = controller;
        GameEventHandler.registerListener(this);
        this.scoreboard = createNewScoreboard();
        Objective e = scoreboard.getObjective("game");
        if(e != null) {
            this.objective = e;
            e.displayName(Component.text("Gra w ").append(Component.text("KONIA").color(NamedTextColor.RED).decorate(TextDecoration.BOLD)));
        } else {
            this.objective = scoreboard.registerNewObjective("game", Criteria.DUMMY, Component.text("Gra w ").append(Component.text("KONIA").color(NamedTextColor.RED).decorate(TextDecoration.BOLD)));
        }

        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.playersAlive = controller.playersAlive;
        this.totalPlayers = controller.totalPlayers;
    }

    private Scoreboard createNewScoreboard() {
        return Bukkit.getServer().getScoreboardManager().getNewScoreboard();
    }

    public void displayToPlayers() {
        for (Player p : playersAlive) {
            p.setScoreboard(scoreboard);
        }
    }

    public void prepareScoreboard() {
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

        Score filler3 = objective.getScore("");
        filler3.setScore(8);

        Score timeLeftLabel = objective.getScore(ChatColor.BLUE + "Pozostały czas");
        timeLeftLabel.setScore(7);

        timeLeftLabel = objective.getScore("-");
        timeLeftLabel.setScore(6);

    }

    @Override
    public void onItChange(GameController controller, Player player) {
        if(it != null) {
            it.resetScore();
        }

        Score s = objective.getScore(player.getName());
        s.setScore(IT_SCORE);

        it = s;
    }

    @Override
    public void onPlayerEliminated(GameController controller, Player player) {
        if(playerCount != null) {
            playerCount.resetScore();
        }

        Score s = objective.getScore(String.valueOf(controller.playersAlive.size()));
        s.setScore(PLAYER_COUNT_SCORE);

        playerCount = s;
    }

    @Override
    public void onRoundStart(GameController controller) {

    }

    @Override
    public void onPlayerRevived(GameController controller, Player player) {
        if(playerCount != null) {
            playerCount.resetScore();
        }

        Score s = objective.getScore(String.valueOf(controller.playersAlive.size()));
        s.setScore(PLAYER_COUNT_SCORE);

        playerCount = s;
    }

    @Override
    public void onGameEnded(GameController controller) {
        for (Player p : totalPlayers) {
            p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }

    @Override
    public void onGameTick(GameTickEvent event) {
        long roundDelay = event.getRoundDelay();
        long currentTime = event.getRoundDelay();

        long time = roundDelay - currentTime;
        String remainingTime = new SimpleDateFormat("mm:ss.SSS").format(new Date(time));

        if(timeLeft != null) {
            timeLeft.resetScore();
        }

        Score s = objective.getScore(remainingTime);
        s.setScore(GAME_TICK_SCORE);

        timeLeft = s;
    }
}
