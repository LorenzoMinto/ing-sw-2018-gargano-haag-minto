package it.polimi.se2018.view;

import it.polimi.se2018.controller.ObjectiveCardManager;
import it.polimi.se2018.controller.RankingRecord;
import it.polimi.se2018.model.*;
import it.polimi.se2018.networking.Client;
import it.polimi.se2018.networking.ConnectionType;
import it.polimi.se2018.networking.NetworkingException;
import it.polimi.se2018.networking.SenderInterface;
import it.polimi.se2018.utils.*;
import it.polimi.se2018.utils.Observer;
import it.polimi.se2018.utils.Message;

import java.util.*;

/**
 * This is the View class of the MVC paradigm.
 * It's an abstract class because a lot of actions depends strongly on implementation.
 * Just the basic code is inserted here. Every method is then re-implemented or extended by
 * actual implementations (CLI and GUI).
 * 
 * @author Federico Haag
 */
public abstract class View implements Observer {

    /*  CONSTANTS FOR CONSOLE MESSAGES
        Following constants are not commented one by one because they are as self explaining as needed.
        Major information can be found looking for their usage.
        Being private, they are used only in this file. So if a change is needed, just look for usages in this file.
    */
    private static final String MUST_CONNECT = "You have to connect to the server";
    private static final String THE_GAME_IS_ENDED = "Game ended";
    private static final String WINDOW_PATTERNS_RECEIVED = "Received window patterns to choose from";
    private static final String YOU_HAVE_JOINED_THE_WAITING_ROOM = "You have joined the waiting room";
    private static final String REMOVED_FROM_GAME = "You were successfully disconnected from the game";
    private static final String A_PLAYER_BECAME_INACTIVE = " has become inactive. Their turns will be skipped.";
    private static final String BACK_TO_GAME = "You are back in the game, now.";
    private static final String YOU_ARE_NOW_INACTIVE = "You were disconnected from the game for inactivity. Your turns will be skipped.";
    private static final String FAILED_SETUP_GAME = "Initial game setup failed. You could face crucial issues playing.";
    private static final String FAILED_SETUP_ROUND = "New round setup failed. You could face crucial issues playing.";
    private static final String FAILED_SETUP_TURN = "New turn setup failed. You could face crucial issues playing.";
    private static final String YOU_ARE_THE_WINNER = "You are the winner! Congratulations!";
    private static final String THE_WINNER_IS = "The winner is ";
    private static final String WINDOW_PATTERN_UPDATED = "A Window pattern has been updated ";
    private static final String ITS_YOUR_TURN = "It's your turn!";
    private static final String ERROR_MOVE = "An unexpected error wouldn't let you perform the move. Try again.";
    private static final String MAX_PLAYERS_ERROR = "You can't join the game as there is already the maximum number of players in the game.";
    private static final String NICKNAME_ALREADY_USED_ERROR = "You can't join the game as there already is a player with this nickname.";
    private static final String ALREADY_PLAYING_ERROR = "You can't join the game as it is not running.";
    private static final String USE_TOOL_CARD = " uses the toolCard ";
    private static final String YOU_HAVE_DRAFTED = "You have drafted ";
    private static final String JOINS_THE_WAITING_ROOM = " joins the waiting room";
    private static final String LEAVES_THE_WAITING_ROOM = " leaves the waiting room";
    private static final String ROUND_NOW_STARTS = "# Round now starts!";
    private static final String NOW_ITS_TURN_OF = "Now it's the turn of";
    private static final String THE_GAME_IS_STARTED = "The game started!";
    private static final String ERROR_SENDING_MESSAGE = "Error sending message: ";


