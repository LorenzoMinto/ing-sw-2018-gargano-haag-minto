package it.polimi.se2018.model;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ColorPlacementRuleDecoratorTest {

    private static Cell[][] pattern;

    private PlacementRule rule;
    private PlacementRule decoratedRule;

    private WindowPattern windowPattern;

    private Dice redDice;
    private Dice blueDice;

    @BeforeClass
    public static void setUpClass() throws Exception {
        pattern = new Cell[3][3];
        for(int i=0; i<3; i++){
            for(int j=0; j<3; j++){
                pattern[i][j] = new Cell();
            }
        }
        pattern[1][1] = new Cell(0, DiceColor.RED);
    }

    @Before
    public void setUp() throws Exception {
        PlacementRule emptyRule = new EmptyPlacementRule();
        rule = new ColorPlacementRuleDecorator(emptyRule);
        decoratedRule = new ColorPlacementRuleDecorator(new AdjacentColorPlacementRuleDecorator(emptyRule));

        windowPattern = new WindowPattern("", "", "",0, pattern);
        redDice = new Dice(DiceColor.RED);
        blueDice = new Dice(DiceColor.BLUE);
    }

    @Test
    public void testCheckIfMoveIsAllowedWhenMoveIsOnAlreadyPlacedDice() {
        windowPattern.putDiceOnCell(redDice, 1,1);
        assertFalse(rule.isMoveAllowed(windowPattern, redDice, 1,1));
    }

    @Test
    public void testCheckIfMoveIsAllowed() {
        assertTrue(decoratedRule.isMoveAllowed(windowPattern, redDice, 1, 1));
    }

    @Test
    public void testCheckIfMoveIsAllowedWhenNotAllowed() {
        assertFalse(decoratedRule.isMoveAllowed(windowPattern, blueDice, 1, 1));
    }

    @Test
    public void testCheckIfMoveIsAllowedIfDecoratedNotAllowed() {
        windowPattern.putDiceOnCell(redDice, 1,0);
        assertFalse(decoratedRule.isMoveAllowed(windowPattern, redDice, 1, 1));
    }
}