package org.sonatype.sisu.charger.internal;

import java.util.List;

import org.sonatype.sisu.charger.ChargeStrategy;

/**
 * Strategy that will ensure all the payloads are here, are bailed out, or did fail.
 * 
 * @author cstamas
 */
public class AllArrivedChargeStrategy
    extends AbstractChargeStrategy
{
    public static final ChargeStrategy INSTANCE = new AllArrivedChargeStrategy();

    @Override
    public <E> void setDone( Charge<E> eCharge, ChargeWrapper<E> eChargeWrapper )
    {
        // noop, don't need this
    }

    @Override
    public <E> boolean isDone( final Charge<E> charge )
    {
        // done if all done, otherwise not
        List<ChargeWrapper<E>> ammoFutures = charge.getAmmoFutures();

        for ( ChargeWrapper<E> a : ammoFutures )
        {
            if ( !a.getFuture().isDone() )
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public <E> List<E> getResult( final Charge<E> charge )
        throws Exception
    {
        return getAllResults( charge );
    }
}
