package it.polimi.se2018.utils;

/**
 * Enum of all types of view bound messages
 */
public enum ViewBoundMessageType {
    ERROR_MESSAGE,
    ACKNOWLEDGMENT_MESSAGE,
    A_PLAYER_BECOME_INACTIVE,
    BACK_TO_GAME,
    YOU_ARE_INACTIVE,
    DISTRIBUTION_OF_WINDOW_PATTERNS,
    GAME_ENDED,
    SETUP,
    NEW_ROUND,
    NEW_TURN,
    USED_TOOLCARD,
    RANKINGS,
    SOMETHING_CHANGED_IN_WINDOWPATTERN,
    SOMETHING_CHANGED_IN_DRAFTPOOL,
    DRAFTED_DICE,
    TRACK_CHOSEN_DICE,
    SLOT_OF_TRACK_CHOSEN_DICE,
    IT_IS_YOUR_TURN,
    BAD_FORMATTED,
    JOIN_WR_DENIED_PLAYING,
    JOIN_WR_DENIED_NICKNAME,
    JOIN_WR_DENIED_LIMIT,
    ADDED_TO_WR,
    REMOVED_FROM_WR,
    PLAYER_ADDED_TO_WR,
    SOMETHING_CHANGED_IN_TRACK, PLAYER_REMOVED_FROM_WR
}
