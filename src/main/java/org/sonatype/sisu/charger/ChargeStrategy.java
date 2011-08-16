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
    boolean isDone( Charge<E> charge, ChargeWrapper<E> wrapper );

    List<E> getResult( Charge<E> charge )
        throws Exception;
}
