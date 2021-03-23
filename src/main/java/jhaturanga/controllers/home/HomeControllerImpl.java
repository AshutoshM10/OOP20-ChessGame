package jhaturanga.controllers.home;

import java.util.Optional;

import jhaturanga.controllers.AbstractController;
import jhaturanga.model.game.gametypes.GameTypesEnum;
import jhaturanga.model.player.Player;
import jhaturanga.model.timer.DefaultTimers;
import jhaturanga.model.user.User;
import jhaturanga.model.user.management.UsersManager;

public final class HomeControllerImpl extends AbstractController implements HomeController {

    @Override
    public void setGameType(final GameTypesEnum gameType) {
        this.getModel().setGameType(gameType);
    }

    @Override
    public void setTimer(final DefaultTimers timer) {
        this.getModel().setTimer(timer);
    }

    @Override
    public void createMatch() {
        this.getModel().createMatch();
    }

    @Override
    public boolean isGameTypePresent() {
        return this.getModel().getGameType().isPresent();
    }

    @Override
    public boolean isDynamicGameTypePresent() {
        return this.getModel().isDynamicGameTypeSet();
    }

    @Override
    public void setWhitePlayer(final Player player) {
        this.getModel().setWhitePlayer(player);
    }

    @Override
    public void setBlackPlayer(final Player player) {
        this.getModel().setBlackPlayer(player);
    }

    @Override
    public void setFirstUserGuest() {
        this.getModel().setFirstUser(UsersManager.GUEST);

    }

    @Override
    public void setSecondUserGuest() {
        this.getModel().setSecondUser(UsersManager.GUEST);

    }

    @Override
    public Optional<User> getFirstUser() {
        return this.getModel().getFirstUser();
    }

    @Override
    public Optional<User> getSecondUser() {
        return this.getModel().getSecondUser();
    }

    @Override
    public boolean isFirstUserLogged() {
        return this.getModel().getFirstUser().isPresent();
    }

    @Override
    public boolean isSecondUserLogged() {
        return this.getModel().getSecondUser().isPresent();
    }

    @Override
    public void setupPlayers() {
        if (this.getFirstUser().isPresent() && this.getSecondUser().isPresent()) {
            this.getModel().setWhitePlayer(new PlayerImpl(PlayerColor.WHITE, this.getFirstUser().get()));
            this.getModel().setBlackPlayer(new PlayerImpl(PlayerColor.BLACK, this.getSecondUser().get()));
        }

    }

    @Override
    public Optional<String> getGameTypeName() {
        return this.getModel().getSettedGameTypeName();
    }

}