    /*  CONSTANTS FOR MESSAGES PARAMS
        Following constants are not commented one by one because they are as self explaining as needed.
        Major information can be found looking for their usage.
        Being private, they are used only in this file. So if a change is needed, just look for usages in this file.
     */
    //Note: this strings are strictly connected with the ones used in Controller and Model. DO NOT CHANGE!
    private static final String PARAM_PLAYER = "player";
    private static final String PARAM_MESSAGE = "message";
    private static final String PARAM_DRAWN_TOOL_CARDS = "drawnToolCards";
    private static final String PARAM_DRAWN_PUBLIC_OBJECTIVE_CARDS = "drawnPublicObjectiveCards";
    private static final String PARAM_PLAYERS = "players";
    private static final String PARAM_TRACK = "track";
    private static final String PARAM_DRAFT_POOL_DICES = "draftPoolDices";
    private static final String PARAM_WINDOW_PATTERNS = "windowPatterns";
    private static final String PARAM_YOUR_WINDOW_PATTERN = "yourWindowPattern";
    private static final String PARAM_PRIVATE_OBJECTIVE_CARD = "privateObjectiveCard";
    private static final String PARAM_WHO_IS_PLAYING = "whoIsPlaying";
    private static final String PARAM_WINNER_PLAYER_ID = "winnerPlayerID";
    private static final String PARAM_RANKINGS = "rankings";
    private static final String PARAM_CURRENT_PLAYER = "currentPlayer";
    private static final String PARAM_WINDOW_PATTERN = "windowPattern";
    private static final String PARAM_TOOL_CARD = "toolCard";
    private static final String PARAM_TOOL_CARDS = "toolCards";
    private static final String PARAM_DRAFTED_DICE = "draftedDice";


    // CONFIGURATION INFORMATION

    /**
     * Set of moves that the player can do with this view
     */
    private EnumSet<Move> permissions = EnumSet.of(Move.JOIN_GAME);

    /**
     * State of the view: becomes "inactive" when player is marked from server as "inactive"
     */
    private ViewState state = ViewState.ACTIVE;

    /**
     * The client that handles communication of this view
     */
    private SenderInterface client;



    // COPIES OF GAME INFORMATION TO GRAPHICALLY REPRESENT THEM

    /**
     * The ID of the player of this view
     */
    private String playerID;

    /**
     * Number of the current round
     */
    private int roundNumber;

    /**
     * ToolCards that have been distributed at the beginning of the game
     */
    List<ToolCard> drawnToolCards;

    /**
     * Public Objective Cards that have been distributed at the beginning of the game
     */
    List<PublicObjectiveCard> drawnPublicObjectiveCards;

    /**
     * Window Patterns that were given at the beginning of the game to the view's player
     * to choose one of them.
     */
    List<WindowPattern> drawnWindowPatterns;
    
    /**
     * List of players
     */
    List<String> players; //TODO: hanno un ordine garantito? scrivi risposta in javadoc

    /**
     * Track
     */
    Track track;

    /**
     * Dices currently contained in draft pool
     */
    List<Dice> draftPoolDices;

    /**
     * ID of the current playing player
     */
    String playingPlayerID;

    /**
     * Reference to the dice that has been drafted and it has not been placed somewhere yet
     */
    Dice draftedDice;

    /**
     * Window Pattern of the view's player
     */
    WindowPattern windowPattern;

    /**
     * Window Patterns of players
     */
    List<WindowPattern> windowPatterns; //TODO: contiene anche il proprio? scrivi risposta in javadoc

    /**
     * The private objective card that has been given to the player at the beginning of the game
     */
    PrivateObjectiveCard privateObjectiveCard;

    /**
     * Final rankings of the game
     */
    List<RankingRecord> rankings;



    // HANDLING OF MOVES (PERFORMED BY THE VIEW'S PLAYER)

    /**
     * Handles the move "Leave Waiting Room"
     */
    void handleLeaveWaitingRoomMove(){
        sendMessage(new Message(ControllerBoundMessageType.LEAVE_WR,Message.fastMap("nickname",this.playerID)));
    }

    /**
     * Handles the move "Back to game"
     */
    void handleBackGameMove(){
        sendMessage(new Message(ControllerBoundMessageType.BACK_GAMING,null,this.playerID));
    }

    /**
     * Handles the move "End turn"
     */
    void handleEndTurnMove(){
        sendMessage(new Message(ControllerBoundMessageType.END_TURN,null,this.playerID));
    }

    /**
     * @param wp
     */
    void handleWindowPatternSelection(WindowPattern wp){
        //TODO: questo metodo non è mai usato. come mai?
        sendMessage(new Message(ControllerBoundMessageType.END_TURN,null,this.playerID));
    }

    /**
     * Handles the move "Drafted dice from Drat Pool"
     */
    void handleDraftDiceFromDraftPoolMove(){
        //no behaviour in common between CLI and GUI
    }

