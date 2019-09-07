import java.sql.*;
import java.util.*;
import java.io.*;
import java.text.*;

public class n01388748_Assign4 {

    public static void main(String[] args) throws SQLException, IOException, ParseException
    {
        String DATABASE_DRIVER = "com.mysql.jdbc.Driver";
        String DATABASE_URL = "jdbc:mysql://localhost:3306/";
        String USERNAME = "root";
        String PASSWORD = "59503177Ab";

        Connection myConn = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
        Statement stmt = myConn.createStatement();
        myConn.setAutoCommit(false);
        Scanner input = new Scanner(System.in);

        File players = new File("players.csv");
        ArrayList<String[]> playersArray = new ArrayList<>();
        File teams = new File("teams.csv");*
        ArrayList<String[]> teamsArray = new ArrayList<>();
        File members = new File("members.csv");
        ArrayList<String[]> membersArray = new ArrayList<>();
        File matches = new File("matches_v2.csv");
        ArrayList<String[]> matchesArray = new ArrayList<>();
        File earnings = new File("earnings.csv");
        ArrayList<String[]> earningsArray = new ArrayList<>();
        File tournaments = new File("tournaments.csv");
        ArrayList<String[]> tournamentsArray = new ArrayList<>();

        int selection;

        // Create database

        createDatabase(myConn);

        // Retrieve and insert data into database

        getData(players, playersArray);
        insPlayers(playersArray, myConn);
        getData(teams, teamsArray);
        insTeams(teamsArray, myConn);
        getData(members, membersArray);
        insMembers(membersArray, myConn);
        getData(matches, matchesArray);
        insMatches(matchesArray, myConn);
        getData(earnings, earningsArray);
        insEarnings(earningsArray, myConn);
        getData(tournaments, tournamentsArray);
        insTournaments(tournamentsArray, myConn);
        myConn.commit();

        // Begin selection statement
        while(true)
        {
            System.out.println("Please enter a digit:\n" +
                                "0: Exit\t\t" +
                                "1: Query 1\t\t" +
                                "2: Query 2\t\t" +
                                "3: Query 3\n" +
                                "4: Query 4\t" +
                                "5: Query 5\t\t" +
                                "6: Query 6\t\t" +
                                "7: Query 7\n");

            selection = input.nextInt();

            switch(selection)
            {
                case 0: stmt.addBatch("DROP TABLES players, Teams, Members, Matches, Earnings, Tournaments");
                        stmt.addBatch("DROP DATABASE PlayerDB_Assign4");
                        stmt.executeBatch();
                        System.exit(0);
                        break;
                case 1:
                    query1(myConn);
                    break;
                case 2:
                    query2(myConn);
                    break;
                case 3:
                    query3(myConn);
                    break;
                case 4:
                    query4(myConn);
                    break;
                case 5:
                    query5(myConn);
                    break;
                case 6:
                    query6(myConn);
                    break;
                case 7:
                    System.out.println("Query 7 will execute.");
                    break;
                default:
                    System.out.println("Enter a valid input.");
                    break;
            }
        }

        //stmt.addBatch("DROP TABLES players, Teams, Members, Matches, Earnings, Tournaments");
        //stmt.addBatch("DROP DATABASE PlayerDB_Assign4")       ;
        //stmt.executeBatch();
    }

    static void getData(File data, ArrayList<String[]> list) throws IOException
    {
        Scanner fileScan = new Scanner(data);

        while (fileScan.hasNext())
        {
            String line = fileScan.nextLine();
            String[] spLine = line.split(",");

            for (int i=0;i<spLine.length;i++)
            {
                spLine[i] = spLine[i].replace("\"", "");
            }

            //for (int i=0;i<spLine.length;i++)
            //  System.out.print(spLine[i] + " ");
            //System.out.println("");

            list.add(spLine);
        }

    }

