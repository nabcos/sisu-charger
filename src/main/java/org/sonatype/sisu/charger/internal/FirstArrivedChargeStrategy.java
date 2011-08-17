package org.sonatype.sisu.charger.internal;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CountDownLatch;

import org.sonatype.sisu.charger.ChargeStrategy;

/**
 * ChargeStrategy for "first with payload or unhandled exception". This strategy will block as long as first Callable
 * delivers some payload or fails with unhandled exception -- making whole Charge to fail. In case of "bail out", the
 * next Callable is processed in same way, as long as there are Callables.
 *
 * @author cstamas
 */
public class FirstArrivedChargeStrategy
    extends AbstractChargeStrategy
{
    public static final ChargeStrategy INSTANCE = new FirstArrivedChargeStrategy();

    private Map<Charge<?>, ChargeState> states = new WeakHashMap<Charge<?>, ChargeState>( 4 );

    private static class ChargeState<E>
    {
        CountDownLatch latch;

        ChargeWrapper<E> wrapper;

        int completed;

        public E result;

        private ChargeState()
        {
            this.completed = 0;
        }

    }

    public synchronized <E> void setDone( final Charge<E> charge, final ChargeWrapper<E> wrapper )
    {
        ChargeState<E> state;

        synchronized ( states )
        {
            if ( ( state = states.get( charge ) ) == null )
            {
                states.put( charge, ( state = new ChargeState() ) );
            }
        }

        // this method is synchronized, so the only contestants for the state lock are this synchronized block and the on in getResult.
        synchronized ( state )
        {
            state.wrapper = wrapper;
            state.completed++;
            state.notifyAll();
        }
    }

    public <E> List<E> getResult( final Charge<E> charge )
        throws Exception
    {
        if ( charge.getAmmoFutures().isEmpty() )
        {
            return Collections.emptyList();
        }
        else
        {
            ChargeState<E> state;

            synchronized ( states )
            {
                if ( ( state = states.get( charge ) ) == null )
                {
                    states.put( charge, ( state = new ChargeState() ) );
                }
            }

            // shortcut if we already have a result
            if ( state.result != null )
            {
                return Collections.singletonList( state.result );
            }

            synchronized ( state )
            {
                // as long as we have the chance to get a result...
                do
                {
                    // ... wait for new results to arrive and ...
                    if ( state.wrapper == null )
                    {
                        state.wait();
                    }

                    // ( if we have multiple callers waiting for a result, one will might have set a result before we are running)
                    if ( state.result == null )
                    {
                        state.result = getFutureResult( state.wrapper );
                    }

                    if ( state.result != null )
                    {
                        // ... return result if it was valid
                        return Collections.singletonList( state.result );
                    }

                    // result was a handled exception in wrapper, wait for next result (or exit while loop via condition)
                    state.wrapper = null;
                }
                while ( state.completed < charge.getAmmoFutures().size() );

                return Collections.emptyList();
            }
        }
    }

    public <E> boolean isDone( Charge<E> eCharge )
    {
        ChargeState state;
        return ( state = states.get( eCharge ) ) != null && state.wrapper != null;
    }
}