    /**
     * Handles the move "Place dice on window pattern"
     */
    void handlePlaceDiceOnWindowPatternMove(){
        //no behaviour in common between CLI and GUI
    }

    /**
     * Handles the move "Use tool card"
     */
    void handleUseToolCardMove(){
        //no behaviour in common between CLI and GUI
    }

    /**
     * Handles the move "Increment drafted dice"
     */
    void handleIncrementDraftedDiceMove(){
        sendMessage(new Message(ControllerBoundMessageType.INCREMENT_DICE));
    }

    /**
     * Handles the move "Decrement drafted dice"
     */
    void handleDecrementDraftedDiceMove(){
        sendMessage(new Message(ControllerBoundMessageType.DECREMENT_DICE));
    }

    /**
     * Handles the move "End effect"
     */
    void handleEndEffectMove(){
        sendMessage(new Message(ControllerBoundMessageType.END_TOOLCARD_EFFECT));
    }

    /**
     * Handles the move "Change drafted dice value"
     */
    void handleChangeDraftedDiceValueMove(){
        //TODO: implement
    }

    /**
     * Handles the move "Choose dice from track"
     */
    void handleChooseDiceFromTrackMove(){
        //TODO: implement
    }

    /**
     * Handles the move "Move Dice"
     */
    void handleMoveDiceMove(){
        //TODO: implement
    }

    /**
     * Handles the move "Join game"
     */
    void handleJoinGameMove(){
        //no behaviour in common between CLI and GUI
    }


    /*  HANDLING OF EVENTS. EVENTS ARE BASICALLY MESSAGES RECEIVED FROM SERVER.
        Some of the following methods are private because they are not extended or overridden by CLI and GUI.
     */

    /**
     * Handles an Acknowledgment
     * @param m message containing the acknowledgment
     */
    private void handleAcknowledgmentEvent(Message m){
        Object o;
        try {
            o = m.getParam(PARAM_MESSAGE);
        } catch (NoSuchParamInMessageException e) {
            return;
        }
        @SuppressWarnings("unchecked")
        String text = (String) o;

        ack(text);
    }

    /**
     * Handles the event "Inactive Player" that happens when another player becomes inactive
     * @param m the message relative to this event
     */
    private void handleInactivePlayerEvent(Message m){
        Object o;
        try {
            o = m.getParam(PARAM_PLAYER);
        } catch (NoSuchParamInMessageException e) {
            return;
        }
        @SuppressWarnings("unchecked")
        String pID = (String) o;

        showMessage(pID.concat(A_PLAYER_BECAME_INACTIVE));
    }

    /**
     * Handles the event "Game ended"
     */
    private void handleGameEndedEvent(){
        showMessage(THE_GAME_IS_ENDED);
    }

    /**
     * Handles Error Messages sent from server
     * @param m the message containing information about the error
     */
    private void handleErrorEvent(Message m){
        Object o;
        try {
            o = m.getParam(PARAM_MESSAGE);
        } catch (NoSuchParamInMessageException e) {
            return;
        }
        @SuppressWarnings("unchecked")
        String err = (String) o;

        if(!err.equals("")){
            errorMessage(err);
        }
    }

    /**
     * Handles the event "New Round"
     * @param m the message containing new round information
     */
    private void handleNewRoundEvent(Message m) {
        Object o;
        try {
            o = m.getParam("number");
        } catch (NoSuchParamInMessageException e) {
            showMessage(FAILED_SETUP_ROUND);
            return;
        }
        @SuppressWarnings("unchecked")
        int number = (int) o;

        try {
            o = m.getParam(PARAM_TRACK);
        } catch (NoSuchParamInMessageException e) {
            showMessage(FAILED_SETUP_ROUND);
            return;
        }
        @SuppressWarnings("unchecked")
        Track mTrack = (Track) o;

        try {
            o = m.getParam(PARAM_DRAFT_POOL_DICES);
        } catch (NoSuchParamInMessageException e) {
            showMessage(FAILED_SETUP_ROUND);
            return;
        }
        @SuppressWarnings("unchecked")
        List<Dice> mDraftPoolDices = (List<Dice>) o;

        setRoundNumber(number);
        setDraftPoolDices(mDraftPoolDices);
        setTrack(mTrack);

        notifyNewRound();
    }

