package org.sonatype.sisu.charger.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private Map<Charge<?>, ChargeState> states = new HashMap<Charge<?>, ChargeState>( 4 );

    private static class ChargeState<E>
    {
        CountDownLatch latch;

        ChargeWrapper<E> wrapper;

        private ChargeState()
        {
            this.latch = new CountDownLatch( 1 );
        }

        private ChargeState( ChargeWrapper<E> wrapper )
        {
            this.latch = new CountDownLatch( 0 );
            this.wrapper = wrapper;
        }
    }

    public <E> void setDone( final Charge<E> charge, final ChargeWrapper<E> wrapper )
    {
        ChargeState<E> state;

        synchronized ( states )
        {
            if ( ( state = states.get( charge ) ) == null )
            {
                states.put( charge, ( state = new ChargeState( wrapper ) ) );
            }

            state.wrapper = wrapper;
            state.latch.countDown();
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

            state.latch.await();

            return Collections.singletonList( getFutureResult( state.wrapper ) );
        }
    }

    public <E> boolean isDone( Charge<E> eCharge )
    {
        ChargeState state;
        return ( state = states.get( eCharge ) ) != null && state.wrapper != null;
    }
}
