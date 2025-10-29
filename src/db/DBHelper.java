package db;
import java.sql.*;
import util.Security;
import java.util.Base64;
public class DBHelper {
    private static final String DB_FILE = "quiz.db";
    private static final String URL = "jdbc:sqlite:" + DB_FILE;
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
    public static void init() {
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.execute("PRAGMA foreign_keys = ON;");
            s.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE NOT NULL, password_hash TEXT NOT NULL, salt TEXT NOT NULL, is_admin INTEGER DEFAULT 0);");
            s.execute("CREATE TABLE IF NOT EXISTS quizzes (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, description TEXT);");
           s.execute("CREATE TABLE IF NOT EXISTS questions (" +
          "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
          "quiz_id INTEGER, " +
          "question TEXT, " +
          "option_a TEXT, option_b TEXT, option_c TEXT, option_d TEXT, " +
          "correct_option TEXT, " +
          "FOREIGN KEY(quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE" +
          ");");

            s.execute("CREATE TABLE IF NOT EXISTS attempts (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, quiz_id INTEGER, score INTEGER, taken_at DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY(user_id) REFERENCES users(id), FOREIGN KEY(quiz_id) REFERENCES quizzes(id));");
            // create default admin if not exists
            PreparedStatement ps = c.prepareStatement("SELECT id FROM users WHERE username=?");
            ps.setString(1, "admin");
            ResultSet rs = ps.executeQuery();
            if(!rs.next()){
                String salt = util.Security.generateSalt();
                String hash = util.Security.hashPassword("admin123", salt);
                PreparedStatement ins = c.prepareStatement("INSERT INTO users(username,password_hash,salt,is_admin) VALUES(?,?,?,1)");
                ins.setString(1, "admin");
                ins.setString(2, hash);
                ins.setString(3, salt);
                ins.executeUpdate();
                ins.close();
            }
            rs.close();
            ps.close();
            // create sample quiz if not exists
            ResultSet r2 = s.executeQuery("SELECT id FROM quizzes WHERE title='Java Basics'");
            if(!r2.next()){
                PreparedStatement pq = c.prepareStatement("INSERT INTO quizzes(title,description) VALUES(?,?)", Statement.RETURN_GENERATED_KEYS);
                pq.setString(1, "Java Basics");
                pq.setString(2, "Sample quiz about Java basics");
                pq.executeUpdate();
                ResultSet gk = pq.getGeneratedKeys();
                int qid = -1;
                if(gk.next()) qid = gk.getInt(1);
                if(qid==-1) {
                    // fetch last id
                    ResultSet r3 = s.executeQuery("SELECT id FROM quizzes ORDER BY id DESC LIMIT 1");
                    if(r3.next()) qid = r3.getInt(1);
                    r3.close();
                }
                if(qid!=-1){
                    PreparedStatement qq = c.prepareStatement("INSERT INTO questions(quiz_id,question,option_a,option_b,option_c,option_d,correct_option) VALUES(?,?,?,?,?,?,?)");
                    qq.setInt(1, qid);
                    qq.setString(2, "Who invented Java Programming?");
                    qq.setString(3, "James Gosling");
                    qq.setString(4, "Dennis Ritchie");
                    qq.setString(5, "Bjarne Stroustrup");
                    qq.setString(6, "Guido van Rossum");
                    qq.setString(7, "a");
                    qq.executeUpdate();
                    qq.close();
                }
                pq.close();
            }
            r2.close();
        } catch(SQLException ex){ ex.printStackTrace(); }
    }
}
