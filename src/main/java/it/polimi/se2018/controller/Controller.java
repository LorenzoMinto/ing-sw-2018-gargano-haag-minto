package it.polimi.se2018.controller;

import it.polimi.se2018.model.*;

import java.util.List;

public class Controller {

    //TODO: check access permissions to the following attributes

    Game game;  //Reference to the Model

    ControllerState controllerState; //State pattern

    ToolCard activeToolcard;

    DiceBag diceBag;

    //Controller states
    StartControllerState startState;
    PlaceControllerState placeState;

    //Getters for controller states
    public StartControllerState getStartState() {
        return startState;
    }
    public PlaceControllerState getPlaceState() {
        return placeState;
    }


    //Constructor
    public Controller(Game game) {

        //Set controller states
        this.startState = new StartControllerState(this);
        this.placeState = new PlaceControllerState(this);

        //Set main attributes
        this.game = game;
        this.controllerState =  this.startState;
        this.activeToolcard = null;
        this.diceBag = new DiceBag(18); //TODO: read this number from config file
    }

    //State pattern
    public void setControllerState(ControllerState controllerState) {
        this.controllerState = controllerState;
        //could change controllerState implicitly
        this.controllerState.executeImplicitBehaviour();
    }


    //Handling View->Controller (Moves)

    public void handleMove(PlayerMove move) {
        if (game.currentRound.currentTurn.isCurrentPlayer(move.getPlayer())) {
            //switch that handles different kind of moves to different methods of current controllerState
            if (move instanceof UseToolCardPlayerMove) {

                controllerState.useToolCard(
                        move.getPlayer(),
                        ((UseToolCardPlayerMove)move).getToolcard()
                );

            }

            /*
            TODO: fill with all cases
            else if (move instanceof ) {

            }
            */
        } else {
            move.getView().reportError("Not your turn!");
        }
    }

    public boolean canUseSpecificToolCard(Player player, ToolCard toolCard) {
        //TODO: implement this method
        //define logic to check from Controller if toolcard is usable, these object are mere copies.

        return false; //placeholder waiting for implementation
    }

    public void setActiveToolCard(ToolCard toolCard) {
        toolCard.use();

        this.activeToolcard = toolCard;
        game.currentRound.currentTurn.setUsedToolCard(toolCard);
    }

    public ToolCard getActiveToolCard() {
        //TODO: make a copy
        return this.activeToolcard;
    }

    public boolean goToNextRound() {
        if (game.hasNextRound()) {
            int numberOfDices = game.players.size() * 2 + 1;
            List<Dice> dices = diceBag.getDices(numberOfDices);

            game.nextRound( dices );
            return true;
        }
        return false;
    }

    public boolean goToNextTurn() {
        if (game.currentRound.hasNextTurn()) {

            game.currentRound.nextTurn();
            return true;
        }
        return false;
    }

}
