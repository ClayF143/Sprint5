package software_masters.planner_networking;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author john.dyar
 *
 */
public class Driver extends Application implements ViewTransitionalModel
{

	private Registry registry;
	private RemoteClient client;
	private LoginController controller;
	private MainController mainController;
	private ComparisonController comparisonController;
	private Stage stage;

	/**
	 * launches the javafx application
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		launch(args);
	}

	/*
	 * Connects to the server and houses the primary stage of the application
	 * 
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception
	{
		try
		{
			registry = LocateRegistry.getRegistry(1060);
			Server stub = (Server) registry.lookup("PlannerServer");
			client = new Client(stub);
            stub.addObserver(client);
            System.out.println("added Observer");
            

		} catch (Exception e)
		{
			System.out.println("If the server is local, there was an error"
					+ "connecting to it.");
			e.printStackTrace();
		}

		stage = primaryStage;
		showLogin();
		primaryStage.setTitle("Centre Business Plans");
		primaryStage.show();
		primaryStage.setOnCloseRequest(e ->
			{
				e.consume();
				try
				{
					closing();
				} catch (RemoteException e1)
				{
					e1.printStackTrace();
				}
			});

	}

	/**
	 * Creates a confirmation box and closes the client's application
	 * 
	 * @throws RemoteException
	 */
	public void closing() throws RemoteException
	{
		boolean confirm = false;
		confirm = ConfirmationBox.show("Are you sure you want to quit? \n" + "Unsaved data will not be saved ",
				"Confirmation", "Yes", "No");
		if (confirm)
		{
			client.getServer().save();
			stage.close();
		}
	}

	public void showLogin() throws IOException
	{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("../../login.fxml"));

		Parent root = (Parent) loader.load();
		Scene scene = new Scene(root);

		controller = loader.getController();
		controller.setClient((Client)client);
		controller.setViewTransitionalModel(this);
		stage.setScene(scene);

	}

	public void showMainView() throws Exception
	{
		FXMLLoader loader2 = new FXMLLoader(getClass().getResource("ClientViewScene.fxml"));

		Parent root2 = (Parent) loader2.load();
		Scene scene2 = new Scene(root2);

		mainController = loader2.getController();
		mainController.setClient((Client)client);
		mainController.setViewTransitionalModel(this);
		mainController.getPlans(mainController.yearDropdown);
		stage.setScene(scene2);
	}
	
	@Override
	public void setClient(Client client) throws IOException {
		this.client=client;
		
	}

	@Override
	public void showComparison() throws Exception
	{
		FXMLLoader loader3=new FXMLLoader(getClass().getResource("ComparisonView.fxml"));
		
		Parent root3 = (Parent) loader3.load();
		Scene scene3 = new Scene(root3);
		
		comparisonController = loader3.getController();
		comparisonController.setClient((Client)client);
		comparisonController.setViewTransitionalModel(this);
		comparisonController.setYear1(mainController.yearDropdown.getValue().getYear());
		comparisonController.planChange1();
		comparisonController.getPlans(comparisonController.yearDropdown);
		stage.setScene(scene3);
	}

}
