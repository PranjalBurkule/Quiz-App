package ui;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import db.DBHelper;
import util.Security;
import java.sql.*;
import javafx.geometry.Insets;
public class RegisterScene {
    private Stage stage;
    public RegisterScene(Stage stage){ this.stage = stage; }
    public void show(){
        VBox root = new VBox(10); root.setPadding(new Insets(15));
        Label lbl = new Label("Register - Create Account");
        TextField tfUser = new TextField(); tfUser.setPromptText("Username");
        PasswordField pf = new PasswordField(); pf.setPromptText("Password");
        Button btnCreate = new Button("Create Account");
        Button btnBack = new Button("Back to Login");
        HBox hb = new HBox(10, btnCreate, btnBack);
        root.getChildren().addAll(lbl, tfUser, pf, hb);
        Scene scene = new Scene(root, 400, 200);
        stage.setScene(scene); stage.setTitle("Register");
        stage.show();
        btnBack.setOnAction(e -> { AuthScene auth = new AuthScene(stage); auth.show(); });
        btnCreate.setOnAction(e -> {
            String u = tfUser.getText().trim(); String p = pf.getText();
            if(u.isEmpty()||p.isEmpty()){ alert("Fill both"); return; }
            try(Connection conn = DBHelper.getConnection(); PreparedStatement ps = conn.prepareStatement("INSERT INTO users(username,password_hash,salt) VALUES(?,?,?)")) {
                String salt = Security.generateSalt();
                String hash = Security.hashPassword(p, salt);
                ps.setString(1, u); ps.setString(2, hash); ps.setString(3, salt);
                ps.executeUpdate();
                alert("Account created. Login.");
                AuthScene auth = new AuthScene(stage); auth.show();
            } catch(SQLException ex){ alert("Error: "+ex.getMessage()); }
        });
    }
    private void alert(String s){ Alert a = new Alert(Alert.AlertType.INFORMATION, s); a.showAndWait(); }
}
