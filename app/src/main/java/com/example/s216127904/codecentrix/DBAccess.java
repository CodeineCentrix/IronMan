package com.example.s216127904.codecentrix;

import android.os.StrictMode;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;

import ViewModel.CommentsModel;
import ViewModel.PenaltyModel;
import ViewModel.RacerModel;
import ViewModel.TicketModel;
import ViewModel.User;

public class DBAccess {

    private ResultSet outerResultSet;

    private static class DBHelper {
        private static String conString;
        private static Connection connection;
        private static PreparedStatement st;
        private static ResultSet innerResultSet;
        private static String forName;
        static String us;
        static String password;

        private static void Connect() {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            try {

                conString = "jdbc:jtds:sqlserver://204.246.56.130:1433/ironman";
                forName = "net.sourceforge.jtds.jdbc.Driver";
                us = "ironman";
                password = "Zb599q0H2_f_";


                Class.forName(forName).newInstance();
                connection = DriverManager.getConnection(conString, us, password);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private static void Close() {
            try {
                innerResultSet=null;
                st = null;
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        static String SetParaToPass(String sql, Object[] parameters) {
            String usp = "{ CALL " + sql + "( ?";
            for (int i = 1; i < parameters.length; i++) {
                usp += ",?";
            }
            usp += " )}";
            return usp;
        }

        static boolean NonQuery(String sql, Object[] parameters) {
            Connect();
            int i = 0;
            try {
                st = connection.prepareStatement(SetParaToPass(sql, parameters));
                int count = 1;
                for (Object para : parameters) {
                    BindParameter(count, para, st);
                    count++;
                }
                i = st.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return i == 0;
        }

        static ResultSet Select(String sql) {
            try {
                Connect();
                st = connection.prepareStatement(sql);
                innerResultSet = st.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return innerResultSet;
        }

        static ResultSet SelectPara(String sql, Object[] parameters) {
            try {
                Connect();
                st = connection.prepareStatement(SetParaToPass(sql, parameters));
                int i = 1;
                for (Object para : parameters) {
                    BindParameter(i, para, st);
                    i++;
                }
                innerResultSet = st.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return innerResultSet;
        }

        private static void BindParameter(int parameterIndex, Object parameterObj, PreparedStatement preparedStatement) throws SQLException {
            //Hate this, I really do But the is no other way
            if (parameterObj == null) {
                preparedStatement.setNull(parameterIndex, java.sql.Types.OTHER);
            } else {
                if (parameterObj instanceof String) {
                    preparedStatement.setString(parameterIndex, (String) parameterObj);
                } else if (parameterObj instanceof Integer) {
                    preparedStatement.setInt(parameterIndex, ((Integer) parameterObj).intValue());
                } else if (parameterObj instanceof Long) {
                    preparedStatement.setLong(parameterIndex, ((Long) parameterObj).longValue());
                } else if (parameterObj instanceof Float) {
                    preparedStatement.setFloat(parameterIndex, ((Float) parameterObj).floatValue());
                } else if (parameterObj instanceof Boolean) {
                    preparedStatement.setBoolean(parameterIndex, ((Boolean) parameterObj).booleanValue());
                } else if (parameterObj instanceof Byte) {
                    preparedStatement.setInt(parameterIndex, ((Byte) parameterObj).intValue());
                } else if (parameterObj instanceof BigDecimal) {
                    preparedStatement.setBigDecimal(parameterIndex, (BigDecimal) parameterObj);
                } else if (parameterObj instanceof Short) {
                    preparedStatement.setShort(parameterIndex, ((Short) parameterObj).shortValue());
                } else if (parameterObj instanceof Double) {
                    preparedStatement.setDouble(parameterIndex, ((Double) parameterObj).doubleValue());
                } else if (parameterObj instanceof byte[]) {
                    preparedStatement.setBytes(parameterIndex, (byte[]) parameterObj);
                } else if (parameterObj instanceof java.sql.Date) {
                    preparedStatement.setDate(parameterIndex, (java.sql.Date) parameterObj);
                } else if (parameterObj instanceof Time) {
                    preparedStatement.setTime(parameterIndex, (Time) parameterObj);
                } else if (parameterObj instanceof Timestamp) {
                    preparedStatement.setTimestamp(parameterIndex, (Timestamp) parameterObj);
                } else if (parameterObj instanceof BigInteger) {
                    preparedStatement.setString(parameterIndex, parameterObj.toString());
                } else {
                    preparedStatement.setObject(parameterIndex, parameterObj);
                }
            }
        }
    }

    public TicketModel GetTickets() {
        //this object acts like a sqlparameter like in c#
        TicketModel ticketModel = new TicketModel();

        //pass the stored procedure name and the paras if you have parameter
        outerResultSet = DBHelper.Select("uspGetTickets");
        //the is alot that could go wrong when trying to connect to the database that is why the is a try catch
        try {
            //the outerResulSet is the table returned from the execution of the stored procedure
            outerResultSet.next();//Moves from row of Heading to row record
            ticketModel.TictetType = outerResultSet.getInt("TicketType");//you need to specify not only the colunm name but also the data type to be return
            ticketModel.TicketColour = outerResultSet.getString("TicketColour");
            ticketModel.ticketName = outerResultSet.getString("ticketName");
            ticketModel.TicketTip = outerResultSet.getString("TicketTip");
            DBHelper.Close();//to closes the connection
        } catch (SQLException e) {
            e.printStackTrace();// I propable should inform the user
        }
        return ticketModel;
    }
    public ArrayList<CommentsModel> GetComments() {
        ArrayList<CommentsModel> comments = new ArrayList<>();
        outerResultSet = DBHelper.Select("uspGetComments");
        try {

           while (outerResultSet.next()){
            CommentsModel comment = new CommentsModel();
            comment.CommentID = outerResultSet.getInt("CommentID");
            comment.CommentDescription = outerResultSet.getString("CommentDescription");
            comment.TicketID = outerResultSet.getInt("TicketID");
            comments.add(comment);
           }
            DBHelper.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comments;
    }
    public ArrayList<RacerModel> GetAllRacerers() {
        ArrayList<RacerModel> racerModels = new ArrayList<>();
        outerResultSet = DBHelper.Select("uspGetAllRacerers");
        try {

            while (outerResultSet.next()){
                RacerModel Racer = new RacerModel();
                Racer.RacerID = outerResultSet.getInt("RacerID");
                Racer.RacerName = outerResultSet.getString("RacerName");
                Racer.RacerSurname = outerResultSet.getString("RacerSurname");
                racerModels.add(Racer);
            }
            DBHelper.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return racerModels;
    }

    public User FindAndLoginUser(String username, String password) throws SQLException {
        boolean validUser = false;
        User ref = new User();
        Object[] params = new  Object[] {
              username,
            password
        };
        outerResultSet = DBHelper.SelectPara("uspLogin", params);
        outerResultSet.next();
            ref.RefID = outerResultSet.getInt("RefID");
            ref.RefFullName = outerResultSet.getString("RefFullName");
            ref.RefEmail = outerResultSet.getString("RefEmail");
            ref.RefPassword = outerResultSet.getString("RefPassword");

        DBHelper.Close();
        return  ref;
    }

    public Boolean AddPenalty(PenaltyModel penalty){
        Object[] paras = {
                penalty.TicketID,
                penalty.TentID,
                penalty.RacerID,
                penalty.RefID,
                penalty.CommentID,
                penalty.PenaltyTime,
                penalty.PenaltyPicturePath,
                penalty.longitude,
                penalty.latitude

        };
        boolean i = DBHelper.NonQuery("uspAddPenalty",paras);
        DBHelper.Close();
        return i;
    }
}
