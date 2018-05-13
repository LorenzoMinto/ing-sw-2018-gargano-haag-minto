package it.polimi.se2018.controller;

import it.polimi.se2018.model.*;

/**
 *  @author Lorenzo Minto
 *  @author Federico Haag (refactor)
 */
public class RollOverDraftedControllerState extends ImplicitControllerState {

    /**
     * Class constructor.
     *
     * @param controller the controller of which this class is going to act as a state.
     */
    public RollOverDraftedControllerState(Controller controller) {
        super(controller);
    }

    /**
     * Rolls over (opposite face up) the drafted dice of the current turn.
     */
    @Override
    public void executeImplicitBehaviour() {
        Turn currentTurn = controller.game.getCurrentRound().getCurrentTurn();
        Dice draftedDice = currentTurn.getDraftedDice();
        draftedDice.rollOver();
        currentTurn.setDraftedDice(draftedDice);
        controller.setControllerState(controller.stateManager.getNextState(this));
    }
}
