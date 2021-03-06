/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ocpplugin.api.ocp.device.handlers;

/**
 * ocpplugin-api
 * org.opendaylight.ocpplugin.api.ocp.device.handlers
 *
 * Interface has to implement all relevant manager to correctly handling
 * device initialization. DeviceManager to RpcManger and back
 * to DeviceManager. DeviceManager add new Device to MD-SAL Operational DataStore.
 */
public interface DeviceInitializator {

    /**
     * Method sets relevant {@link DeviceInitializationPhaseHandler} for building
     * handler's chain for new Device initial phase.
     * 1) DeviceManager has to add all descriptions and features
     * 2) RpcManager has to register all RPC services
     * 3) DeviceManager has to add new Device to MD-SAL dataStore
     *
     * @param handler
     */
    void setDeviceInitializationPhaseHandler(DeviceInitializationPhaseHandler handler);
}