    static void insPlayers(ArrayList<String[]> list, Connection connection) throws ParseException, SQLException
    {
        // Insert into player table
        int player_id = 0;
        String tag = "", real_name = "", nationality = "";
        java.util.Date birthday = null;
        java.sql.Date bday = null;
        char game_race = ' ';
        String insert;
        PreparedStatement ps;

        for (int i=0;i<list.size();i++)
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            String[] arr = list.get(i);
            player_id = Integer.parseInt(arr[0]);
            tag = arr[1];
            real_name = arr[2];
            nationality = arr[3];
            arr[4] = arr[4].replace("-","/");
            birthday = format.parse(arr[4]);
            bday = new java.sql.Date(birthday.getTime());
            game_race = arr[5].charAt(0);

            insert = "INSERT INTO players(player_id, tag, real_name, nationality, birthday, game_race) VALUES (\""+ player_id + "\", \"" + tag + "\", \"" + real_name + "\", \"" + nationality + "\", \"" + bday + "\", \"" + game_race + "\")";
            //System.out.println(insert);
            ps = connection.prepareStatement(insert);

            ps.execute();

            if (i % 300 == 0)
                System.out.printf("Inserting data... Please wait. (%d of %d players inserted.)\n", i, list.size());
        }
        System.out.println("Data insertion done.\n");
    }

    static void insTeams(ArrayList<String[]> list, Connection connection) throws ParseException, SQLException
    {
        // Insert teams

        int team_id = 0;
        String name = "";
        java.util.Date start_date = null;
        java.sql.Date founded = null;
        java.util.Date end_date = null;
        java.sql.Date disbanded = null;
        String insert;
        PreparedStatement ps;

        for (int i=0;i<list.size();i++)
        {
            String[] arr = list.get(i);
            team_id = Integer.parseInt(arr[0]);
            //System.out.println(team_id);
            name = arr[1];
            //System.out.println(name);
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            arr[2] = arr[2].replace("-","/");
            start_date = format.parse(arr[2]);
            founded = new java.sql.Date(start_date.getTime());
            //System.out.println(founded);


            if (arr.length == 4)
            {
                arr[3] = arr[3].replace("-", "/");
                end_date = format.parse(arr[3]);
                disbanded = new java.sql.Date(end_date.getTime());
                insert = "INSERT INTO Teams(team_id, name, founded, disbanded) VALUES (?,?,?,?)";
                ps = connection.prepareStatement(insert);
                ps.setInt(1, team_id);
                ps.setString(2, name);
                ps.setDate(3, founded);
                ps.setDate(4, disbanded);

                //System.out.println(ps);
            }
            else
            {
                insert = "INSERT INTO Teams(team_id, name, founded, disbanded) VALUES (?,?,?,?)";
                ps = connection.prepareStatement(insert);
                ps.setInt(1, team_id);
                ps.setString(2, name);
                ps.setDate(3, founded);
                ps.setDate(4,null);
                ps.execute();

                //System.out.println(ps);
            }

            if (i % 40 == 0)
                System.out.printf("Inserting data... Please wait. (%d of %d teams inserted.)\n", i, list.size());
        }
        System.out.println("Data insertion done.\n");
    }

    static void insMembers(ArrayList<String[]> list, Connection connection) throws SQLException, ParseException
    {
        // Insert members

        int player = 0, team = 0;
        java.util.Date start;
        java.sql.Date start_date;
        java.util.Date end;
        java.sql.Date end_date;
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        String insert;
        PreparedStatement ps;

        for (int i=0;i<list.size();i++)
        {
            String[] arr = list.get(i);

            player = Integer.parseInt(arr[0]);
            //System.out.print(player + "\t");
            team = Integer.parseInt(arr[1]);
            //System.out.println(team);

            arr[2] = arr[2].replace("-", "/");
            start = format.parse(arr[2]);
            start_date = new java.sql.Date(start.getTime());
            //System.out.print(start_date + "\t");

            if (arr.length == 4)
            {
                arr[3] = arr[3].replace("-", "/");
                end = format.parse(arr[3]);
                end_date = new java.sql.Date(end.getTime());
                insert = "INSERT INTO Members(player, team, start_date, end_date)VALUES(?,?,?,?)";
                ps = connection.prepareStatement(insert);
                ps.setInt(1,player);
                ps.setInt(2, team);
                ps.setDate(3, start_date);
                ps.setDate(4, end_date);
                //System.out.println(ps);
                ps.execute();

                //System.out.println("Populated column");
            }
            else
            {
                //System.out.println("Empty column");
                end_date = new java.sql.Date(0);
                insert = "INSERT INTO Members(player, team, start_date, end_date)VALUES(?,?,?,?)";
                ps = connection.prepareStatement(insert);
                ps.setInt(1, player);
                ps.setInt(2, team);
                ps.setDate(3, start_date);
                ps.setDate(4, null);
                //System.out.println(end_date);
                //System.out.println(ps);
                ps.execute();
                //System.out.println(end_date);
            }

            //System.out.println(insert);

            if (i % 200 == 0)
                System.out.printf("Inserting data... Please wait. (%d of %d team members inserted.)\n", i, list.size());
        }
        System.out.println("Data insertion done.\n");
    }

    static void insMatches(ArrayList<String[]> list, Connection connection) throws ParseException, SQLException
    {
        // Insert Matches

        int match_id = 0, playerA = 0, playerB = 0, scoreA = 0, scoreB = 0, tournament = 0;
        boolean offline_;
        java.util.Date day;
        java.sql.Date date;
        String insert;
        PreparedStatement ps;

        for (int i=0;i<list.size();i++)
        {
            String[] arr = list.get(i);

            match_id = Integer.parseInt(arr[0]);
            tournament = Integer.parseInt(arr[2]);
            playerA = Integer.parseInt(arr[3]);
            playerB = Integer.parseInt(arr[4]);
            scoreA = Integer.parseInt(arr[5]);
            scoreB = Integer.parseInt(arr[6]);
            offline_ = Boolean.parseBoolean(arr[7]);

            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            arr[1] = arr[1].replace("-", "/");
            day = format.parse(arr[1]);
            date = new java.sql.Date(day.getTime());


            insert = "INSERT INTO Matches(match_id, date, tournament, playerA, playerB, scoreA, scoreB, offline_) VALUES ("
                    + match_id + ",'" + date + "'," + tournament + "," + playerA + "," + playerB + "," + scoreA + "," + scoreB + "," +
                    offline_ + ")";
            //System.out.println(insert);
            ps = connection.prepareStatement(insert);
            ps.execute();

            if (i % 5000 == 0)
                System.out.printf("Inserting data... Please wait. (%d of %d matches inserted.)\n", i, list.size());

        }
        System.out.println("Data insertion done.\n");
    }

    static void insEarnings(ArrayList<String[]> list, Connection connection) throws SQLException, ParseException
    {
        // Insert earnings

        int tournament = 0, player = 0, prize_money = 0, pos = 0;
        String insert;
        PreparedStatement ps;

        for (int i =0;i<list.size();i++)
        {
            String[] arr = list.get(i);

            tournament = Integer.parseInt(arr[0]);
            player = Integer.parseInt(arr[1]);
            prize_money = Integer.parseInt(arr[2]);
            pos = Integer.parseInt(arr[3]);

            insert = "INSERT INTO Earnings(tournament, player, prize_money, pos) VALUES (" +
                    tournament + "," + player + "," + prize_money + "," + pos + ")";

            ps = connection.prepareStatement(insert);

            ps.execute();

            if (i % 500 == 0)
                System.out.printf("Inserting data... Please wait. (%d of %d Earnings inserted.)\n", i, list.size());
        }
        System.out.println("Data insertion done.\n");
    }

    static void insTournaments(ArrayList<String[]> list, Connection connection) throws SQLException
    {
        int tournament_id = 0;
        String name = "", region = "";
        boolean major = false;
        String insert;
        PreparedStatement ps;

        for (int i=0;i<list.size();i++)
        {
            String[] arr = list.get(i);

            tournament_id = Integer.parseInt(arr[0]);
            name = arr[1];
            region = arr[2];
            major = Boolean.parseBoolean(arr[3]);

            insert = "INSERT INTO Tournaments(tournament_id, name, region, major) VALUES (" + tournament_id + ",\""
                    + name + "\",'" + region + "'," + major + ")";

            ///System.out.println(insert);
            ps = connection.prepareStatement(insert);
            ps.execute();

            if (i % 500 == 0)
                System.out.printf("Inserting data... Please wait. (%d of %d tournaments inserted.)\n", i, list.size());
        }
        System.out.println("Data insertion done.\n");
    }

    static void query1(Connection connection) throws SQLException
    {
        // Query 1

        String year;
        String month;
        Scanner input = new Scanner(System.in);
        boolean noResults = true;
        boolean conQuery = true;

        System.out.println("Query 1: Given a year and month, provide the real name, tag, and nationality"
                + " of players who were born that month.\n");

        while (conQuery) {
            System.out.println("Please enter a year and a two-digit month separated by a space.");
            year = input.next();
            month = input.next();


            String q = "SELECT p.real_name, p.tag, p.nationality from players p where year(birthday)=\"" + year + "\" and month(birthday)=\"" + month + "\"";
            PreparedStatement ps = connection.prepareStatement(q);
            ResultSet rs = ps.executeQuery();

            System.out.format("%1$-20s%2$-20s%3$-20s\n", "Player Name", "Gamertag", "Nationality");
            System.out.println("");

            while (rs.next()) {
                System.out.format("%1$-20s%2$-20s%3$-20s\n", rs.getString(1), rs.getString(2), rs.getString(3));
                noResults = false;
            }
            System.out.println("");

            if (noResults)
                System.out.println("No results found.");

            System.out.println("Would you like to enter another query? (Y/N)");

            char choice = input.next().charAt(0);

            if ((choice == 'N') || (choice == 'n'))
                conQuery = false;

        }
    }

    static void query2(Connection connection) throws SQLException
    {
        // Query 2

        int teamID, playID;
        String query;
        Scanner input = new Scanner(System.in);
        boolean conQuery = true;
        boolean noResults = true;


        System.out.println("Query 2: Given a player ID and a team ID, add that player as a member of the specified team.\n" +
                "If the player is already a member of another team, the database should also be updated to reflect their departure" +
                " from the old team, with the end date set as above. If the player was already a current member of the given \"new\"" +
                "no changes are necessary.");

        while (conQuery)
        {
            System.out.print("Please enter a player id and team id, separated by a space.");

            playID = input.nextInt();
            teamID = input.nextInt();

            query = "select m.player, m.team, m.start_date from Members m where m.player=\"" + playID + "\" and m.team=\""+ teamID + "\"";

            PreparedStatement ps = connection.prepareStatement(query);

            ResultSet rs = ps.executeQuery();
            boolean onTeam = false;

            while (rs.next())
            {
                System.out.println("Player " + playID + " is already a member of team " + teamID);
                onTeam = true;
                noResults = false;
            }

            if (onTeam == false)
            {
                query = "SELECT m.player, m.team, m.start_date FROM Members m WHERE m.player=? AND YEAR(?)";
                int column = rs.getMetaData().getColumnCount();
                ps = connection.prepareStatement(query);
                ps.setInt(1, playID);
                ps.setDate(2, null);
                java.util.Date start = Calendar.getInstance().getTime();
                java.sql.Date start_date = new java.sql.Date(start.getTime());

                rs = ps.executeQuery();

                /*while (rs.next())
                {
                    for (int i=1;i<=column;i++)
                    {*/
                        System.out.println("updating here");
                        String query2 = "UPDATE Members SET end_date=CURDATE() WHERE player="+playID;
                        ps = connection.prepareStatement(query2);
                        ps.executeUpdate();
                    //}
                //}

                System.out.println("Adding player to new team");
                query = "INSERT INTO Members(player,team,start_date,end_date) VALUES (?,?,?,?)";
                ps = connection.prepareStatement(query);
                ps.setInt(1, playID);
                ps.setInt(2, teamID);
                ps.setDate(3, start_date);
                ps.setDate(4, null);
                ps.executeUpdate();
                System.out.println("Player " + playID + " has been added to team " + teamID + " as of " + start_date + ".");
                noResults = false;
            }

            if (noResults)
                System.out.println("No results found.");

            System.out.println("Do you want to change another player? (Y/N)");

            char choice = input.next().charAt(0);

            if ((choice == 'N') ||(choice == 'n'))
                conQuery = false;
        }
    }

    static void query3(Connection connection) throws SQLException
    {
        // Query 3

        String query;
        String year;
        String nationality;
        Scanner input = new Scanner(System.in);
        boolean conQuery = true;
        boolean noResults = true;

        while (conQuery)
        {
            System.out.println("Query 3: Show the real names and birthdays of each input nationality" +
                    " player who was born in the input year.");
            System.out.println("Enter a two character nationality followed by a four digit year, separated by a space");
            nationality = input.next();
            year = input.next();

            query = "select p.real_name, p.birthday from players p where year(birthday)=\"" + year + "\" && p.nationality=\"" + nationality + "\"";

            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            System.out.format("%1$-20s%2$-20s\n", "Player Name", "Birthday");
            System.out.println("");

            while (rs.next()) {
                System.out.format("%1$-20s%2$-20s\n", rs.getString(1), rs.getString(2));
                noResults = false;
            }
            //System.out.println("here");

            if (noResults)
                System.out.println("No results found.");

            System.out.println("Do you want to run another query? (Y/N)");

            char choice = input.next().charAt(0);

            if ((choice == 'N') || (choice == 'n'))
                conQuery = false;
        }
    }

    static void query4(Connection connection) throws SQLException
    {
        // Query 4

        String query;
        System.out.println("Query 4: List the tag and game race of players that have obtained a Triple Crown at least once.");

        query = "select p.real_name, p.tag, p.game_race from players p, Matches M, Tournaments T, Earnings E " +
                "where (E.pos = 1) && (T.major = TRUE) && " +
                "(T.region = 'KR' || T.region = 'EU' || T.region = 'AM') " +
                "&& T.tournament_id = E.tournament && E.player = p.player_id " +
                "GROUP BY p.player_id having count(T.region)>=3;";
        PreparedStatement ps = connection.prepareStatement(query);

        ResultSet rs = ps.executeQuery();

        System.out.format("%1$-20s%2$-20s\n", "Player Tag", "Game Race");
        System.out.println("");

        while (rs.next())
        {
            System.out.format("%1$-20s%2$-20s\n", rs.getString(1), rs.getString(2));
        }
        System.out.println("");
    }

    static void query5(Connection connection) throws SQLException
    {
        // Query 5

        String query;
        System.out.println("Listing the former members by player tag, real name, and date of most recent " +
                "departure of ROOT Gaming.");

        query = "SELECT P.tag,P.real_name, M.end_date FROM players P, members M, teams T WHERE M.team=T.team_id AND M.player=P.player_id AND T.name='ROOT Gaming' GROUP BY M.player HAVING COUNT(M.player)=1 AND YEAR(m.end_date)<>'0000'";

        PreparedStatement ps = connection.prepareStatement(query);

        ResultSet rs = ps.executeQuery();

        System.out.format("%1$-20s%2$-20s%3$-20s\n", "Gamertag", "Player Name", "Departure Date");
        System.out.println("");

        while (rs.next())
        {
            System.out.format("%1$-20s%2$-20s%3$-20s\n", rs.getString(1), rs.getString(2), rs.getString(3));
        }
        System.out.println("");
    }

    static void query6(Connection connection) throws SQLException
    {
        // Query 6

        System.out.println("From among the Protoss players who have played at least 10 games against Terran opponents,\n" +
                "find those who were able to win more than 65% of their PvT matches. Give the tag, nationality,\n" +
                "PvTs win rate (in percent of these players. Sort according to PvT win rate.");

        String query="SELECT DISTINCT P.tag,P.nationality " +
                "FROM players P, Matches M " +
                "WHERE M.playerA IN (SELECT P.player_id from Players P where P.game_race='P') " +
                "AND M.playerB IN (SELECT P.player_id FROM Players P WHERE P.game_race='T') " +
                "GROUP BY M.match_id " +
                "HAVING COUNT(M.match_id)>=10 LIMIT 10";

        PreparedStatement ps = connection.prepareStatement(query);

        ResultSet rs = ps.executeQuery();

        System.out.format("%1$-20s%2$-20s\n", "Gamertag", "Nationality");
        System.out.println("");

        while (rs.next())
        {
            System.out.format("%1$-20s%2$-20s\n", rs.getString(1), rs.getString(2));
        }
        System.out.println("");
    }

    static void createDatabase(Connection connection) throws SQLException
        {
        Statement stmt = connection.createStatement();

        stmt.addBatch("CREATE DATABASE IF NOT EXISTS PlayerDB_Assign4");
        stmt.addBatch("USE PlayerDB_Assign4");
        stmt.addBatch("CREATE TABLE IF NOT EXISTS players(player_id int primary key, tag varchar(30), real_name varchar(45), nationality varchar(30), birthday date, game_race char(1))");
        stmt.addBatch("CREATE TABLE IF NOT EXISTS Teams(team_id int primary key, name varchar(45), founded date, disbanded date)");
        stmt.addBatch("CREATE TABLE IF NOT EXISTS Members(player int, team int, start_date date, end_date date)");
        stmt.addBatch("CREATE TABLE IF NOT EXISTS Matches(match_id int primary key, date date, tournament int, playerA int, playerB int, scoreA int, scoreB int, offline_ boolean)");
        stmt.addBatch("CREATE TABLE IF NOT EXISTS Earnings(tournament int, player int, prize_money int, pos int)");
        stmt.addBatch("CREATE TABLE IF NOT EXISTS Tournaments(tournament_id int primary key, name varchar(100), region varchar(2), major boolean)");
        stmt.executeBatch();

    }
}