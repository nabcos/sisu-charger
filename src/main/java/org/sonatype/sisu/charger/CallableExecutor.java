package org.sonatype.sisu.charger;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

public interface CallableExecutor
{
    /**
     * Submits a value-returning task for execution and returns a Future representing the pending results of the task.
     * 
     * @param task the task to submit
     * @return a Future representing pending completion of the task
     * @throws RejectedExecutionException if the task cannot be scheduled for execution
     * @throws NullPointerException if the task is null
     */
    <T> Future<T> submit( Callable<T> task )
        throws RejectedExecutionException, NullPointerException;
}