    /**
     * Handles the event "New Turn"
     * @param m the message containing the new turn information
     */
    private void handleNewTurnEvent(Message m) {
        Object o;

        try {
            o = m.getParam(PARAM_WHO_IS_PLAYING);
        } catch (NoSuchParamInMessageException e) {
            showMessage(FAILED_SETUP_TURN);
            return;
        }
        @SuppressWarnings("unchecked")
        String whoIsPlaying = (String) o;

        setPlayingPlayerID(whoIsPlaying);

        notifyNewTurn();
    }

    /**
     * Handles the event "A player has been removed from the waiting room"
     * @param m the message containing the removed player information
     */
    private void handlePlayerRemovedFromWREvent(Message m){
        Object o;
        try {
            o = m.getParam(PARAM_PLAYER);
        } catch (NoSuchParamInMessageException e) {
            return;
        }
        @SuppressWarnings("unchecked")
        String nickname = (String) o;

        if(!nickname.equals(this.playerID)) {
            showMessage(nickname + LEAVES_THE_WAITING_ROOM);
        }
    }

    /**
     * Handles the event "A player has been added to the waiting room"
     * @param m the message containing the added player information
     */
    private void handlePlayerAddedToWREvent(Message m){
        Object o;
        try {
            o = m.getParam(PARAM_PLAYER);
        } catch (NoSuchParamInMessageException e) {
            return;
        }
        @SuppressWarnings("unchecked")
        String nickname = (String) o;

        if(!nickname.equals(this.playerID)){
            showMessage(nickname+ JOINS_THE_WAITING_ROOM);
        }
    }

    //Following methods are extended or overridden by CLI and/or GUI

    /**
     * Handles the event "Give Window Pattern"
     */
    void handleGiveWindowPatternsEvent(Message m){
        Object o;
        try {
            o = m.getParam(PARAM_WINDOW_PATTERNS);
        } catch (NoSuchParamInMessageException e) {
            return;
        }
        @SuppressWarnings("unchecked")
        List<WindowPattern> patterns = (List<WindowPattern>) o;
        this.drawnWindowPatterns = patterns;
        showMessage(WINDOW_PATTERNS_RECEIVED);
    }

    /**
     * Handles the event "Added to the waiting room"
     */
    void handleAddedEvent(){
        showMessage(YOU_HAVE_JOINED_THE_WAITING_ROOM);
    }

    /**
     * Handles the event "Removed from the waiting room"
     */
    void handleRemovedEvent(){
        showMessage(REMOVED_FROM_GAME);
    }

    /**
     * Handles the printing of the acknowledgment message
     * @param text the text to be printed
     */
    void ack(String text){
        if(!text.equals("")){
            showMessage(text);
        }
    }

    /**
     * Handles the event "Back to game"
     */
    void handleBackToGameEvent(){
        changeStateTo(ViewState.ACTIVE);
        showMessage(BACK_TO_GAME);
    }

    /**
     * Handles the event "You are inactive"
     */
    void handleInactiveEvent(){
        changeStateTo(ViewState.INACTIVE);
        showMessage(YOU_ARE_NOW_INACTIVE);
    }

