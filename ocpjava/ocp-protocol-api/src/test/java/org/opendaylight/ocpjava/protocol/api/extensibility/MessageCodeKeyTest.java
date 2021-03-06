/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ocpjava.protocol.api.extensibility;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.ocpjava.protocol.api.keys.MessageCodeKey;
import org.opendaylight.ocpjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ocp.protocol.rev150811.HealthCheckInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ocp.protocol.rev150811.HealthCheckOutput;

/**
 * @author michal.polkorab
 *
 */
public class MessageCodeKeyTest {

    /**
     * Test MessageCodeKey equals and hashCode
     */
    @Test
    public void test() {
        MessageCodeKey key1 =
                new MessageCodeKey(EncodeConstants.OCP_VERSION_ID, 4, HealthCheckInput.class);
        MessageCodeKey key2 =
                new MessageCodeKey(EncodeConstants.OCP_VERSION_ID, 4, HealthCheckInput.class);
        Assert.assertTrue("Wrong equals", key1.equals(key2));
        Assert.assertTrue("Wrong hashcode", key1.hashCode() == key2.hashCode());
        key2 = new MessageCodeKey(EncodeConstants.OCP_VERSION_ID, 4, null);
        Assert.assertFalse("Wrong equals", key1.equals(key2));
        Assert.assertFalse("Wrong hashcode", key1.hashCode() == key2.hashCode());
        key2 = new MessageCodeKey(EncodeConstants.OCP_VERSION_ID, 4, HealthCheckOutput.class);
        Assert.assertFalse("Wrong equals", key1.equals(key2));
        Assert.assertFalse("Wrong hashcode", key1.hashCode() == key2.hashCode());
        key2 = new MessageCodeKey(EncodeConstants.OCP_VERSION_ID, 6, HealthCheckInput.class);
        Assert.assertFalse("Wrong equals", key1.equals(key2));
        Assert.assertFalse("Wrong hashcode", key1.hashCode() == key2.hashCode());
    }

    /**
     * Test MessageCodeKey equals - additional test
     */
    @Test
    public void testEquals() {
        MessageCodeKey key1 = new MessageCodeKey(EncodeConstants.OCP_VERSION_ID, 4, HealthCheckInput.class);

        Assert.assertTrue("Wrong equal to identical object.", key1.equals(key1));
        Assert.assertFalse("Wrong equal to null.", key1.equals(null));
        Assert.assertFalse("Wrong equal to different class.", key1.equals(new Object()));

        key1 = new MessageCodeKey(EncodeConstants.OCP_VERSION_ID, 4, null);
        MessageCodeKey key2 = new MessageCodeKey(EncodeConstants.OCP_VERSION_ID, 4, HealthCheckInput.class);
        Assert.assertFalse("Wrong equal by clazz.", key1.equals(key2));

        key2 = new MessageCodeKey(EncodeConstants.OCP_VERSION_ID, 4, null);
        Assert.assertTrue("Wrong equal by clazz.", key1.equals(key2));
    }

    /**
     * Test MessageCodeKey toString()
     */
    @Test
    public void testToString() {
        MessageCodeKey key1 = new MessageCodeKey(EncodeConstants.OCP_VERSION_ID, 1, HealthCheckInput.class);

        //Assert.assertEquals("Wrong toString()", "msgVersion: 1 objectClass: org.opendaylight.yang.gen.v1.urn"
        //        + ".opendaylight.ocp.protocol.rev150811.HealthCheckInput msgType: 1", key1.toString());
    }
}