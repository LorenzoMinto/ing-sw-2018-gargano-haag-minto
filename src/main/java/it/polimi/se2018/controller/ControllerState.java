package it.polimi.se2018.controller;

import it.polimi.se2018.model.Dice;
import it.polimi.se2018.model.Player;
import it.polimi.se2018.model.ToolCard;
import it.polimi.se2018.networking.message.ControllerMessage;
import it.polimi.se2018.networking.message.Message;

/**
 * Abstract class that represents a generic state of the {@link Controller}.
 *
 *  @author Lorenzo Minto
 *  @author Federico Haag (refactor)
 */
public abstract class ControllerState {

    /**
     * String to be printed if a non pertaining method is called in the middle of a ToolCard effect.
     */
    static final ControllerMessage MIDDLE_OF_EFFECT = new ControllerMessage("Can't do. You are in the middle of " +
            "a Toolcard effect");

    /**
     * String to be printed if a method that requires drafting is called before any dice was drafted.
     */
    static final ControllerMessage NO_DICE_DRAFTED = new ControllerMessage("Can't do. No dice has been drafted.");

    /**
     * String to be printed if only action possible is to draft and place.
     */
    static final ControllerMessage ONLY_DRAFT_AND_PLACE = new ControllerMessage("Can't do. You can only draft and place");

    /**
     * String to be printed if there's a drafted dice that needs to be placed.
     */
    static final ControllerMessage PLACE_DICE = new ControllerMessage("Can't do. You have to place the drafted dice");

    /**
     * String to be printed if the only action possible is a ToolCard activation.
     */
    static final ControllerMessage TOOLCARD_ONLY = new ControllerMessage("Can't do. You have already drafted and placed." +
            " You can only use a Toolcard");

    /**
     * Default message
     */
    ControllerMessage defaultMessage = new ControllerMessage("Default Message. Actually I don't know what to say.");


    /**
     * Reference to the {@link Controller} to which this state belongs.
     */
    protected Controller controller;


    /**
     * Drafts a specified dice from the current draft pool and sets it as the drafted dice on the current turn.
     *  @param dice the dice to be drafted.
     *
     */
    public Message draftDiceFromDraftPool(Dice dice){ return defaultMessage; }

    /**
     * Places the drafted dice of the current turn on the specified cell (row and column).
     *  @param row the row index of the cell where the dice is to be placed.
     * @param col the column index of the cell where the dice is to be placed.
     */
    public Message placeDice(int row, int col){ return defaultMessage; }

    /**
     * Activates, if allowed, the effect of the passed tool card by initiating the relative state succession. The
     * passed toolCard is set as activeToolCard in the Controller.
     *  @param player the player requesting the action.
     * @param toolcard the toolcard to be activated.
     */
    public Message useToolCard(Player player, ToolCard toolcard){ return defaultMessage; }

    /**
     * Removes the specified dice from the specified track slot and sets it as trackChosenDice on the current turn.
     *  @param dice the chosen dice.
     * @param slotNumber the number of the TrackSlot where the dice is.
     */
    public Message chooseDiceFromTrack(Dice dice, int slotNumber){ return defaultMessage; }

    /**
     * Moves, if legal, the dice found at the cell designated by (rowFrom, colFrom) to the cell designated by (rowTo,
     * colTo).
     *  @param rowFrom the row index of the cell FROM which the dice is to be moved.
     * @param colFrom the column index of the cell FROM which the dice is to be moved.
     * @param rowTo the row index of the cell TO which the dice is to be moved.
     * @param colTo the column index of the cell TO which the dice is to be moved.
     */
    public Message moveDice(int rowFrom, int colFrom, int rowTo, int colTo){ return defaultMessage; }


    /**
     * Increments the value of the drafted dice found in the current turn.
     *
     */
    public Message incrementDice(){ return defaultMessage; }


    /**
     * Decrements the value of the drafted dice found in the current turn.
     *
     */
    public Message decrementDice(){ return defaultMessage; }

    /**
     * Sets the value of the drafted dice found in the current turn to the specified value.
     *  @param value the chosen value for the drafted dice.
     *
     */
    public Message chooseDiceValue(int value){ return defaultMessage; }

    /**
     * Executes some implicit behaviour relative to the state. It does nothing when state is not Implicit.
     */
    public void executeImplicitBehaviour(){
        //do nothing by default
    }

}
