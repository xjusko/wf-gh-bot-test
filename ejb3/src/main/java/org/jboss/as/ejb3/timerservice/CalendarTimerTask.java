/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.ejb3.timerservice;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.jboss.as.ejb3.timerservice.spi.TimedObjectInvoker;

/**
 * CalendarTimerTask
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class CalendarTimerTask extends TimerTask {

    public CalendarTimerTask(CalendarTimer calendarTimer) {
        super(calendarTimer);
    }

    @Override
    protected void callTimeout(TimerImpl timer) throws Exception {
        // if we have any more schedules remaining, then schedule a new task
        if (timer.getNextExpiration() != null && !timer.isInRetry()) {
            timer.scheduleTimeout(false);
        }
        invokeBeanMethod(timer);
    }

    @Override
    protected void invokeBeanMethod(TimerImpl timer) throws Exception {
        // finally invoke the timeout method through the invoker
        if (timer.isAutoTimer()) {
            CalendarTimer calendarTimer = (CalendarTimer) timer;
            TimedObjectInvoker invoker = this.timerService.getInvoker();
            // call the timeout method
            invoker.callTimeout(calendarTimer, calendarTimer.getTimeoutMethod());
        } else {
            this.timerService.getInvoker().callTimeout(timer);
        }
    }

    @Override
    protected Date calculateNextTimeout(TimerImpl timer) {
        // The next timeout for the calendar timer will have to be computed using the
        // current "nextExpiration"
        Date currentTimeout = timer.getNextExpiration();
        if (currentTimeout == null) {
            return null;
        }
        Calendar cal = new GregorianCalendar();
        cal.setTime(currentTimeout);
        // now compute the next timeout date
        Calendar nextTimeout = ((CalendarTimer) timer).getCalendarTimeout().getNextTimeout(cal);
        if (nextTimeout != null) {
            return nextTimeout.getTime();
        }
        return null;
    }

    @Override
    protected void scheduleTimeoutIfRequired(TimerImpl timer) {
        if (timer.getNextExpiration() != null) {
            timer.scheduleTimeout(false);
        }
    }

    @Override
    protected void postTimeoutProcessing(TimerImpl timer) throws InterruptedException {
        timer.lock();
        try {
            final TimerState timerState = timer.getState();
            if (timerState != TimerState.CANCELED
                    && timerState != TimerState.EXPIRED) {
                if (timer.getNextExpiration() == null) {
                    timerService.expireTimer(timer);
                } else {
                    timer.setTimerState(TimerState.ACTIVE, null);
                    // persist changes
                    timerService.persistTimer(timer, false);
                }
            }
        } finally {
            timer.unlock();
        }
    }

}
