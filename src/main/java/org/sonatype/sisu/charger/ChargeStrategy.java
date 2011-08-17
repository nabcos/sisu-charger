package org.sonatype.sisu.charger;

import java.util.List;

import org.sonatype.sisu.charger.internal.Charge;
import org.sonatype.sisu.charger.internal.ChargeWrapper;

/**
 * Charge strategy is in charge (pun!) to "drive" how a charge should execute or finish.
 * 
 * @author cstamas
 * @param <E>
 */
public interface ChargeStrategy<E>
{
    /**
     * Checks whether the given charge is to be considered 'done' for this strategy.
     */
    boolean isDone( Charge<E> charge );

    /**
     * Marks the given wrapper for the given charge as done with execution.
     */
    void setDone( Charge<E> charge, ChargeWrapper<E> wrapper );

    /**
     * Returns the results of the given charge. May block to wait until the charge is done.
     *
     * @see #isDone(org.sonatype.sisu.charger.internal.Charge)
     */
    List<E> getResult( Charge<E> charge )
        throws Exception;
}
