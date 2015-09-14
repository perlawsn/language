package org.dei.perla.lang.query.statement;

/**
 * Available sampling rate policies.
 *
 * <ul>
 * <li>STRICT: the query executor will try to enforce the sampling rate
 * selected by the user. No sampling will be performed if the sampling rate
 * is not supported by the network devices</li>
 *
 * <li>ADAPTIVE: the query executor will adapt the sampling rate according to
 * the capabilities of the sampling network. This functionality requires
 * the FPC to signal that the sampling rate is unsupported using the
 * unsupported-rate script instruction.</li>
 * </ul>
 *
 * @author Guido Rota 13/03/15.
 */
public enum RatePolicy {

    ADAPTIVE,
    STRICT

}
