/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ocpplugin.api.ocp.connection;

import java.util.concurrent.ThreadPoolExecutor;
import org.opendaylight.ocpplugin.api.ocp.connection.HandshakeManager;

/**
 * OCP handshake context holder
 */
public interface HandshakeContext extends AutoCloseable {

    /**
     * @return handshakeManager
     */
    HandshakeManager getHandshakeManager();

    /**
     * @return handshake pool
     */
    ThreadPoolExecutor getHandshakePool();
}
