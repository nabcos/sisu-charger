package org.sonatype.sisu.charger;

import java.util.List;

import org.sonatype.sisu.charger.internal.Charge;
import org.sonatype.sisu.charger.internal.ChargeWrapper;

/**
 * Charge strategy is in charge (pun!) to "drive" how a charge should execute or finish.
 * 
 * @author cstamas
 */
public interface ChargeStrategy
{
    /**
     * Checks whether the given charge is to be considered 'done' for this strategy.
     */
    <E> boolean isDone( Charge<E> charge );

    /**
     * Marks the given wrapper for the given charge as done with execution.
     */
    <E> void setDone( Charge<E> charge, ChargeWrapper<E> wrapper );

    /**
     * Returns the results of the given charge. May block to wait until the charge is done.
     *
     * @see #isDone(org.sonatype.sisu.charger.internal.Charge)
     */
    <E> List<E> getResult( Charge<E> charge )
        throws Exception;
}
