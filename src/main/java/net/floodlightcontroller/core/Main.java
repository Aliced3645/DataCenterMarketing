/**
 *    Copyright 2013, Big Switch Networks, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License"); you may
 *    not use this file except in compliance with the License. You may obtain
 *    a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *    License for the specific language governing permissions and limitations
 *    under the License.
 **/

package net.floodlightcontroller.core;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import net.floodlightcontroller.core.internal.CmdLineSettings;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.FloodlightModuleLoader;
import net.floodlightcontroller.core.module.IFloodlightModuleContext;
import net.floodlightcontroller.datacentermarketing.FlowUI;
import net.floodlightcontroller.datacentermarketing.UIThread;
import net.floodlightcontroller.restserver.IRestApiService;
import javax.swing.*;
import net.floodlightcontroller.datacentermarketing.*;

/**
 * Host for the Floodlight main method
 * @author alexreimers
 */

public class Main {

    /**
     * Main method to load configuration and modules
     * @param args
     * @throws FloodlightModuleException 
     */
	
	//a reference to UI. All modules could visit and call services.
	static FlowUI flowUI;
	private static IFloodlightProviderService controller;
	
	public static IFloodlightProviderService getController(){
		return Main.controller;
	}
	
	public static FlowUI getFlowUI(){
		return Main.flowUI;
	}
	
	
    public static void main(String[] args) throws FloodlightModuleException {
        
    	new UIThread();
        while(UIThread.getFlowUI() == null){
        	System.out.println("Waiting....");
        }
        Main.flowUI = UIThread.getFlowUI();
    	
    	// Setup logger
        System.setProperty("org.restlet.engine.loggerFacadeClass", 
                "org.restlet.ext.slf4j.Slf4jLoggerFacade");
        
        CmdLineSettings settings = new CmdLineSettings();
        CmdLineParser parser = new CmdLineParser(settings);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            parser.printUsage(System.out);
            System.exit(1);
        }
        
        // Load modules
        FloodlightModuleLoader fml = new FloodlightModuleLoader();
        IFloodlightModuleContext moduleContext = fml.loadModulesFromConfig(settings.getModuleFile());
        // Run REST server
        IRestApiService restApi = moduleContext.getServiceImpl(IRestApiService.class);
        restApi.run();
        // Run the main floodlight module
        IFloodlightProviderService controller =
                moduleContext.getServiceImpl(IFloodlightProviderService.class);
       
      	
        
        // This call blocks, it has to be the last line in the main
        controller.run();
    }
}
