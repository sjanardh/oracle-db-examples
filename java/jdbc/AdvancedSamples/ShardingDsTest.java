
/**
 * The test demostrate the capabilities of JDBC sharding datasource. When using sharding datasource, 
 * applications are not required to provide the sharding key in order to create a connection from datasource. 
 * The driver automatically derives sharding keys and executes query on the correct shard.
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import oracle.jdbc.pool.OracleDataSource;

public class ShardingDsTest {
  final static String useName = "testuser1";
  final static String password = "testuser1";
  // GSM connection to be used for insert/select to/from to all shards
  final static String gsmURL = "jdbc:oracle:thin:@(DESCRIPTION = (ADDRESS = (HOST = nshe02cn04.us.oracle.com)(PORT = 3493)(PROTOCOL = tcp))(CONNECT_DATA = (SERVICE_NAME = shsvc.shpool.oradbcloud)))";
  // Connection to be used for insert/select to/from to Shard-1
  final static String SHARD1_URL = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=tcp)(HOST=nshe02cn04)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=cdb2pdb1.regress.rdbms.dev.us.oracle.com)))";
  // Connection to be used for insert/select to/from to Shard-2
  final static String SHARD2_URL = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=tcp)(HOST=nshe02cn04)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=cdb3pdb1.regress.rdbms.dev.us.oracle.com)))";;
  // Connection to be used for insert/select to/from to Shard-3
  final static String SHARD3_URL = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=tcp)(HOST=nshe02cn04)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=cdb4pdb1.regress.rdbms.dev.us.oracle.com)))";;

  final static String SELECT_CUST_BY_ID_QUERY = "select id, name from customer where id = ?";
final static String INSERT_CUST = "insert into customer values(?, ?)";
  // Cross Shards Query
  final static String SELECT_ALL_CUST_QUERY = "select * from customer";
  final static int RECORD_COUNT = 10;
  

  public static void main(String args[]) throws Exception {
    OracleDataSource shardingDS = createShardingDataSource();
  
    Connection shardingDsConn = null;
    Connection shard1Conn = null;
    Connection shard2Conn = null;
    Connection shard3Conn = null;
    try {
      // no need to provide the sharding key to get the connection from sharding datasource
      shardingDsConn = shardingDS.getConnection();
      System.out.println("Get DB connection using the Sharding Datasource ...  success!");
      System.out.println("Insert customers into all Shards, using the Sharding datasource \n");

      String name = "Smith";
      for (int i = 1; i < RECORD_COUNT; i++) {
        insertCustomer(shardingDsConn, i, name + i);
      }

      shard1Conn = getDirectShardConnection(SHARD1_URL);
      System.out.println("\nCheck customers in shard-1 using direct Shard connection ");
      System.out.println("---------------------------- :");
      System.out.println("ID\t\t\tNAME" );
      displayAllCustomers(shard1Conn);
      
      shard2Conn = getDirectShardConnection(SHARD2_URL);
      System.out.println("\nCheck customers in shard-2 using direct Shard connection");
      System.out.println("---------------------------- :");
      System.out.println("ID\t\t\tNAME" );
      displayAllCustomers(shard2Conn);
      
      shard3Conn = getDirectShardConnection(SHARD3_URL);
      System.out.println("\nCheck customers in shard-3 using direct Shard connection");
      System.out.println("---------------------------- :");
      System.out.println("ID\t\t\tNAME" );
      displayAllCustomers(shard3Conn);

      System.out.println("---------------------------- :");
      System.out.println("\nDisplay customers in all Shards using the sharding datasource ");
      System.out.println("ID\t\t\tNAME" );
      displayAllCustomers(shardingDsConn);

      } catch (Exception ex) {
        System.out.println("Got exception : "+ex.getMessage());
        ex.printStackTrace();
      } finally {
        if (shardingDsConn != null) {
          cleanup(shardingDsConn);
          shardingDsConn.close();
        }

        if (shard1Conn != null)
          shard1Conn.close();
        if (shard2Conn != null)
          shard2Conn.close();
        if (shard3Conn != null)
          shard3Conn.close();
      }
    }


  private static OracleDataSource createShardingDataSource() throws SQLException {
    OracleDataSource ds = new OracleDataSource();
    Properties prop = new Properties();
    prop.setProperty("user", useName);
    prop.setProperty("password", password);
    // Set the following system property to enable the Sharding Datasource
    System.out.println("\nSet the  oracle.jdbc.useShardingDriverConnection property to true \n");
    prop.setProperty("oracle.jdbc.useShardingDriverConnection", "true");
    ds.setConnectionProperties(prop);
    ds.setURL(gsmURL);
    return ds;
  }

  private static void insertCustomer(Connection conn, int id, String name) throws SQLException {
    try (PreparedStatement ps = conn.prepareStatement(INSERT_CUST)) {
      ps.setInt(1, id);
      ps.setString(2, name);
	    System.out.println("Insert into customer  values( "+ id + " " + name + " )" );
      ps.executeUpdate();
    }
  }

  private static void displayCustomerDataById(Connection conn, int id) throws SQLException {
    try (PreparedStatement ps = conn.prepareStatement(SELECT_CUST_BY_ID_QUERY)) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {

        }
      }

    }
  }

  private static void cleanup(Connection conn) throws SQLException {
    try (Statement st = conn.createStatement()) {
      st.execute("truncate table customer");
    }
  }
  private static Connection getDirectShardConnection(String directShardUrl) throws SQLException {
    return DriverManager.getConnection(directShardUrl, useName, password);
    
  }
  
  private static void displayAllCustomers(Connection conn) throws SQLException {
    try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL_CUST_QUERY)) {
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          System.out.println(""+rs.getInt(1) + "\t\t\t" + rs.getString(2));
        }
      }
    }
  }

}
