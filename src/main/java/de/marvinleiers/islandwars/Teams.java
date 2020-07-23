package de.marvinleiers.islandwars;

import de.marvinleiers.gameapi.game.Game;
import de.marvinleiers.gameapi.utils.CustomConfig;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Teams
{
    private static CustomConfig gameConfig = new CustomConfig(IslandWars.getPlugin().getDataFolder().getPath() + "/games/games.yml");

    private Game game;

    public ArrayList<Player> firstTeam = new ArrayList<>();
    public ArrayList<Player> secondTeam = new ArrayList<>();
    private int firstStarsLeft;
    private int secondStarsLeft;

    public Teams(Game game)
    {
        this.game = game;
        this.firstStarsLeft = 3;
        this.secondStarsLeft = 3;

        for (int i = 0; i < game.players.size(); i++)
        {
            if (i % 2 == 0)
            {
                firstTeam.add(game.players.get(i));

                game.players.get(i).teleport(gameConfig.getLocation("games." + game.getName() + ".team-1.spawn"));
            }
            else
            {
                secondTeam.add(game.players.get(i));

                game.players.get(i).teleport(gameConfig.getLocation("games." + game.getName() + ".team-2.spawn"));
            }
        }
    }

    public void reduceFirstStars()
    {
        this.firstStarsLeft--;
    }

    public void reduceSecondStars()
    {
        this.secondStarsLeft--;
    }

    public int getFirstStarsLeft()
    {
        return firstStarsLeft;
    }

    public int getSecondStarsLeft()
    {
        return secondStarsLeft;
    }

    public ArrayList<Player> getFirstTeam()
    {
        return firstTeam;
    }

    public ArrayList<Player> getSecondTeam()
    {
        return secondTeam;
    }

    public Game getGame()
    {
        return game;
    }
}
