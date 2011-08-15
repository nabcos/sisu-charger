package org.sonatype.sisu.charger.internal;

import java.util.List;

import org.sonatype.sisu.charger.ChargeFuture;

import com.google.common.base.Preconditions;

/**
 * Handle to a Charge's future. Despite it's name, it is NOT java.concurrent.Future implementor!
 * 
 * @author cstamas
 * @param <E>
 */
public class DefaultChargeFuture<E>
    implements ChargeFuture<E>
{
    private final Charge<E> charge;

    public DefaultChargeFuture( final Charge<E> charge )
    {
        this.charge = Preconditions.checkNotNull( charge );
    }

    /**
     * Cancels the execution of charge.
     * 
     * @return
     */
    public boolean cancel( final boolean mayInterruptIfRunning )
    {
        return charge.cancel( mayInterruptIfRunning );
    }

    /**
     * Returns true if charge "is done with work" according to it's strategy (does not mean all the Ammunition is
     * done!).
     * 
     * @return
     */
    public boolean isDone()
    {
        return charge.isDone();
    }

    /**
     * Returns the charge' results. This method BLOCKS as long Charge is not done.
     * 
     * @return
     * @throws Exception
     */
    public List<E> getResult()
        throws Exception
    {
        return charge.getResult();
    }
}
