package ui;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import db.DBHelper;
import java.sql.*;
import util.Security;
import javafx.geometry.Insets;
public class AuthScene {
    private Stage stage;
    public AuthScene(Stage stage){ this.stage = stage; }
    public void show(){
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        Label lbl = new Label("Online Quiz - Login");
        TextField tfUser = new TextField();
        tfUser.setPromptText("Username");
        PasswordField pf = new PasswordField();
        pf.setPromptText("Password");
        HBox btns = new HBox(10);
        Button btnLogin = new Button("Login");
        Button btnRegister = new Button("Register");
        btns.getChildren().addAll(btnLogin, btnRegister);
        root.getChildren().addAll(lbl, tfUser, pf, btns);
        Scene scene = new Scene(root, 400, 200);
        stage.setScene(scene);
        stage.setTitle("Online Quiz - Login");
        stage.show();
        btnRegister.setOnAction(e -> {
            RegisterScene reg = new RegisterScene(stage);
            reg.show();
        });
        btnLogin.setOnAction(e -> {
            String u = tfUser.getText().trim();
            String p = pf.getText();
            if(u.isEmpty()||p.isEmpty()){ alert("Enter credentials"); return; }
            try(Connection conn = DBHelper.getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT id,password_hash,salt,is_admin FROM users WHERE username=?")) {
                ps.setString(1,u);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    String hash = rs.getString("password_hash");
                    String salt = rs.getString("salt");
                    int id = rs.getInt("id");
                    boolean isAdmin = rs.getInt("is_admin")==1;
                    if(Security.verify(p,salt,hash)){
                        if(isAdmin) {
                            AdminScene admin = new AdminScene(stage);
                            admin.show();
                        } else {
                            UserScene user = new UserScene(stage, id, u);
                            user.show();
                        }
                    } else alert("Invalid credentials");
                } else alert("User not found");
            } catch(Exception ex){ ex.printStackTrace(); alert("Error: "+ex.getMessage()); }
        });
    }
    private void alert(String s){ Alert a = new Alert(Alert.AlertType.INFORMATION, s); a.showAndWait(); }
}
