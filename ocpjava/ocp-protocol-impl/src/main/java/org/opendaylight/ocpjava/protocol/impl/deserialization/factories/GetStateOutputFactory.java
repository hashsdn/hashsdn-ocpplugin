/*
 * Copyright (c) 2015 Foxconn Corporation and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ocpjava.protocol.impl.deserialization.factories;

import org.opendaylight.ocpjava.protocol.api.extensibility.OCPDeserializer;

import org.opendaylight.yang.gen.v1.urn.opendaylight.ocp.protocol.rev150811.GetStateOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ocp.protocol.rev150811.GetStateOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ocp.common.types.rev150811.StateType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ocp.common.types.rev150811.StateVal;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ocp.common.types.rev150811.GetStateRes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ocp.common.types.rev150811.ObjId;

import org.opendaylight.yang.gen.v1.urn.opendaylight.ocp.common.types.rev150811.getstateoutput.Obj;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ocp.common.types.rev150811.getstateoutput.ObjBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ocp.common.types.rev150811.getstateoutput.obj.State;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ocp.common.types.rev150811.getstateoutput.obj.StateBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ocp.common.types.rev150811.OcpMsgType;

import org.opendaylight.ocpjava.protocol.impl.core.XmlElementStart;
import org.opendaylight.ocpjava.protocol.impl.core.XmlElementEnd;
import org.opendaylight.ocpjava.protocol.impl.core.XmlCharacters;

import org.opendaylight.ocpjava.protocol.impl.deserialization.factories.utils.MessageHelper;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Translates GetStateResp message (OCP Protocol v4.1.1)
 * @author Marko Lai <marko.ch.lai@foxconn.com>
 */

/* limitation: objId:0..X, states(type, value):1..X */

/*
<!-- Example: Object State Reporting Response -->
<msg xmlns="http://uri.etsi.org/ori/002-2/v4.1.1">
    <header>
        <msgType>RESP</msgType>
        <msgUID>34561</msgUID>
    </header>
    <body>
        <getStateResp>
            <result>SUCCESS</result>
            <obj objID="exampleObj:0">
                <state type="FST">OPERATIONAL</state>
                <state type="AST">UNLOCKED</state>
            </obj>
        </getStateResp>
    </body>
</msg>
*/

public class GetStateOutputFactory implements OCPDeserializer<GetStateOutput> {

    private static final Logger LOG = LoggerFactory.getLogger(GetStateOutputFactory.class);
    @Override
    public GetStateOutput deserialize(List<Object> rawMessage) {
        GetStateOutputBuilder builder = new GetStateOutputBuilder();
        List<Obj> objlist = new ArrayList();
        Iterator itr = rawMessage.iterator();
        
        while(itr.hasNext()) {
            Object tok = itr.next();
            LOG.trace("GetStateOutputFactory - itr = {}", tok);
            try {
                if(tok instanceof XmlElementStart) {
                    //msgType
                    if (((XmlElementStart)tok).name().equals("body")){
                        String type = MessageHelper.getMsgType(itr);
                        builder.setMsgType(OcpMsgType.valueOf(type));
                    }
                    //msgUID
                    else if (((XmlElementStart)tok).name().equals("msgUID")){
                        String uidStr = MessageHelper.getMsgUID(itr);
                        int uid = Integer.parseInt(uidStr);
                        builder.setXid((long)uid);
                    }
                    //result
                    else if (((XmlElementStart)tok).name().equals("result")){
                        String rel = MessageHelper.getResult(itr);
                        builder.setResult(GetStateRes.valueOf(rel));
                    }
                    //obj
                    else if (((XmlElementStart)tok).name().equals("obj")) {

                        ObjBuilder objbuilder = new ObjBuilder();       

                        //set Obj ID
                        objbuilder.setId(new ObjId(((XmlElementStart)tok).attributes().get(0).value()));
                        LOG.trace("GetStateOutputFactory - getId = {}", objbuilder.getId());

                        Object objtok = itr.next();
                        while(!(objtok instanceof XmlElementStart)){
                            objtok = itr.next();
                        }
                        
                        StateBuilder statebuilder = new StateBuilder();
                        List<State> statelist = new ArrayList();

                        while(objtok instanceof XmlElementStart){
                            if(((XmlElementStart)objtok).name().equals("state")) {
                                //set state Type
                                String tmp = ((XmlElementStart)objtok).attributes().get(0).value();                                
                                statebuilder.setType(StateType.valueOf(tmp));
                                LOG.trace("GetStateOutputFactory - getType = {}", statebuilder.getType());

                                //set state Value
                                String bufStr = MessageHelper.getMsgUID(itr);
                                statebuilder.setValue(StateVal.valueOf(bufStr));
                                
                                LOG.trace("GetStateOutputFactory - getValue = {}", statebuilder.getValue());
                                statelist.add(statebuilder.build());
                                
                                //skip remain of XmlCharacters
                                objtok = itr.next();
                                while((objtok instanceof XmlCharacters)){
                                    objtok = itr.next(); 
                                }
                                LOG.debug("ModifyStateOutputFactory - found next: {}", objtok);
                            }

                            if (objtok instanceof XmlElementEnd) {
                                LOG.debug("GetStateOutputFactory - found XmlElementEnd: {}", objtok);
                                if(((XmlElementEnd)objtok).name().equals("obj")) {
                                    LOG.debug("GetStateOutputFactory - objtok20 = {}", objtok);
                                }
                            }
                            else{
                                while(!(objtok instanceof XmlElementStart)){
                                    objtok = itr.next();
                                }
                            }
                        }
                        objbuilder.setState(statelist);                    
                        LOG.trace("GetStateOutputFactory - objbuilder.build() = {}", objbuilder.build());
                        objlist.add(objbuilder.build());
                    }
                } 
            }
            catch( Exception t ) {
                LOG.error("Error {} {}", tok, t.toString());
            }
        }
        builder.setObj(objlist);
        LOG.debug("GetStateOutputFactory - Builder: {}", builder.build());
        return builder.build();
    }
}
