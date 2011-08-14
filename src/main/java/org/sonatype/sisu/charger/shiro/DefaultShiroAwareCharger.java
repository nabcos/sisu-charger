package org.sonatype.sisu.charger.shiro;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.shiro.SecurityUtils;
import org.sonatype.sisu.charger.ChargeStrategy;
import org.sonatype.sisu.charger.internal.Charge;
import org.sonatype.sisu.charger.internal.DefaultCharger;

@Singleton
@Named( "shiro" )
public class DefaultShiroAwareCharger
    extends DefaultCharger
{
    @Override
    protected <E> Charge<E> getChargeInstance( final ChargeStrategy<E> strategy )
    {
        return new ShiroAwareCharge<E>( strategy, SecurityUtils.getSubject() );
    }
}
