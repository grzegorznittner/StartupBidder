package com.startupbidder.cli;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.lang.StringUtils;

import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;
import com.startupbidder.dao.MockDataBuilder;
import com.startupbidder.vo.ListPropertiesVO;
import com.startupbidder.vo.ListingPropertyVO;
import com.startupbidder.web.FrontController;

public class DataImport {
	private static final Logger log = Logger.getLogger(DataImport.class.getName());
	
	private Options options;
	
	private String loglevel = "FINE";
	private int fromId = 0;
	private int toId = 50;
	private int chunksize = 500;
	private String cursor = null;
	
	private RemoteApiInstaller remoteAPIInstaller;
	private String appDomain = "localhost";
	private int appPort = 7777;
	private String username = "grzegorz.nittner@gmail.com", password;
	
	private void setupLogger() {
    	Handler fh = new ConsoleHandler();
	    Logger.getLogger("").addHandler(fh);
	    Logger.getLogger("").setLevel(Level.parse(loglevel));
	}
	
	private void registerRemoteAPI() throws IOException {
		// registering datastore objects in Objectify
		new FrontController();
		
		log.info("Connecting to applicaiton running on " + appDomain + ":" + appPort + " using user " + username);
		RemoteApiOptions remoteAPIOptions = new RemoteApiOptions()
	    	.server(appDomain, appPort)
	    	.credentials(username, password);

		remoteAPIInstaller = new RemoteApiInstaller();
		remoteAPIInstaller.logMethodCalls();
		remoteAPIInstaller.install(remoteAPIOptions);
	}
	
	private void deregisterRemoteAPI() {
		try {
			remoteAPIInstaller.uninstall();
		} catch (Exception e) {
			//log.log(Level.SEVERE, "Deregistering Remote API error", e);
		}
	}
	
	@SuppressWarnings("static-access")
	private Options createCliOptions() {
		Options options = new Options();
		options.addOption(new Option( "help", "displays help page" ));
		options.addOption(OptionBuilder.withArgName("loglevel")
                .hasArg()
                .withDescription("log level, handled values: INFO, ERROR")
                .create("loglevel"));
		options.addOption(OptionBuilder.withArgName("user")
                .hasArg()
                .withDescription("AppEngine user name, default: grzegorz.nittner@gmail.com")
                .create("user"));
		options.addOption(OptionBuilder.withArgName("pass")
                .hasArg()
                .withDescription("AppEngine's user password. If not provided you'll be asked for it. " +
                		"IT'S RECOMMENDED to enter it from command line.")
                .create("pass"));
		options.addOption(OptionBuilder.withArgName("domain")
                .hasArg()
                .withDescription("application domain, eg. startupbidder.appspot.com, defaults to localhost")
                .create("domain"));
		options.addOption(OptionBuilder.withArgName("port")
                .hasArg()
                .withDescription("application port, for GAE it will be 443, defaults to 7777 (dev environment)")
                .create("port"));
		options.addOption(new Option( "mock", "imports mock data from startupbidder sources"));
		options.addOption(new Option( "startuply", "imports data from startuply"));
		options.addOption(new Option( "angellist", "imports data from angel list"));
		options.addOption(new Option( "updatestats", "updates listing statistics"));
		options.addOption(OptionBuilder.withArgName("fromid")
                .hasArg()
                .withDescription("first company id which will be imported")
                .create("fromid"));
		options.addOption(OptionBuilder.withArgName("toid")
                .hasArg()
                .withDescription("last company id which will be imported")
                .create("toid"));
		options.addOption(OptionBuilder.withArgName("chunksize")
                .hasArg()
                .withDescription("max number of listings updated (used by updatestats)")
                .create("chunksize"));
		options.addOption(OptionBuilder.withArgName("cursor")
                .hasArg()
                .withDescription("cursor to update next chunk of listings (returned by updatestats)")
                .create("cursor"));
		
		return options;
	}
	
	public void parseCli(String args[]) {
	    CommandLineParser parser = new GnuParser();
	    try {
	    	options = createCliOptions();
	        CommandLine line = parser.parse(options, args);
	        if (line.hasOption("help")) {
	        	HelpFormatter formatter = new HelpFormatter();
	        	formatter.printHelp("import.sh", options);
	        	return;
	        }
	        if (line.hasOption("loglevel")) {
	        	loglevel = line.getOptionValue("loglevel");
	        }
	        //setupLogger();
	        
	        if (line.hasOption("user")) {
	        	username = line.getOptionValue("user");
	        }
	        if (line.hasOption("pass")) {
	        	password = line.getOptionValue("pass");
	        } else {
	        	System.out.println();
	        	password = new String(System.console().readPassword("Password for account '" + username + "': "));
	        }
	        if (line.hasOption("domain")) {
	        	appDomain = line.getOptionValue("domain");
	        }
	        if (line.hasOption("port")) {
	        	appPort = Integer.valueOf(line.getOptionValue("port"));
	        }
	        registerRemoteAPI();
	        
	        if (line.hasOption("mock")) {
	        	new MockDataBuilder().createMockDatastore(true, true);
	        }
	        if (line.hasOption("startuply")) {
	        	if (line.hasOption("fromid")) {
	        		fromId = Integer.valueOf(line.getOptionValue("fromid"));
	        	}
	        	if (line.hasOption("toid")) {
	        		toId = Integer.valueOf(line.getOptionValue("toid"));
	        	}
	        	log.info("Import Startuply data from " + fromId + " to " + toId);
	        	new MockDataBuilder().importStartuplyData(fromId, toId);
	        }
	        if (line.hasOption("angellist")) {
	        	if (line.hasOption("fromid")) {
	        		fromId = Integer.valueOf(line.getOptionValue("fromid"));
	        	}
	        	if (line.hasOption("toid")) {
	        		toId = Integer.valueOf(line.getOptionValue("toid"));
	        	}
	        	log.info("Import AngelList data from " + fromId + " to " + toId);
	        	new MockDataBuilder().importAngelListData("" + fromId, "" + toId);
	        }
	        if (line.hasOption("updatestats")) {
	        	if (line.hasOption("chunksize")) {
	        		chunksize = Integer.valueOf(line.getOptionValue("chunksize"));
	        	}
	        	if (line.hasOption("cursor")) {
	        		cursor = line.getOptionValue("cursor");
	        	}
	        	ListPropertiesVO props = new MockDataBuilder().updateListingStatistics(chunksize, cursor);
	        	if (StringUtils.isEmpty(props.getNextCursor())) {
	        		log.info("Updated all listings");
	        	} else {
	        		log.info("Cursor for next chunk: " + props.getNextCursor());
	        	}
	        }
	        deregisterRemoteAPI();
	    } catch(Exception exp) {
	        System.err.println( "Parsing failed.  Reason: " + exp.getMessage());
	        exp.printStackTrace(System.err);
	        return;
	    }
	}
	
	public void run(String args[]) {
		parseCli(args);
	}

	public static void main(String args[]) {
		DataImport dataImport = new DataImport();
		dataImport.run(args);
		System.exit(0);
	}
}
