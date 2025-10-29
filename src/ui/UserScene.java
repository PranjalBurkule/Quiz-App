package ui;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import db.DBHelper;
import java.sql.*;
import javafx.geometry.Insets;
public class UserScene {
    private Stage stage; private int userId; private String username; private ListView<String> list;
    public UserScene(Stage stage, int userId, String username){ this.stage = stage; this.userId = userId; this.username = username; }
    public void show(){
        BorderPane root = new BorderPane(); root.setPadding(new Insets(10));
        HBox top = new HBox(10); Button btnStart = new Button("Start Quiz"); Button btnHistory = new Button("My Attempts"); top.getChildren().addAll(btnStart, btnHistory);
        list = new ListView<>(); refresh();
        root.setTop(top); root.setCenter(list);
        Scene scene = new Scene(root, 800, 600); stage.setScene(scene); stage.setTitle("User - "+username); stage.show();
        btnStart.setOnAction(e -> {
            String sel = list.getSelectionModel().getSelectedItem(); if(sel==null) return;
            int qid = Integer.parseInt(sel.split(":")[0]);
            QuizRunnerScene qr = new QuizRunnerScene(stage, userId, qid);
            qr.show();
        });
        btnHistory.setOnAction(e -> {
            StringBuilder sb = new StringBuilder();
            try(Connection c = DBHelper.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT q.title,a.score,a.taken_at FROM attempts a JOIN quizzes q ON a.quiz_id=q.id WHERE a.user_id=?")) {
                ps.setInt(1, userId); ResultSet rs = ps.executeQuery();
                while(rs.next()) sb.append(rs.getString("title")).append(" => ").append(rs.getInt("score")).append(" @ ").append(rs.getString("taken_at")).append("\n");
            } catch(Exception ex){ ex.printStackTrace(); }
            Alert a = new Alert(Alert.AlertType.INFORMATION, sb.length()>0?sb.toString():"No attempts yet"); a.showAndWait();
        });
    }
    private void refresh(){
        list.getItems().clear();
        try(Connection c = DBHelper.getConnection(); Statement s = c.createStatement(); ResultSet rs = s.executeQuery("SELECT id,title FROM quizzes")) {
            while(rs.next()) list.getItems().add(rs.getInt("id")+": "+rs.getString("title"));
        } catch(Exception ex){ ex.printStackTrace(); }
    }
}
