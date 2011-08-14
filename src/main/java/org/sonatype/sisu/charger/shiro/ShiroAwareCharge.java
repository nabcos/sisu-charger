package org.sonatype.sisu.charger.shiro;

import java.util.concurrent.Callable;

import org.apache.shiro.subject.Subject;
import org.sonatype.sisu.charger.ChargeStrategy;
import org.sonatype.sisu.charger.ExceptionHandler;
import org.sonatype.sisu.charger.internal.Charge;

/**
 * Amount of parallel workload.
 * 
 * @author cstamas
 * @param <E>
 */
public class ShiroAwareCharge<E>
    extends Charge<E>
{
    private final Subject subject;

    public ShiroAwareCharge( final ChargeStrategy<E> strategy, final Subject subject )
    {
        super( strategy );

        this.subject = subject;
    }

    public void addAmmo( final Callable<? extends E> callable, final ExceptionHandler exceptionHandler )
    {
        super.addAmmo( subject.associateWith( callable ), exceptionHandler );
    }
}
