package it.polimi.se2018.model;

/**
 * {@link PlacementRule} decorator to enforce the following rule: a dice must be adjacent to a previously
 * placed die, orthogonally or diagonally. If invert is set to true on construction then enforces the following:
 * a dice must NOT be adjacent to any previously placed dice.
 *
 * @author Lorenzo Minto
 */
public class AdjacentDicePlacementRuleDecorator extends PlacementRuleDecorator {


    /**
     * If set to true checks whether dice to be placed has no adjacent dice.
     */
    private boolean invert;

    /**
     * Class constructor specifying {@link PlacementRule} to be decorated.
     *
     * @param decoratedPlacementRule rule to be decorated.
     * @param invert if set to true checks whether dice to be placed has no adjacent dice.
     */
    public AdjacentDicePlacementRuleDecorator(PlacementRule decoratedPlacementRule, boolean invert){
        super(decoratedPlacementRule);
        this.invert = invert;
    }

    /**
     * Checks if the move specified by a given {@link Dice} and a position (row and col) is allowed on
     * the given {@link WindowPattern}.
     *
     * @param windowPattern the window pattern on which the move is played.
     * @param dice the dice to be placed.
     * @param row  the row index of the cell where the dice is to be placed.
     * @param col  the column index of the cell where the dice is to be placed.
     * @return whether or not the move is allowed.
     */
    @Override
    public boolean checkIfMoveIsAllowed(WindowPattern windowPattern, Dice dice, int row, int col) {
        boolean adjacency = checkAdjacentDiceConstraints(windowPattern, row, col);
        return  invert ? !adjacency : adjacency &&
                decoratedPlacementRule.checkIfMoveIsAllowed(windowPattern, dice, row, col);
    }

    /**
     * Checks if the move specified by a given {@link Dice} and a position (row and col) complies with the dice
     * adjacency constraint on the given {@link WindowPattern}.
     *
     * @param windowPattern the window pattern on which the move is played.
     * @param row the row index of the cell where the dice is to be placed.
     * @param col the column index of the cell where the dice is to be placed.
     * @return whether or not the move complies with the dice adjacency constraint, or, in case invert is set to true,
     * whether or not the dice to be placed is adjacent to any other dice on the window pattern.
     *
     * @see AdjacentColorPlacementRuleDecorator for formal rule.
     */
    private boolean checkAdjacentDiceConstraints(WindowPattern windowPattern, int row, int col){
        Cell[][] pattern = windowPattern.getPattern();
        int numberOfRows = windowPattern.getNumberOfRows();
        int numberOfColumns = windowPattern.getNumberOfColumns();

        return (row != 0 && pattern[row-1][col].hasDice()) ||
                (row!=0 && col!=0 && pattern[row-1][col-1].hasDice()) ||
                (col != numberOfColumns-1 && pattern[row][col+1].hasDice()) ||
                (row!=0 && col!=numberOfColumns-1 && pattern[row-1][col+1].hasDice()) ||
                (row != numberOfRows-1 && pattern[row+1][col].hasDice()) ||
                (row!=numberOfRows-1 && col!=0 && pattern[row+1][col-1].hasDice()) ||
                (col != 0 && pattern[row][col-1].hasDice()) ||
                (row!=numberOfRows-1 && col!=numberOfColumns-1 && pattern[row+1][col+1].hasDice());

    }


}
