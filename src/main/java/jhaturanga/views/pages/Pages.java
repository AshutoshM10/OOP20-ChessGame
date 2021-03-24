package jhaturanga.views.pages;

import java.util.function.Supplier;

import jhaturanga.controllers.Controller;
import jhaturanga.controllers.EmptyController;
import jhaturanga.controllers.editor.EditorControllerImpl;
import jhaturanga.controllers.history.HistoryControllerImpl;
import jhaturanga.controllers.home.HomeControllerImpl;
import jhaturanga.controllers.loading.LoadingControllerImpl;
import jhaturanga.controllers.login.LoginControllerImpl;
import jhaturanga.controllers.match.MatchControllerImpl;
import jhaturanga.controllers.replay.ReplayControllerImpl;
import jhaturanga.controllers.resume.ResumeControllerImpl;
import jhaturanga.controllers.settings.SettingsControllerImpl;
import jhaturanga.controllers.setup.SetupControllerImpl;

public enum Pages {

    /**
     * Loading screen.
     */
    LOADING("loading", LoadingControllerImpl::new),

    /**
     * Splash page.
     */
    SPLASH("splash", EmptyController::new),

    /**
     * Home page.
     */
    HOME("home", HomeControllerImpl::new),

    /**
     * New Game page.
     */
    NEWGAME("newgame", EmptyController::new),

    /**
     * Match page.
     */
    MATCH("match", MatchControllerImpl::new),

    /**
     * Leaderboard page.
     */
    LEADERBOARD("leaderboard", EmptyController::new),

    /**
     * Resume page.
     */
    RESUME("resume", ResumeControllerImpl::new),

    /**
     * Game Type Selection page.
     */
    SETUP("setup", SetupControllerImpl::new),

    /**
     * Login page.
     */
    LOGIN("login", LoginControllerImpl::new),

    /**
     * Editor page.
     */
    EDITOR("editor", EditorControllerImpl::new),

    /**
     * History page.
     */
    HISTORY("history", HistoryControllerImpl::new),

    /**
     * Settings page.
     */
    SETTINGS("settings", SettingsControllerImpl::new),

    /**
     * Replay page.
     */
    REPLAY("replay", ReplayControllerImpl::new);

    private final String name;
    private final Supplier<Controller> controllerGenerator;

    Pages(final String name, final Supplier<Controller> controllerGenerator) {
        this.name = name;
        this.controllerGenerator = controllerGenerator;
    }

    public String getName() {
        return this.name;
    }

    public Controller getNewControllerInstance() {
        return this.controllerGenerator.get();
    }

}