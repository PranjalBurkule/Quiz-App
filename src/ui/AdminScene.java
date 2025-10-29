package ui;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import db.DBHelper;
import java.sql.*;
import javafx.geometry.Insets;
public class AdminScene {
    private Stage stage;
    private ListView<String> list;
    public AdminScene(Stage stage){ this.stage = stage; }
    public void show(){
        BorderPane root = new BorderPane(); root.setPadding(new Insets(10));
        HBox top = new HBox(10);
        Button btnNew = new Button("New Quiz");
        Button btnEdit = new Button("Edit Questions");
        Button btnDelete = new Button("Delete Quiz");
        top.getChildren().addAll(btnNew, btnEdit, btnDelete);
        list = new ListView<>();
        refresh();
        root.setTop(top); root.setCenter(list);
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene); stage.setTitle("Admin - Manage Quizzes"); stage.show();
        btnNew.setOnAction(e -> {
            TextInputDialog d = new TextInputDialog(); d.setHeaderText("Quiz Title"); d.showAndWait().ifPresent(title -> {
                try(Connection c = DBHelper.getConnection(); PreparedStatement ps = c.prepareStatement("INSERT INTO quizzes(title) VALUES(?)")) {
                    ps.setString(1, title); ps.executeUpdate(); refresh();
                } catch(Exception ex){ ex.printStackTrace(); }
            });
        });
        btnDelete.setOnAction(e -> {
            String sel = list.getSelectionModel().getSelectedItem(); if(sel==null) return;
            int id = Integer.parseInt(sel.split(":")[0]);
            try(Connection c = DBHelper.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM quizzes WHERE id=?")) {
                ps.setInt(1, id); ps.executeUpdate(); refresh();
            } catch(Exception ex){ ex.printStackTrace(); }
        });
        btnEdit.setOnAction(e -> {
            String sel = list.getSelectionModel().getSelectedItem(); if(sel==null) return;
            int id = Integer.parseInt(sel.split(":")[0]);
            QuestionEditorScene qs = new QuestionEditorScene(stage, id);
            qs.show();
        });
    }
    private void refresh(){
        list.getItems().clear();
        try(Connection c = DBHelper.getConnection(); Statement s = c.createStatement(); ResultSet rs = s.executeQuery("SELECT id,title FROM quizzes")) {
            while(rs.next()) list.getItems().add(rs.getInt("id")+": "+rs.getString("title"));
        } catch(Exception ex){ ex.printStackTrace(); }
    }
}
