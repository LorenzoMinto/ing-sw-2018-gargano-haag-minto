package it.polimi.se2018.controller;

import it.polimi.se2018.model.*;
import it.polimi.se2018.utils.BadBehaviourRuntimeException;
import it.polimi.se2018.utils.Observable;
import it.polimi.se2018.view.View;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Class that represent the Controller according the MVC paradigm.
 * It contains the logic "in-the-large" while the logic "in-the-small" is
 * contained in Model classes.
 *
 * {@code Controller} receives messages from connected {@link View}(s) validating and, in case,
 * redirecting them to the connected {@link Game}.
 *
 * @author Lorenzo Minto
 * @author Jacopo Pio Gargano
 * @author Federico Haag
 *
 * @see Game
 * @see ControllerState
 * @see ControllerStateManager
 * @see ToolCard
 * @see ToolCardsManager
 * @see DiceBag
 * @see ObjectiveCardManager
 * @see PlacementRule
 * @see WindowPatternManager
 */
public class Controller extends Observable {

    private static final String NOT_YOUR_TURN = "It is not your turn.";
    /**
     * The controlled {@link Game}
     */
    protected Game game;  //Reference to the Model

    /**
     * The current state of the {@link Controller}
     */
    protected ControllerState controllerState; //State pattern
    //TODO: capire perchè IntelliJ consigli di mettere controllerState private. Non dovrebbe consigliarlo. Sicuri che gli state funzioni correttamente?

    /**
     * Contains an instance of a {@link ControllerStateManager} that is the one
     * that creates the needed {@link ControllerState} during a change of state
     *
     * @see ControllerState
     */
    protected ControllerStateManager stateManager;

    /**
     * The {@link ToolCard} that is being used in the current {@link Turn}
     *
     * @see ToolCard
     */
    private ToolCard activeToolcard;

    /**
     * Contains an istance of a {@link DiceBag} in order to create the
     * needed dices for the game
     *
     * @see Dice
     */
    protected DiceBag diceBag;

    /**
     * The current Placement Rule that is used in the current {@link Turn}
     * in order to decide if a placement move is allowed or not
     *
     * @see PlaceControllerState that is the class handling the placement move
     * @see ToolCard that is the class that could change the placement rule
     * @see WindowPattern that is the final involved class
     */
    protected PlacementRule placementRule;

    /**
     * Contains an instance of a {@link ObjectiveCardManager} that is the one
     * that creates {@link PublicObjectiveCard}(s) and {@link PrivateObjectiveCard} (s)
     *
     * @see PublicObjectiveCard
     * @see PrivateObjectiveCard
     */
    private ObjectiveCardManager objectiveCardManager;

    /**
     * Contains an instance of a {@link WindowPatternManager} that is the one
     * that creates the {@link WindowPattern}(s) for player(s)
     *
     * @see WindowPattern
     */
    private WindowPatternManager windowPatternManager;

    /**
     * Contains an instance of a {@link ToolCardsManager} that is the one
     * that creates the {@link ToolCard} (s) to be assigned to the {@link Game}
     */
    private ToolCardsManager toolCardsManager;

    private final Properties properties;

    int movesCounter = 0;


    /**
     * Construct a controller with reference to a game instance
     * and all manager and factory classes needed to the correct
     * working of the game
     *
     * @param game the game instance to be controlled
     * @param properties dictionary of parameters loaded from config file
     */
    public Controller(Game game, Properties properties) {

        this.properties = properties;

        int numberOfDicesPerColor = Integer.parseInt( properties.getProperty("numberOfDicesPerColor") );
        int numberOfToolCards = Integer.parseInt( properties.getProperty("numberOfToolCards") );
        int numberOfPublicObjectiveCards =  Integer.parseInt( properties.getProperty("numberOfPublicObjectiveCards") );

        //Create Managers
        this.stateManager = new ControllerStateManager(this);

        this.windowPatternManager = new WindowPatternManager();

        this.toolCardsManager = new ToolCardsManager(this.getDefaultPlacementRule());

        this.objectiveCardManager = new ObjectiveCardManager();

        //Set main attributes
        this.game = game;
        this.controllerState =  this.stateManager.getStartState();
        this.activeToolcard = null;
        this.diceBag = new DiceBag(numberOfDicesPerColor);

        //Produces and sets cards to the game
        List<ToolCard> toolCards = toolCardsManager.getRandomToolCards(numberOfToolCards);
        List<PublicObjectiveCard> publicObjectiveCards = objectiveCardManager.getPublicObjectiveCards(numberOfPublicObjectiveCards);

        this.game.setCards(toolCards,publicObjectiveCards);
    }


    /**
     * Set as current state the one passed as method's argument
     *
     * @param controllerState state that must be setted to the controller
     */
    public void setControllerState(ControllerState controllerState) {
        this.controllerState = controllerState;
        this.controllerState.executeImplicitBehaviour(); //WARNING: could change controllerState implicitly
    }

    //TODO: handle moves (Message)

    /**
     * Checks if a given {@link ToolCard} can be used by the specified {@link Player}
     * @param toolCard the {@link ToolCard} to be checked
     * @return if the specified {@link ToolCard} can be used or not by the specified {@link Player} (true=can use)
     */
    protected boolean canUseSpecificToolCard(ToolCard toolCard) {

        //If a player has already drafted a dice, then they can't use a ToolCard that needs drafting
        if(toolCard.needsDrafting() && game.getCurrentRound().getCurrentTurn().hasDrafted()){
            return false;
        }

        Player currentPlayer = game.getCurrentRound().getCurrentTurn().getPlayer();

        return currentPlayer.canUseToolCard(toolCard);
    }

