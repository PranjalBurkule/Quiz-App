package ui;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import db.DBHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
public class QuizRunnerScene {
    private Stage stage; private int userId; private int quizId;
    private List<Question> questions = new ArrayList<>(); private int idx=0; private int score=0;
    private Label qLabel = new Label();
    private RadioButton ra = new RadioButton(), rb = new RadioButton(), rc = new RadioButton(), rd = new RadioButton();
    private ToggleGroup tg = new ToggleGroup();
    public QuizRunnerScene(Stage stage, int userId, int quizId){ this.stage=stage; this.userId=userId; this.quizId=quizId; }
    public void show(){
        VBox root = new VBox(10); root.setPadding(new Insets(10));
        qLabel.setWrapText(true);
        ra.setToggleGroup(tg); rb.setToggleGroup(tg); rc.setToggleGroup(tg); rd.setToggleGroup(tg);
        Button btnNext = new Button("Submit & Next");
        root.getChildren().addAll(qLabel, ra, rb, rc, rd, btnNext);
        Scene scene = new Scene(root, 700, 400); stage.setScene(scene); stage.setTitle("Quiz"); stage.show();
        loadQuestions(); if(questions.isEmpty()){ Alert a = new Alert(Alert.AlertType.INFORMATION, "No questions in this quiz"); a.showAndWait(); return; }
        displayCurrent();
        btnNext.setOnAction(e -> {
            RadioButton sel = (RadioButton) tg.getSelectedToggle();
            if(sel==null){ Alert a = new Alert(Alert.AlertType.INFORMATION, "Select an option"); a.showAndWait(); return; }
            String chosen = "";
            if(sel==ra) chosen = "a"; if(sel==rb) chosen = "b"; if(sel==rc) chosen = "c"; if(sel==rd) chosen = "d";
            Question q = questions.get(idx);
            if(q.correct.equalsIgnoreCase(chosen)){ score++; Alert a = new Alert(Alert.AlertType.INFORMATION, "Correct!"); a.showAndWait(); }
            else{ Alert a = new Alert(Alert.AlertType.INFORMATION, "Wrong! Correct: " + q.correct.toUpperCase()); a.showAndWait(); }
            idx++;
            if(idx<questions.size()) displayCurrent(); else finish();
        });
    }
    private void finish(){
        try(Connection c = DBHelper.getConnection(); PreparedStatement ps = c.prepareStatement("INSERT INTO attempts(user_id,quiz_id,score) VALUES(?,?,?)")) {
            ps.setInt(1, userId); ps.setInt(2, quizId); ps.setInt(3, score); ps.executeUpdate();
        } catch(Exception ex){ ex.printStackTrace(); }
        Alert a = new Alert(Alert.AlertType.INFORMATION, "Quiz finished. Score: " + score + "/" + questions.size()); a.showAndWait();
        // go back to user scene
        UserScene us = new UserScene(stage, userId, ""); us.show();
    }
    private void loadQuestions(){
        try(Connection c = DBHelper.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM questions WHERE quiz_id=?")) {
            ps.setInt(1, quizId); ResultSet rs = ps.executeQuery();
            while(rs.next()){
                questions.add(new Question(rs.getInt("id"), rs.getString("question"), rs.getString("option_a"), rs.getString("option_b"), rs.getString("option_c"), rs.getString("option_d"), rs.getString("correct_option")));
            }
        } catch(Exception ex){ ex.printStackTrace(); }
    }
    private void displayCurrent(){
        Question q = questions.get(idx);
        qLabel.setText((idx+1) + ". " + q.text);
        ra.setText("A. " + q.a); rb.setText("B. " + q.b); rc.setText("C. " + q.c); rd.setText("D. " + q.d);
        tg.selectToggle(null);
    }
    static class Question { int id; String text,a,b,c,d,correct; Question(int id,String text,String a,String b,String c,String d,String correct){ this.id=id; this.text=text; this.a=a; this.b=b; this.c=c; this.d=d; this.correct=correct; } }
}
