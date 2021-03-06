/*
 * Copyright (c) 2015 Foxconn Corporation and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ocpjava.protocol.impl.deserialization.factories;

import org.opendaylight.ocpjava.protocol.api.extensibility.OCPDeserializer;

import org.opendaylight.yang.gen.v1.urn.opendaylight.ocp.protocol.rev150811.ModifyParamOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ocp.protocol.rev150811.ModifyParamOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ocp.common.types.rev150811.ModifyParamRes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ocp.common.types.rev150811.ModifyParamGlobRes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ocp.common.types.rev150811.ObjId;

import org.opendaylight.yang.gen.v1.urn.opendaylight.ocp.common.types.rev150811.modifyparamoutput.Param;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ocp.common.types.rev150811.modifyparamoutput.ParamBuilder;

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
 * Translates ModifyParamResp message (OCP Protocol v4.1.1)
 * @author Marko Lai <marko.ch.lai@foxconn.com>
 */

/* limitation: objId:0..1, params(name, result):1..X */

/*
<!-- Example: Modify Parameter Response (multiple parameters, failure) -->
<msg xmlns="http://uri.etsi.org/ori/002-2/v4.1.1">
    <header>
        <msgType>RESP</msgType>
        <msgUID>7894</msgUID>
    </header>
    <body>
        <modifyParamResp>
            <globResult>FAIL_PARAMETER_FAIL</globResult>
            <obj objID="exampleObj:1">
                <param name="parameter 1"/>
                <result>SUCCESS</result>
                <param name="parameter 2"/>
                <result>SUCCESS</result>
                <param name="parameter 3"/>
                <result>FAIL_VALUE_TYPE_ERROR</result>
            </obj>
        </modifyParamResp>
    </body>
</msg>

*/

public class ModifyParamOutputFactory implements OCPDeserializer<ModifyParamOutput> {

    private static final Logger LOG = LoggerFactory.getLogger(ModifyParamOutputFactory.class);
    @Override
    public ModifyParamOutput deserialize(List<Object> rawMessage) {
        ModifyParamOutputBuilder builder = new ModifyParamOutputBuilder();
        ParamBuilder parambuilder = new ParamBuilder();
        List<Param> paramlist = new ArrayList();
        Iterator itr = rawMessage.iterator();

        while(itr.hasNext()) {
            Object tok = itr.next();
            LOG.trace("ModifyParamOutputFactory - itr = {}", tok);
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
                    //globResult
                    else if (((XmlElementStart)tok).name().equals("globResult")){
                        String globRel = MessageHelper.getResult(itr);
                        builder.setGlobResult(ModifyParamGlobRes.valueOf(globRel));
                    }
                    //obj
                    else if (((XmlElementStart)tok).name().equals("obj")) {
                        if(((XmlElementStart)tok).attributes().size() >= 1){
                            builder.setObjId(new ObjId(((XmlElementStart)tok).attributes().get(0).value()));
                    	}
                        LOG.debug("ModifyParamOutputFactory - getObjId = {}", builder.getObjId());

                        tok = itr.next();                                                	
                        while(!(tok instanceof XmlElementStart)) {
                    	    tok = itr.next();
                        }                        
                        while(!(((XmlElementStart)tok).name().equals("obj"))){
                        	if(((XmlElementStart)tok).name().equals("param")) {
                                //param name
                                parambuilder.setName(((XmlElementStart)tok).attributes().get(0).value());
                                LOG.debug("ModifyParamOutputFactory - getName = {}", parambuilder.getName());

                                Object nexttok = itr.next();
                                while(!(nexttok instanceof XmlElementStart)){
                                    nexttok = itr.next();
                                }
                                
                                //param result
                                if(((XmlElementStart)nexttok).name().equals("result")) {
                                    String rel = MessageHelper.getResult(itr);
                                    parambuilder.setResult(ModifyParamRes.valueOf(rel));
                            	}
                     	
                                paramlist.add(parambuilder.build());
                                parambuilder = new ParamBuilder();

                                //jump to the next token until the token is param XmlElementStart or obj XmlElementEnd
                                tok = itr.next();
                                while((tok instanceof XmlElementStart)||(tok instanceof XmlElementEnd)||(tok instanceof XmlCharacters)) {
                                    if ((tok instanceof XmlElementStart) &&((XmlElementStart)tok).name().equals("param")) {
                                        break;
                                    }
                                    else if ((tok instanceof XmlElementEnd) &&((XmlElementEnd)tok).name().equals("obj")){
                                        break;
                                    }
                                    tok = itr.next();
                                }

                                if ((tok instanceof XmlElementEnd) && ((XmlElementEnd)tok).name().equals("obj")) {
                                    break;
                                }
                            }
                        }
                        builder.setParam(paramlist);
                        paramlist = new ArrayList();
                        LOG.trace("ModifyParamOutputFactory - builder.getParam() = {}", builder.getParam());
                    }
                } 
            }
            catch( Exception t ) {
                LOG.error("Error {} {}", tok, t.toString());
            }
        }
        LOG.info("ModifyParamOutputFactory - Builder: {}", builder.build());
        return builder.build();
    }
}
