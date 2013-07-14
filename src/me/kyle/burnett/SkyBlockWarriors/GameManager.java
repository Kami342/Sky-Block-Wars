package me.kyle.burnett.SkyBlockWarriors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.kyle.burnett.SkyBlockWarriors.Configs.ConfigManager;
import me.kyle.burnett.SkyBlockWarriors.Utils.WorldEditUtility;

import org.bukkit.entity.Player;

public class GameManager {

    static GameManager instance = new GameManager();

    private List<Game> games = new ArrayList<Game>();
    private HashMap<String, Integer> playerGame = new HashMap<String, Integer>();
    private HashMap<String, Integer> confirm = new HashMap<String, Integer>();
    private HashMap<String, Integer> editing = new HashMap<String, Integer>();

    public static GameManager getInstance() {

        return instance;
    }

    public void setUp() {

        games.clear();

        for (int x = 0; x <= getArenaAmount(); x++) {

            if (Main.getInstance().Arena.contains("Arena." + x)) {

                if (Main.getInstance().Arena.getBoolean("Arena." + x + ".Enabled")) {

                    games.add(new Game(x, true));
                }
            }
        }
    }

    public int createGame(Player p) {

        int amount = getArenaAmount();

        int newGame = amount + 1;

        WorldEditUtility.getInstance().saveArena(p, newGame);

        Main.getInstance().Arena.set("Arena." + newGame + ".Enabled", true);
        Main.getInstance().Arena.set("Arena." + newGame + ".Active", false);


        Main.getInstance().Arena.set("Amount", newGame);

        ConfigManager.getInstance().saveYamls();

        games.add(new Game(newGame, true, false));

        return newGame;
    }

    public void overrideArena(Player p, Integer arena) {


        Main.getInstance().Arena.set("Arena" + arena, null);
        Main.getInstance().Chest.set("Chest." + arena, null);
        Main.getInstance().Arena.set("Arena." + arena + ".Enabled", true);
        ConfigManager.getInstance().saveYamls();

        WorldEditUtility.getInstance().overrideSave(p, arena);

        Game g = getGameByID(arena);

        getGames().remove(g);

        games.add(new Game(arena, true));

    }

    public List<Game> getGames() {
        return games;
    }

    public Game getPlayerGame(Player p) {

        if (playerGame.get(p.getName()) != null) {

            return getGameByID(playerGame.get(p.getName()));
        }

        return null;
    }

    public boolean isPlayerInGame(Player p) {

        if (playerGame.keySet().contains(p.getName())) {

            if (playerGame.get(p.getName()) != null) {

                return true;
            }
        }

        return false;
    }

    public boolean hasPlayerGameStarted(Player p) {

        if (getPlayerGame(p).getState().equals(ArenaState.IN_GAME)) {
            return true;
        }

        return false;
    }

    public void setPlayerGame(Player p, Integer g) {

        playerGame.put(p.getName(), g);
    }

    public int getArenaAmount() {
        return Main.getInstance().Arena.getInt("Amount");
    }

    public void removePlayer(Player p) {
        playerGame.keySet().remove(p.getName());

    }

    public String listGames() {

        List<String> strings = new ArrayList<String>();

        for (int x = 0; x < games.size(); x++) {

            String s = Integer.toString(games.get(x).getGameID());

            strings.add(s);
        }

        return strings.toString().replace("[", "").replace("]", "");
    }

    public void disableGame(int game) {
        Main.getInstance().Arena.set("Arena." + game + ".Enabled", false);
        ConfigManager.getInstance().saveYamls();
        games.remove(game);
    }

    public void enableGame(int game) {
        Main.getInstance().Arena.set("Arena." + game + ".Enabled", true);
        ConfigManager.getInstance().saveYamls();
        games.add(new Game(game, false, false));
    }

    public void activate(int game) {
        Main.getInstance().Arena.set("Arena." + game + ".Active", true);
        ConfigManager.getInstance().saveYamls();
        getGameByID(game).prepareArena(false, false);

    }

    public void deactivate(int game) {
        Main.getInstance().Arena.set("Arena." + game + ".Active", false);
        ConfigManager.getInstance().saveYamls();
    }

    public boolean isActive(int game) {
        if (Main.getInstance().Arena.getBoolean("Arena." + game + ".Active")) {
            return true;
        }
        return false;
    }

    public boolean isEnabled(int game) {

        if (Main.getInstance().Arena.getBoolean("Arena." + game + ".Enabled")) {
            return true;
        }

        return false;
    }

    public HashMap<String, Integer> getConfirming() {
        return confirm;
    }

    public HashMap<String, Integer> getEditing() {

        return editing;
    }

    public Game getGameEditing(Player p) {

        int game = getEditing().get(p.getName());

        return getGameByID(game);
    }

    public int getPlayerEditing(Player p) {
        return getEditing().get(p.getName());
    }

    public void addEditor(Player p, Integer game) {
        getEditing().put(p.getName(), game);
        getGameByID(game).addEditor(p);
    }

    public boolean isEditing(Player p) {
        if (getEditing().get(p.getName()) != null) {
            return true;
        }
        return false;
    }

    public void removeEditor(Player p) {

        if (getGameEditing(p).getEditorsSize() == 1) {
            getGameEditing(p).setState(ArenaState.WAITING);
        }

        getGameEditing(p).removeEditor(p);

        getEditing().put(p.getName(), null);
    }

    public boolean isInteger(String s) {
        try {

            Integer.parseInt(s);

        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public boolean checkGameByID(int id) {

        for (Game g : getGames()) {

            if (g.getGameID() == id) {
                return true;
            }
        }
        return false;
    }

    public Game getGameByID(int id) {

        for (Game g : getGames()) {

            if (g.getGameID() == id) {
                return g;
            }
        }
        return null;
    }


    public HashMap<String, Integer> getPlayers() {
        return playerGame;
    }
}
