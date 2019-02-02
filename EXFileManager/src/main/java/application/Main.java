package application;
	
 
import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.ex.file.manager.MyStatus;
import com.ex.file.manager.Worker;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;


public class Main extends Application {
	static Future<MyStatus> future;
	static ExecutorService executor;
	static ProgressBar pb;
	static Button btn ;
	
	static  DecimalFormat df = new DecimalFormat("##.000");
	static TextField SourceFile,outFilePath;
	static Alert    alert = null ,alertWarn,alertinfo;

	@Override
	public void start(Stage primaryStage) {
		try {
	        primaryStage.setTitle("EX File Manager");
     	   alertWarn = new Alert(AlertType.WARNING);
    	   alertinfo = new Alert(AlertType.INFORMATION);

	        GridPane grid = new GridPane();
	        grid.setAlignment(Pos.CENTER);
	        grid.setHgap(10);
	        grid.setVgap(10);
	        grid.setPadding(new Insets(25, 25, 25, 25));
	       
	        Label SourceLabel = new Label("Full source file path:");
	        grid.add(SourceLabel, 0, 1);

	         SourceFile = new TextField();
	        grid.add(SourceFile, 1, 1);

	        Label outLabel = new Label("Full out file path:");
	        grid.add(outLabel, 0, 2);

	         outFilePath = new TextField();
	        grid.add(outFilePath, 1, 2);

	        btn = new Button("Process");
	        HBox hbBtn = new HBox(10);
	        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
	        hbBtn.getChildren().add(btn);
	        grid.add(hbBtn, 1, 3);
	     
	        pb = new ProgressBar(0);
	        
	        final HBox hb = new HBox();
	        hb.setSpacing(5);
	        hb.setAlignment(Pos.CENTER);
	        hb.getChildren().addAll(  pb );
	        grid.add(hb, 1, 4);
	      
	        btn.setOnAction(new EventHandler<ActionEvent>() {  	 
	            @Override
	            public void handle(ActionEvent e) {
	            	btn.setDisable(Boolean.TRUE);
	            	outFilePath.setDisable(Boolean.TRUE);
	            	SourceFile.setDisable(Boolean.TRUE);
	            	Worker newWorker = new Worker<MyStatus>();
	            	newWorker.setOutFolder(outFilePath.getText());newWorker.setSourceFile(SourceFile.getText());
	            	future = executor.submit(newWorker);
	            	new Thread(task).start();		            	
	            }
	        });

	        Scene scene = new Scene(grid, 400, 400);
	        primaryStage.setScene(scene);
	        primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	Task task = new Task<Void>() {
		@Override
		public void run() {
			 Double count = 0.0;
		 while(!future.isDone()) {
			 try {
				Thread.currentThread().sleep(500);
			} catch (Exception e) {
				//e.printStackTrace();
			}
	        	count = count+0.1;
	        	pb.setProgress(count);
	        	if(count >= 1.0) {count = 0.0;  }
		 }
		 if(future.isDone()) {
			 btn.setDisable(Boolean.FALSE);pb.setProgress(1.0); 
			 outFilePath.setDisable(Boolean.FALSE);
         	 SourceFile.setDisable(Boolean.FALSE);
		 }
		 StringBuilder sb = new StringBuilder();

		 try {
               if(future.get().isError()) {
            	   alert = alertWarn;
          		 alert.setTitle("Message");
            	   sb.append("Something went wrong !! ,Double check path(s) :( \n");
               }else {
            	   alert = alertinfo;
          		   alert.setTitle("Message");
            	   sb.append("it's Done!!\n");
      			 sb.append("\n").append("Time in seconds: " + df.format( future.get().getDiffSeconds() ) + " seconds.");
      			 sb.append("\n").append("Time in minutes: " + df.format( future.get().getDiffMinutes() )  + " minutes.");
    			 sb.append("\n").append("Time in hours: " +   df.format(  future.get().getDiffHours() ) + " hours.");
               }
		} catch (Exception e) {
		}
		 alert.setContentText(sb.toString());
		 Platform.runLater(new Runnable(){
			@Override
			public void run() {
				 alert.showAndWait();
			}
			});
		}
		@Override
		protected Void call() throws Exception {
			return null;
		}
	};
	public static void main(String[] args) {
        executor = Executors.newFixedThreadPool(2);
		launch(args);
	}
}
