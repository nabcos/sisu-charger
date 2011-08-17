package org.sonatype.sisu.charger.internal;

import java.util.Collections;
import java.util.List;

import org.sonatype.sisu.charger.ChargeStrategy;


/**
 * ChargeStrategy for "first with payload or unhandled exception". This strategy will block as long as first Callable
 * delivers some payload or fails with unhandled exception -- making whole Charge to fail. In case of "bail out", the
 * next Callable is processed in same way, as long as there are Callables.
 * 
 * @author cstamas
 */
public class FirstArrivedInOrderChargeStrategy
    extends AbstractChargeStrategy
{
    public static final ChargeStrategy INSTANCE = new FirstArrivedInOrderChargeStrategy();

    @Override
    public <E> boolean isDone( final Charge<E> charge )
    {
        List<ChargeWrapper<E>> ammoFutures = charge.getAmmoFutures();

        for ( ChargeWrapper<E> a : ammoFutures )
        {
            if ( a.getFuture().isDone() )
            {
                try
                {
                    if ( getFutureResult( a ) != null )
                    {
                        return true;
                    }
                }
                catch ( Exception e )
                {
                    // nope, not done but failed badly
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public <E> List<E> getResult( final Charge<E> charge )
        throws Exception
    {
        final List<ChargeWrapper<E>> futures = charge.getAmmoFutures();

        for ( ChargeWrapper<E> f : futures )
        {
            E e = getFutureResult( f );

            if ( e != null )
            {
                return Collections.singletonList( e );
            }
        }

        return Collections.emptyList();
    }

    @Override
    public <E> void setDone( Charge<E> eCharge, ChargeWrapper<E> eChargeWrapper )
    {
        // noop, don't need this
    }
}
