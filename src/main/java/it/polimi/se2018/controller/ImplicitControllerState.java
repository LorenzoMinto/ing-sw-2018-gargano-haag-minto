package it.polimi.se2018.controller;


/**
 * @author Federico Haag
 */
abstract class ImplicitControllerState extends ControllerState {

    /**
     * String to be printed if a method that should not be called in this state is actually called
     */
    private static final String IMPLICIT_WARNING= "This is an implicit state.";

    /**
     * Constructor of the Implicit Controller State
     *
     * @param controller the controller to which the state has to be assigned
     */
    ImplicitControllerState(Controller controller) {
        if (controller==null) { throw new IllegalArgumentException("Can't create a State Controller without a Controller");}
        this.controller = controller;
        this.defaultMessage = IMPLICIT_WARNING;
    }
}