    /**
     * Sets the specified {@link ToolCard} as active. This means that it's being used
     * during the current {@link Turn}
     * @param toolCard the {@link ToolCard} that is being used
     */
    protected void setActiveToolCard(ToolCard toolCard) {

        game.useToolCard(toolCard);

        this.activeToolcard = game.getToolCard(toolCard);
        this.placementRule = this.activeToolcard.getPlacementRule();
    }

    /**
     * Resets the current active toolCard and resets the placement rules to their default.
     *
     * @author Lorenzo Minto
     */
    protected void resetActiveToolCard() {

        this.movesCounter = 0;
        this.activeToolcard = null;
        this.placementRule = getDefaultPlacementRule();
    }

    /**
     * Gets the {@link ToolCard} that is being used in the current {@link Turn}
     *
     * @return the {@link ToolCard} that is being used in the current {@link Turn}
     */
    protected ToolCard getActiveToolCard() {

        return activeToolcard;
    }


    /**
     * Let the game advance in the turns sequence.
     * If in the current {@link Round} have been played all the possible {@link Turn}s
     * it is created a new {@link Round}. If the current {@link Round} is the last one,
     * the game is ended calling {@link Controller#endGame()}.
     */
    protected void advanceGame() {

        resetActiveToolCard();
        setControllerState(stateManager.getStartState());

        //if player's window pattern is empty
        if(game.getCurrentRound().getCurrentTurn().getPlayer().getWindowPattern().isEmpty()){
            this.placementRule = new BorderPlacementRuleDecorator( getDefaultPlacementRule() );
        }

        //Proceed with turns / rounds
        try {
            game.getCurrentRound().nextTurn();
        } catch (NoMoreTurnsAvailableException e) {

            try {
                game.nextRound( diceBag.getDices(game.getPlayers().size()*2 + 1) );
            } catch (NoMoreRoundsAvailableException e1) {

                endGame();
            }

            try {
                game.getCurrentRound().nextTurn();
            } catch (NoMoreTurnsAvailableException e1) {
                throw new BadBehaviourRuntimeException("Asked next turn. No turns availables. Created a new round. Still no turns availables.");
            }

        }
    }

    /**
     * Ends the current game. Calculates rankings and scores and then notify them to players.
     */
    protected void endGame(){

        Map<Player, Integer> rankings = getRankingsAndScores();

        registerRankingsOnUsersProfiles(rankings);

        //TODO: notify view of winners and scores

    }

    /**
     * Gets the rankings and scores of the current {@link Game}.
     * @return rankings and scores of the current {@link Game}
     */
    protected Map<Player,Integer> getRankingsAndScores() {
        List<Player> playersOfLastRound = game.getCurrentRound().getPlayersByReverseTurnOrder();
        List<PublicObjectiveCard> publicObjectiveCards = game.getDrawnPublicObjectiveCards();

        return Scorer.getInstance().getRankings(playersOfLastRound, publicObjectiveCards);
    }

    /**
     * Sends scores and rankings to players profiles ({@link User})
     * in order to increase statistics of wins and played games.
     *
     * @param rankings
     */
    private void registerRankingsOnUsersProfiles(Map<Player, Integer> rankings){

        for(Player player : game.getPlayers()){

            User user = player.getUser();
            user.increaseGamesPlayed();

            Player winner = Scorer.getInstance().getWinner(rankings);

            if(winner.equals(player)){
                user.increaseGamesWon();
            }
        }
    }

    /**
     * Gets the default {@link PlacementRule}
     *
     * @return the default {@link PlacementRule}
     */
    protected PlacementRule getDefaultPlacementRule(){

        return new AdjacentValuePlacementRuleDecorator(
                new AdjacentDicePlacementRuleDecorator(
                        new AdjacentColorPlacementRuleDecorator(
                                new ColorPlacementRuleDecorator(
                                        new ValuePlacementRuleDecorator(
                                                new EmptyPlacementRule())))));

    }

    /**
     * Checks if the specified {@link User} with speficied nickname
     * can join the current {@link Game}.
     *
     * @param user the {@link User} asking to join the {@link Game}
     * @param nickname the nickname that the {@link User} whould use in this {@link Game}
     * @return if the specified {@link User} with speficied nickname
     * can join the current {@link Game}. True means "can join".
     * @throws AcceptingPlayerException if the user was already added or no more players
     * can be accepted in this game (maximum number of players reached)
     */
    public Player acceptPlayer(User user, String nickname) throws AcceptingPlayerException {

        if( game.canAcceptNewPlayer() ){
            PrivateObjectiveCard card = objectiveCardManager.getPrivateObjectiveCard();
            Player player = new Player(user,nickname,card);

            if( game.addPlayer(player) ){ return player; }

            throw new AcceptingPlayerException("Player was already added");

        }

        throw new AcceptingPlayerException("No more players can be accepted");

    }

    /**
     * @return Dictionary of parameters
     */
    public int getConfigProperty(String p){
        return Integer.parseInt( this.properties.getProperty(p) );
    }

}
