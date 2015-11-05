/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.mobicents.media.server.mgcp.controller;

import org.mobicents.media.server.concurrent.ConcurrentMap;

import java.util.Iterator;

/**
 * MGCP call.
 * 
 * @author yulian oifa
 */
public class MgcpCall {

    private final CallManager callManager;
    
    protected final int id;
    protected final ConcurrentMap<MgcpConnection> connections;
        
    protected MgcpCall(CallManager callManager, int id) {
        this.callManager = callManager;
        this.id=id;
        this.connections=new ConcurrentMap<MgcpConnection>();
    }

    public MgcpConnection getMgcpConnection(Integer id) {
    	return connections.get(id);    	    
    }
    
    public int getId() {
		return id;
	}
    
    /**
     * Excludes connection activity from this call.
     * 
     * @param activity the activity to be excluded.
     */
    public void exclude(MgcpConnection activity) {
    	connections.remove(activity.id);

        //if no more connections terminate the entire call
        if (connections.isEmpty()) {
           callManager.terminate(this);
        }
    }
    
    /**
     * The amount of connections related to this call.
     * 
     * @return the amount.
     */
    public int size() {
        return connections.size();
    }
    
    public void deleteConnections() {     	
    	Iterator<Integer> keyIterator = connections.keysIterator();
        while (keyIterator.hasNext()) {
            MgcpConnection currConnection = connections.remove(keyIterator.next());
            currConnection.getEndpoint().deleteConnection(currConnection.getId());
        }
    	
    	if (connections.isEmpty()) {
            callManager.terminate(this);
        }
    }
    
    @Override
    public String toString() {
        return "call-" + id;
    }
}
