using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Data.SqlClient;
using System.Data;
using System.Configuration;
using Npgsql;
namespace PostgreSQLConnect.Dao_Classess
{
    public static class Database_Access
    {
         
        
        
        
       
        public static DbResponse Execute_Db_Query(DbRequest request)
        {
            
            


            DbResponse response = new DbResponse();

            NpgsqlConnection connection = null;
            NpgsqlCommand com = null;
            NpgsqlDataAdapter reader = null;
            try
            {
                string host = ConfigurationManager.AppSettings["host"];
                string username = ConfigurationManager.AppSettings["username"];
                string dbname = ConfigurationManager.AppSettings["datbase"];
                string password = ConfigurationManager.AppSettings["password"];
                string connectionString = "";
                if (string.IsNullOrEmpty(password))
                {
                    connectionString = "Host="+host+";Username="+username+";Database="+dbname;
                }
                else
                {
                    connectionString = "Host=" + host + ";Username=" + username + ";Password="+password+";Database=" + dbname;
                }
                connection = new NpgsqlConnection();
                connection.ConnectionString = connectionString;
                connection.Open();
                com = connection.CreateCommand();
                com.CommandText = request.Query;
                com.CommandType = CommandType.Text;
                if (request.isSelectQuery)
                {

                    reader = new NpgsqlDataAdapter(com);

                    DataSet s = new DataSet();
                    DataTable dt = new DataTable();
                    reader.Fill(s);
                    dt = s.Tables[0];
                    response.dataTable = dt;
                }
                else
                {
                    response.RowAffected = com.ExecuteNonQuery();
                   // Logger.Logger.LogSQL(request.Query);
                }
                connection.Close();
                com.Dispose();
            }
            catch (Exception ex)
            {

                //MessageBox.Show(ex.Message, "Exception Occured", MessageBoxButtons.OK, MessageBoxIcon.Stop);
                throw ex;
            }
            finally
            {
               
            }
            return response;
        }
    }
}
