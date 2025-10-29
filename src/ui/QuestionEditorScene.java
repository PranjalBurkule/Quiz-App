package ui;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import db.DBHelper;
import java.sql.*;
import javafx.geometry.Insets;
public class QuestionEditorScene {
    private Stage stage; private int quizId; private ListView<String> list;
    public QuestionEditorScene(Stage stage, int quizId){ this.stage = stage; this.quizId = quizId; }
    public void show(){
        BorderPane root = new BorderPane(); root.setPadding(new Insets(10));
        list = new ListView<>();
        refresh();
        HBox bot = new HBox(10);
        Button btnAdd = new Button("Add"); Button btnDel = new Button("Delete");
        bot.getChildren().addAll(btnAdd, btnDel);
        root.setCenter(list); root.setBottom(bot);
        Scene scene = new Scene(root, 700, 500);
        stage.setScene(scene); stage.setTitle("Edit Questions - Quiz "+quizId); stage.show();
        btnAdd.setOnAction(e -> {
            Dialog<Void> dlg = new Dialog<>(); dlg.setTitle("New Question");
            GridPane gp = new GridPane(); gp.setVgap(6); gp.setHgap(6); gp.setPadding(new Insets(10));
            TextField q = new TextField(); TextField a = new TextField(); TextField b = new TextField(); TextField c = new TextField(); TextField d = new TextField(); TextField correct = new TextField();
            gp.addRow(0, new Label("Question:"), q);
            gp.addRow(1, new Label("Option A:"), a);
            gp.addRow(2, new Label("Option B:"), b);
            gp.addRow(3, new Label("Option C:"), c);
            gp.addRow(4, new Label("Option D:"), d);
            gp.addRow(5, new Label("Correct (a/b/c/d):"), correct);
            dlg.getDialogPane().setContent(gp);
            ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            dlg.getDialogPane().getButtonTypes().addAll(ok, ButtonType.CANCEL);
            dlg.setResultConverter(btn -> { if(btn==ok) { try(Connection conn = DBHelper.getConnection(); PreparedStatement ps = conn.prepareStatement("INSERT INTO questions(quiz_id,question,option_a,option_b,option_c,option_d,correct_option) VALUES(?,?,?,?,?,?,?)")){ ps.setInt(1, quizId); ps.setString(2, q.getText()); ps.setString(3, a.getText()); ps.setString(4, b.getText()); ps.setString(5, c.getText()); ps.setString(6, d.getText()); ps.setString(7, correct.getText()); ps.executeUpdate(); } catch(Exception ex){ ex.printStackTrace(); } refresh(); } return null; });
            dlg.showAndWait();
        });
        btnDel.setOnAction(e -> {
            String sel = list.getSelectionModel().getSelectedItem(); if(sel==null) return;
            int id = Integer.parseInt(sel.split(":")[0]);
            try(Connection c = DBHelper.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM questions WHERE id=?")) {
                ps.setInt(1, id); ps.executeUpdate(); refresh();
            } catch(Exception ex){ ex.printStackTrace(); }
        });
    }
    private void refresh(){
        list.getItems().clear();
        try(Connection c = DBHelper.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT id,question FROM questions WHERE quiz_id=?")) {
            ps.setInt(1, quizId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) list.getItems().add(rs.getInt("id")+": "+rs.getString("question"));
        } catch(Exception ex){ ex.printStackTrace(); }
    }
}
