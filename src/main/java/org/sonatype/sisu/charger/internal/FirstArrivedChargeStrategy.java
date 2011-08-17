package org.sonatype.sisu.charger.internal;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * ChargeStrategy for "first with payload or unhandled exception". This strategy will block as long as first Callable
 * delivers some payload or fails with unhandled exception -- making whole Charge to fail. In case of "bail out", the
 * next Callable is processed in same way, as long as there are Callables.
 * 
 * @author cstamas
 * @param <E>
 */
public class FirstArrivedChargeStrategy<E>
    extends AbstractChargeStrategy<E>
{
    private volatile ChargeWrapper<E> first = null;

    private CountDownLatch firstFound = new CountDownLatch( 1 );

    @Override
    public boolean isDone( final Charge<E> charge, final ChargeWrapper<E> wrapper )
    {
        first = wrapper;

        firstFound.countDown();

        return true;
    }

    @Override
    public List<E> getResult( final Charge<E> charge )
        throws Exception
    {
        if ( charge.getAmmoFutures().isEmpty() )
        {
            return Collections.emptyList();
        }
        else
        {
            firstFound.await();

            return Collections.singletonList( getFutureResult( first ) );
        }
    }

    @Override
    public boolean isDone( Charge<E> eCharge )
    {
        ChargeState state;
        return ( state = states.get( eCharge ) ) != null && state.wrapper != null;
    }
}
