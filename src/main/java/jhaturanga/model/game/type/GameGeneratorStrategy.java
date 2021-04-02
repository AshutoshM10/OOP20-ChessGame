package jhaturanga.model.game.type;

import jhaturanga.model.game.Game;
import jhaturanga.model.game.factory.GameFactory;
import jhaturanga.model.player.PlayerPair;

@FunctionalInterface
public interface GameGeneratorStrategy {

    /**
     * 
     * @param gameTypeFactory
     * @param players
     * @return the generated game type instance
     */
    Game generate(GameFactory gameTypeFactory, PlayerPair players);
}
