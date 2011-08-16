package org.sonatype.sisu.charger.internal;

import java.util.Collections;
import java.util.List;


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
    @Override
    public boolean isDone( final Charge<E> charge )
    {
        List<ChargeWrapperFuture<E>> ammoFutures = charge.getAmmoFutures();

        for ( ChargeWrapperFuture<E> f : ammoFutures )
        {
            if ( f.isDone() )
            {
                try
                {
                    if ( getFutureResult( f ) != null )
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
    public List<E> getResult( final Charge<E> charge )
        throws Exception
    {
        final List<ChargeWrapperFuture<E>> futures = charge.getAmmoFutures();

        boolean stillUnfinished = true ;

        while ( stillUnfinished )
        {
            // lock charge to not miss notify of e.g. first result while we look at a later future
            synchronized ( charge )
            {
                int doneTasks = 0;

                for ( ChargeWrapperFuture<E> f : futures )
                {
                    if ( f.isDone() )
                    {
                        doneTasks++;

                        E e = getFutureResult( f );

                        if ( e != null )
                        {
                            charge.cancel( false );
                            return Collections.singletonList( e );
                        }
                    }
                }

                stillUnfinished = doneTasks != futures.size();

                if ( stillUnfinished )
                {
                    charge.wait();
                }
            }
        }

        // if we are here, all callables have no result or are failed
        return Collections.emptyList();
    }
}
