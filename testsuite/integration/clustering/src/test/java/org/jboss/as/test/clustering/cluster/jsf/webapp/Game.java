/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.clustering.cluster.jsf.webapp;

import java.io.Serializable;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

@Named
@SessionScoped
public class Game implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 991300443278089016L;

    private int number;

    private int guess;
    private int smallest;

    @Inject
    @MaxNumber
    int maxNumber;

    private int biggest;
    private int remainingGuesses;

    @Inject
    @Random
    Instance<Integer> randomNumber;

    @SuppressWarnings("unchecked")
    @ProtoFactory
    static Game create(int number, int guess, int smallest, int biggest, int remainingGuesses) {
        Game game = new Game();
        game.number = number;
        game.guess = guess;
        game.smallest = smallest;
        game.biggest = biggest;
        game.remainingGuesses = remainingGuesses;
        try {
            game.maxNumber = (Integer) CDI.current().select(game.getClass().getDeclaredField("maxNumber").getAnnotation(MaxNumber.class)).get();
            game.randomNumber = (Instance<Integer>) (Instance<?>) CDI.current().select(game.getClass().getDeclaredField("randomNumber").getAnnotation(Random.class));
            return game;
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
    }

    @ProtoField(number = 1, defaultValue = "0")
    public int getNumber() {
        return number;
    }

    @ProtoField(number = 2, defaultValue = "0")
    public int getGuess() {
        return guess;
    }

    public void setGuess(int guess) {
        this.guess = guess;
    }

    @ProtoField(number = 3, defaultValue = "0")
    public int getSmallest() {
        return smallest;
    }

    @ProtoField(number = 4, defaultValue = "0")
    public int getBiggest() {
        return biggest;
    }

    @ProtoField(number = 5, defaultValue = "0")
    public int getRemainingGuesses() {
        return remainingGuesses;
    }

    public void check() {
        if (guess > number) {
            biggest = guess - 1;
        } else if (guess < number) {
            smallest = guess + 1;
        } else if (guess == number) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Correct!"));
        }
        remainingGuesses--;
    }

    @PostConstruct
    public void reset() {
        this.smallest = 0;
        this.guess = 0;
        this.remainingGuesses = 10;
        this.biggest = maxNumber;
        this.number = randomNumber.get();
    }

    public void validateNumberRange(FacesContext context, UIComponent toValidate, Object value) {
        if (remainingGuesses <= 0) {
            FacesMessage message = new FacesMessage("No guesses left!");
            context.addMessage(toValidate.getClientId(context), message);
            ((UIInput) toValidate).setValid(false);
            return;
        }
        int input = (Integer) value;

        if (input < smallest || input > biggest) {
            ((UIInput) toValidate).setValid(false);

            FacesMessage message = new FacesMessage("Invalid guess");
            context.addMessage(toValidate.getClientId(context), message);
        }
    }
}