    /**
     * Handles the event "Setup"
     * @param m the message containing setup information
     */
    void handleSetupEvent(Message m) {
        Object o;
        try {
            o = m.getParam(PARAM_DRAWN_TOOL_CARDS);
        } catch (NoSuchParamInMessageException e) {
            showMessage(FAILED_SETUP_GAME);
            return;
        }
        @SuppressWarnings("unchecked")
        List<ToolCard> mDrawnToolCards = (List<ToolCard>) o;

        try {
            o = m.getParam(PARAM_DRAWN_PUBLIC_OBJECTIVE_CARDS);
        } catch (NoSuchParamInMessageException e) {
            showMessage(FAILED_SETUP_GAME);
            return;
        }
        @SuppressWarnings("unchecked")
        List<PublicObjectiveCard> mDrawnPublicObjectiveCards = (List<PublicObjectiveCard>) o;

        try {
            o = m.getParam(PARAM_PLAYERS);
        } catch (NoSuchParamInMessageException e) {
            showMessage(FAILED_SETUP_GAME);
            return;
        }
        @SuppressWarnings("unchecked")
        List<String> mPlayers = (List<String>) o;

        try {
            o = m.getParam(PARAM_TRACK);
        } catch (NoSuchParamInMessageException e) {
            showMessage(FAILED_SETUP_GAME);
            return;
        }
        @SuppressWarnings("unchecked")
        Track mTrack = (Track) o;

        try {
            o = m.getParam(PARAM_DRAFT_POOL_DICES);
        } catch (NoSuchParamInMessageException e) {
            showMessage(FAILED_SETUP_GAME);
            return;
        }
        @SuppressWarnings("unchecked")
        List<Dice> mDraftPoolDices = (List<Dice>) o;

        try {
            o = m.getParam(PARAM_WINDOW_PATTERNS);
        } catch (NoSuchParamInMessageException e) {
            showMessage(FAILED_SETUP_GAME);
            return;
        }
        @SuppressWarnings("unchecked")
        List<WindowPattern> mWindowPatterns = (List<WindowPattern>) o;

        try {
            o = m.getParam(PARAM_YOUR_WINDOW_PATTERN);
        } catch (NoSuchParamInMessageException e) {
            showMessage(FAILED_SETUP_GAME);
            return;
        }
        @SuppressWarnings("unchecked")
        WindowPattern mWindowPattern = (WindowPattern) o;

        try {
            o = m.getParam(PARAM_PRIVATE_OBJECTIVE_CARD);
        } catch (NoSuchParamInMessageException e) {
            showMessage(FAILED_SETUP_GAME);
            return;
        }
        @SuppressWarnings("unchecked")
        PrivateObjectiveCard mPrivateObjectiveCard = (PrivateObjectiveCard) o;

        //Assignments are done only at the end of parsing of all data to prevent partial update (due to errors)
        setDrawnToolCards(mDrawnToolCards);
        setDraftPoolDices(mDraftPoolDices);
        setDrawnPublicObjectiveCards(mDrawnPublicObjectiveCards);
        setPlayers(mPlayers);
        setTrack(mTrack);
        setPrivateObjectiveCard(mPrivateObjectiveCard);
        setWindowPatterns(mWindowPatterns);
        setWindowPattern(mWindowPattern);

        notifyGameStarted();

        setPermissions(EnumSet.noneOf(Move.class));
    }

    /**
     * Handles the event "Received rankings"
     * @param m the message containing rankings.
     */
    void handleRankingsEvent(Message m) {
        Object o;
        try {
            o = m.getParam(PARAM_WINNER_PLAYER_ID);
        } catch (NoSuchParamInMessageException e) {
            return;
        }
        @SuppressWarnings("unchecked")
        String winnerID = (String) o;

        try {
            o = m.getParam(PARAM_RANKINGS);
        } catch (NoSuchParamInMessageException e) {
            return;
        }
        @SuppressWarnings("unchecked")
        List<RankingRecord> receivedRankings = (List<RankingRecord>) o;

        this.rankings = receivedRankings;

        if(winnerID.equals(this.playerID)){
            showMessage(YOU_ARE_THE_WINNER);
        } else {
            showMessage(THE_WINNER_IS +winnerID);
        }
    }

    /**
     * Handles the event "A Window Pattern has been updated"
     * @param m the message containing window pattern information
     */
    void handleUpdatedWindowPatternEvent(Message m) {
        Object o;
        try {
            o = m.getParam(PARAM_WINDOW_PATTERN);
        } catch (NoSuchParamInMessageException e) {
            return;
        }
        @SuppressWarnings("unchecked")
        WindowPattern wp = (WindowPattern) o;

        try {
            o = m.getParam(PARAM_CURRENT_PLAYER);
        } catch (NoSuchParamInMessageException e) {
            return;
        }
        @SuppressWarnings("unchecked")
        String pID = (String) o;

        // Assume ordinamento corrispettivo PLAYERS_ID:WINDOWPATTERNS
        int index = players.indexOf(pID);
        windowPatterns.set(index, wp);

        showMessage(WINDOW_PATTERN_UPDATED);
    }

