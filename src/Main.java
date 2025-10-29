import javafx.application.Application;
import javafx.stage.Stage;
import ui.AuthScene;
import db.DBHelper;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        DBHelper.init(); // ensure DB and default admin + sample quiz exist
        AuthScene auth = new AuthScene(primaryStage);
        auth.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
