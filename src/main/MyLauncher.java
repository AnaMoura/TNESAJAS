package main;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

import agents.AuctioneerAgent;
import agents.BuyerAgent;
import agents.TaxiAgent;
import agents.UberAgent;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;

import sajas.core.Agent;
import sajas.core.Runtime;
import sajas.sim.repast3.Repast3Launcher;
import sajas.wrapper.AgentController;
import sajas.wrapper.ContainerController;
import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.sim.analysis.Plot;
import uchicago.src.sim.analysis.Sequence;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Object2DDisplay;

public class MyLauncher extends Repast3Launcher {

	DisplaySurface dsurf;
	private static final boolean BATCH_MODE = false;
	ArrayList<OpenSequenceGraph> graphs;
	private ContainerController mainContainer;
	@Override
	public String[] getInitParam() {
		return new String[0];
	}

	@Override
	public String getName() {
		return "SAJaS Project";
	}

	@Override
	protected void launchJADE() {

		Runtime rt = Runtime.instance();
		Profile p1 = new ProfileImpl();
		mainContainer = rt.createMainContainer(p1);

		launchAgents();
	}

	private void launchAgents() {
		Object[] auctioneerArgs = new Object[3];
		auctioneerArgs[0] = 0.1f; // MinPrice
		auctioneerArgs[1] = 65.0f; //MaxMarketPrice
		auctioneerArgs[2] = 100; //Rounds
		AgentController auctioneer;
		try {
			auctioneer = mainContainer.createNewAgent("auctioneer", "agents.AuctioneerAgent", auctioneerArgs);
			auctioneer.start();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Object[] buyerArgs = new Object[3];


		for(int i = 0; i < 200; i++)
		{
			
			buyerArgs[0] = (float)ThreadLocalRandom.current().nextDouble(50, 110);//limitPrice
			buyerArgs[1] = (float)ThreadLocalRandom.current().nextDouble(0.0, 0.5);//Agressiv
			buyerArgs[2] = 5.0f;//increaseRate
			AgentController buyer;
			try {
				String name = "buyer" + i;
				buyer = mainContainer.createNewAgent(name, "agents.BuyerAgent", buyerArgs);
				buyer.start();
			} catch (StaleProxyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Object[] uberArgs = new Object[3];
		uberArgs[0] = (float)ThreadLocalRandom.current().nextDouble(10, 30); //limitPrice
		uberArgs[1] = (float)ThreadLocalRandom.current().nextDouble(-0.5, 0.2);//Agressiv
		uberArgs[2] = 5.0f;//increaseRate

		for(int i = 0; i < 50; i++)
		{
			AgentController seller;
			try {
				String name = "uberAgent" + i;
				seller = mainContainer.createNewAgent(name, "agents.UberAgent", uberArgs);
				seller.start();
			} catch (StaleProxyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		Object[] taxiArgs = new Object[3];
		taxiArgs[0] = 30.0f;
		
		for(int i = 0; i < 50; i++)
		{
			AgentController taxi;
			try {

				String name = "taxiAgent" + i;
				taxi = mainContainer.createNewAgent(name, "agents.TaxiAgent", taxiArgs);
				taxi.start();


			} catch (StaleProxyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void setup() {
		super.setup();

		// property descriptors
		// ...
	}




	public static void buildGraphics(){
		LinkedList<Float> uberTransactions;
		LinkedList<Float> taxiTransactions;

		Plot aPlot = new Plot("TaxiMoney");
		aPlot.setAxisTitles("TaxiMoney", "Time");

		aPlot.setConnected(false);
		taxiTransactions = AuctioneerAgent.taxiTransactions;
		for (int i = 0; i < taxiTransactions.size(); i++) {
			if(taxiTransactions.get(i)!=0)
				aPlot.plotPoint(i, taxiTransactions.get(i), 0);
		}
		aPlot.display();
		aPlot.updateGraph();


		Plot aPlot2 = new Plot("UberMoney");
		aPlot2.setAxisTitles("UberMoney", "Time");

		aPlot2.setConnected(false);
		uberTransactions = AuctioneerAgent.uberTransactions;
		for (int i = 0; i < uberTransactions.size(); i++) {
			if(uberTransactions.get(i)!=0)
				aPlot2.plotPoint(i, uberTransactions.get(i), 0);
		}
		aPlot2.display();
		aPlot2.updateGraph();
		aPlot.fillPlot();
		aPlot2.fillPlot();

	}


	/**
	 * Launching Repast3
	 * @param args
	 */
	public static void main(String[] args) {
		boolean BATCH_MODE = true;
		SimInit init = new SimInit();
		init.setNumRuns(1);   // works only in batch mode
		init.loadModel(new MyLauncher(), null, BATCH_MODE);
	}

}