    /**
     * Handles the event "Change Draft Pool"
     * @param m the message containing draftpool information
     */
    void handleChangedDraftPoolEvent(Message m) {
        Object o;
        try {
            o = m.getParam(PARAM_DRAFT_POOL_DICES);
        } catch (NoSuchParamInMessageException e) {
            return;
        }
        @SuppressWarnings("unchecked")
        List<Dice> mDraftPoolDices = (List<Dice>) o;
        setDraftPoolDices(mDraftPoolDices);
    }

    /**
     * Handles the event "It is now your turn"
     */
    void handleYourTurnEvent() {
        showMessage(ITS_YOUR_TURN);
    }

    /**
     * Handles the event "Bad Formatted". It is received when some previous message
     * sent to server was bad formatted or contained unexpected data.
     */
    void handleBadFormattedEvent() {
        showMessage(ERROR_MOVE);
    }

    /**
     * Handles the event "Can't join waiting room because players limit has been reached"
     */
    void handleDeniedLimitEvent() {
        showMessage(MAX_PLAYERS_ERROR);
    }

    /**
     * Handles the event "Can't join waiting room because your requested nickname is already used in this game"
     */
    void handleDeniedNicknameEvent() {
        showMessage(NICKNAME_ALREADY_USED_ERROR);
    }

    /**
     * Handles the event "Can't join because game is already running"
     */
    void handleDeniedPlayingEvent() {
        showMessage(ALREADY_PLAYING_ERROR);
    }

    /**
     * Handles the event "Used toolcard"
     * @param m the message containing toolcard information
     */
    void handleUsedToolCardEvent(Message m){
        Object o;
        try {
            o = m.getParam(PARAM_TOOL_CARD);
        } catch (NoSuchParamInMessageException e) {
            return;
        }
        @SuppressWarnings("unchecked")
        ToolCard toolCard = (ToolCard) o;

        try {
            o = m.getParam(PARAM_TOOL_CARDS);
        } catch (NoSuchParamInMessageException e) {
            return;
        }
        @SuppressWarnings("unchecked")
        List<ToolCard> toolCards = (List<ToolCard>) o;

        try {
            o = m.getParam(PARAM_PLAYER);
        } catch (NoSuchParamInMessageException e) {
            return;
        }
        @SuppressWarnings("unchecked")
        String p = (String) o;

        setDrawnToolCards(toolCards);

        showMessage(p+ USE_TOOL_CARD +toolCard.getTitle());
    }

    /**
     * @param m
     */
    //TODO: completa javadoc
    void handleSlotOfTrackChosenDice(Message m) {
        //TODO: implement here
    }

    void handleTrackChosenDiceEvent(Message m) {
        //TODO: verificare che questo metodo serva a qualcosa
    }

    /**
     * Handles the event "Drafted Dice"
     * @param m the message containing drafted dice information
     */
    void handleDraftedDiceEvent(Message m) {
        Object o;
        try {
            o = m.getParam(PARAM_DRAFTED_DICE);
        } catch (NoSuchParamInMessageException e) {
            return;
        }
        @SuppressWarnings("unchecked")
        Dice mDraftedDice = (Dice) o;
        setDraftedDice(mDraftedDice);
        showMessage(YOU_HAVE_DRAFTED +mDraftedDice);
    }


    // NOTIFY METHODS

    /**
     * Notify classes that extends View (CLI and GUI) about the beginning of a new round
     */
    void notifyNewRound(){
        showMessage(this.roundNumber+ ROUND_NOW_STARTS);
    }

    /**
     * Notify classes that extends View (CLI and GUI) about the beginning of a new turn
     */
    void notifyNewTurn(){
        if(!playingPlayerID.equals(playerID)){
            showMessage(NOW_ITS_TURN_OF + playingPlayerID);
            setPermissions(EnumSet.noneOf(Move.class));
        }
    }

    /**
     * Notify classes that extends View (CLI and GUI) about the beginning of the game
     */
    void notifyGameStarted(){
        showMessage(THE_GAME_IS_STARTED);
    }

    /**
     * Notify classes that extends View (CLI and GUI) that permissions has changed
     */
    void notifyPermissionsChanged(){
        //no behaviour in common between CLI and GUI
    }

    /**
     * Notify classes that extends View (CLI and GUI) that game variables changed
     */
    void notifyGameVariablesChanged(){
        //no behaviour in common between CLI and GUI
    }


