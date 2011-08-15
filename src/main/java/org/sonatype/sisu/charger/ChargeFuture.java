package org.sonatype.sisu.charger;

import java.util.List;

/**
 * Handle to a Charge's future. Despite it's name, it is NOT java.concurrent.Future implementor!
 * 
 * @author cstamas
 * @param <E>
 */
public interface ChargeFuture<E>
{
    /**
     * Cancels the Charge.
     * 
     * @param mayInterruptIfRunning
     * @return
     */
    boolean cancel( boolean mayInterruptIfRunning );

    /**
     * Returns true if charge "is done with work" according to it's strategy (does not mean all the Ammunition is
     * done!).
     * 
     * @return
     */
    boolean isDone();

    /**
     * Returns the charge' results. This method BLOCKS as long Charge is not done.
     * 
     * @return
     * @throws Exception
     */
    List<E> getResult()
        throws Exception;
}
