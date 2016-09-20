package br.com.psava.rhythmgame.interfaces;

import br.com.psava.rhythmgame.classes.MovementAction;

/**
 * Created by patricksava on 19/09/16.
 */

public interface MovementActionClickListener {
    void handleMovementAction(MovementAction ma, int hitPoints);
}