    // MESSAGES HANDLING

    /**
     * Sends the given message to server
     * @param m the message to send to server
     */
    void sendMessage(Message m){
        try {
            this.client.sendMessage(m);
        } catch (NetworkingException e) {
            errorMessage(ERROR_SENDING_MESSAGE.concat(m.toString()));
            //TODO: check if this must be removed in production
        } catch (NullPointerException ex){
            errorMessage(MUST_CONNECT);
        }
    }

    /**
     * Receives a message from server (this method is called by client)
     * @param m the received message
     */
    private void receiveMessage(Message m){

        if( state==ViewState.INACTIVE ){
            parseMessageOnInactiveState(m);
        } else {
            parseMessageOnActiveState(m);
        }
    }

    /**
     * Parse the received message if the current view state is "INACTIVE"
     * @param m the received message
     */
    private void parseMessageOnInactiveState(Message m){

        ViewBoundMessageType type = (ViewBoundMessageType) m.getType();

        switch (type) {
            case BACK_TO_GAME:
                handleBackToGameEvent();
                break;
            case GAME_ENDED:
                handleGameEndedEvent();
                break;
            case RANKINGS:
                handleRankingsEvent(m);
                break;
            default:
                //No other messages are evaluated in this state
                break;
        }
    }

    /**
     * Parse the received message if the current view state is "ACTIVE"
     * @param m the received message
     */
    private void parseMessageOnActiveState(Message m){

        ViewBoundMessageType type = (ViewBoundMessageType) m.getType();
        System.out.println("RECEIVED:"+type.toString());

        switch (type) {
            case ERROR_MESSAGE:
                handleErrorEvent(m);
                break;
            case ACKNOWLEDGMENT_MESSAGE:
                handleAcknowledgmentEvent(m);
                break;
            case A_PLAYER_BECOME_INACTIVE:
                handleInactivePlayerEvent(m);
                break;
            case YOU_ARE_INACTIVE:
                handleInactiveEvent();
                break;
            case DISTRIBUTION_OF_WINDOW_PATTERNS:
                handleGiveWindowPatternsEvent(m);
                break;
            case GAME_ENDED:
                handleGameEndedEvent();
                break;
            case SETUP:
                handleSetupEvent(m);
                break;
            case NEW_ROUND:
                handleNewRoundEvent(m);
                break;
            case NEW_TURN:
                handleNewTurnEvent(m);
                break;
            case USED_TOOLCARD:
                handleUsedToolCardEvent(m);
                break;
            case RANKINGS:
                handleRankingsEvent(m);
                break;
            case SOMETHING_CHANGED_IN_WINDOWPATTERN:
                handleUpdatedWindowPatternEvent(m);
                break;
            case SOMETHING_CHANGED_IN_DRAFTPOOL:
                handleChangedDraftPoolEvent(m);
                break;
            case DRAFTED_DICE:
                handleDraftedDiceEvent(m);
                break;
            case TRACK_CHOSEN_DICE:
                handleTrackChosenDiceEvent(m);
                break;
            case SLOT_OF_TRACK_CHOSEN_DICE:
                handleSlotOfTrackChosenDice(m);
                break;
            case IT_IS_YOUR_TURN: //needed just for setting permissions
                handleYourTurnEvent();
                break;
            case BAD_FORMATTED:
                handleBadFormattedEvent();
                break;
            case JOIN_WR_DENIED_PLAYING:
                handleDeniedPlayingEvent();
                break;
            case JOIN_WR_DENIED_NICKNAME:
                handleDeniedNicknameEvent();
                break;
            case JOIN_WR_DENIED_LIMIT:
                handleDeniedLimitEvent();
                break;
            case ADDED_TO_WR:
                handleAddedEvent();
                break;
            case REMOVED_FROM_WR:
                handleRemovedEvent();
                break;
            case PLAYER_ADDED_TO_WR:
                handlePlayerAddedToWREvent(m);
                break;
            case PLAYER_REMOVED_FROM_WR:
                handlePlayerRemovedFromWREvent(m);
                break;
            default:
                //No other messages are evaluated in this state
                break;
        }

        if(type!=ViewBoundMessageType.ERROR_MESSAGE){
            //UPDATE PERMISSIONS
            EnumSet<Move> p = (EnumSet<Move>) m.getPermissions();
            if(!p.isEmpty()){
                setPermissions(p);
            }//else keep same permissions
        }
    }


