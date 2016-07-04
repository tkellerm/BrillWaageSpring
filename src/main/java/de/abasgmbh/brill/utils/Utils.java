/**
 * creation date: Jul 04, 2016
 * first author: marco
 * maintained by: marco
 * <p>
 * (C) Copyright abas Software AG, Karlsruhe, Germany; 2016
 */
package de.abasgmbh.brill.utils;

public class Utils {

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // ignore it
        }

    }
}
