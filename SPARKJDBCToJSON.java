
 public class SPARKJDBCToJSON implements Serializable {

        private static final String MYSQL_USERNAME = "chetan";
        private static final String MYSQL_PWD = "";
        private static final String MYSQL_CONNECTION_URL =
                "jdbc:mysql://localhost:3306/test?user=" + MYSQL_USERNAME + "&password=" + MYSQL_PWD;

        private static final JavaSparkContext sc =
                new JavaSparkContext(new SparkConf().setAppName("SPARKJDBCToJSON").setMaster("local"));
        private static final SQLContext sqlContext = new SQLContext(sc);

        public static void main(String[] args) {

            DataFrame custDf = sqlContext.jsonFile("/home/chetan/demodata/transinfo.json");
            custDf.registerTempTable("mytable");
            DataFrame recordlist = sqlContext.sql("select custid, name, lastname, age, country,amount from mytable");
            recordlist.insertIntoJDBC(MYSQL_CONNECTION_URL, "customer1", false);


            //if reuried read the table and convert it to Jason
            Map<String, String> options = new HashMap<String, String>();

            options.put("url", MYSQL_CONNECTION_URL);
            options.put("dbtable", "customer1");

            DataFrame jdbcDF = sqlContext.jdbc(options.get("url"),options.get("dbtable"));

            RDD <String> JSONStr = jdbcDF.toJSON();
            JSONStr.saveAsTextFile("/home/chetan/demodata/JSONToJDBCOut.JSON");
            System.out.println("printing rdd ".concat(String.valueOf(JSONStr.count())));

        }
    }