    //UTILS

    /**
     * Display a message to the user. Abstract, so it is implemented differently by CLI and GUI.
     * @param message the message to be displayed
     */
    abstract void showMessage(String message);

    /**
     * Display an error message to the user. Abstract, so it is implemented differently by CLI and GUI.
     * @param message the error message to be displayed
     */
    abstract void errorMessage(String message);

    /**
     * Connects View to the server, creating a new Client instance.
     * @param type the connection's type
     * @param serverName the server's name
     * @param port the server's port to connect to
     */
    void connectToRemoteServer(ConnectionType type, String serverName, int port){

        if(client==null){ //client is effectively final
            this.client = new Client(type,serverName,port,this, false);
        }
    }

    /**
     * Changes view's state to the one specified
     * @param state the state to be setted as current one
     */
    private void changeStateTo(ViewState state){

        if(state==ViewState.INACTIVE){
            setPermissions(EnumSet.of(Move.BACK_GAME));
        } else {
            setPermissions(EnumSet.noneOf(Move.class));
        }

        this.state = state;
    }

    @Override
    public boolean update(Message m) {
        receiveMessage(m);
        return true;
    }


    // GETTERS

    /**
     * Returns the view's player's id
     * @return the view's player's id
     */
    public String getPlayerID() {
        return playerID;
    }

    /**
     * Returns the current permissions
     * @return the current permissions
     */
    public Set<Move> getPermissions() {
        return permissions;
    }

    /**
     * @return the view's player private objective card
     */
    public PrivateObjectiveCard getPrivateObjectiveCard() {
        //TODO: change this temporary implementation
        ObjectiveCardManager manager = new ObjectiveCardManager();
        return manager.getPrivateObjectiveCard();
    }


    /*  SETTERS
        The following methods are not commented because they are self explaining
     */

    //TODO: verificare perchè alcuni sono segnalati da IntelliJ come possibilmente spostabili a "private"

    /**
     * @see View#playerID
     */
    public void setPlayer(String playerID) {
        this.playerID = playerID;
    }

    /**
     * @see View#permissions
     */
    public void setPermissions(Set<Move> permissions) {
        this.permissions = (EnumSet<Move>)permissions;
        notifyPermissionsChanged();
    }

    /**
     * @see View#drawnToolCards
     */
    public void setDrawnToolCards(List<ToolCard> drawnToolCards) {
        this.drawnToolCards = drawnToolCards;
    }

    /**
     * @see View#drawnPublicObjectiveCards
     */
    public void setDrawnPublicObjectiveCards(List<PublicObjectiveCard> drawnPublicObjectiveCards) {
        this.drawnPublicObjectiveCards = drawnPublicObjectiveCards;
    }

    /**
     * @see View#players
     */
    public void setPlayers(List<String> players) {
        this.players = players;
    }

    /**
     * @see View#track
     */
    public void setTrack(Track track) {
        this.track = track;
    }

    /**
     * @see View#draftPoolDices
     */
    public void setDraftPoolDices(List<Dice> draftPoolDices) {
        this.draftPoolDices = draftPoolDices;
    }

    /**
     * @see View#roundNumber
     */
    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    /**
     * @see View#playingPlayerID
     */
    public void setPlayingPlayerID(String playingPlayerID) {
        this.playingPlayerID = playingPlayerID;
    }

    /**
     * @see View#draftedDice
     */
    public void setDraftedDice(Dice draftedDice) {
        this.draftedDice = draftedDice;
    }

    /**
     * @see View#windowPattern
     */
    public void setWindowPattern(WindowPattern windowPattern) {
        this.windowPattern = windowPattern;
    }

    /**
     * @see View#privateObjectiveCard
     */
    public void setPrivateObjectiveCard(PrivateObjectiveCard privateObjectiveCard) {
        this.privateObjectiveCard = privateObjectiveCard;
    }

    /**
     * @see View#windowPatterns
     */
    public void setWindowPatterns(List<WindowPattern> windowPatterns) {
        this.windowPatterns = windowPatterns;
    }
}
