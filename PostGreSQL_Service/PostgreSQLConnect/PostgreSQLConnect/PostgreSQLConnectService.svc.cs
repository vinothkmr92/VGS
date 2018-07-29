using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.ServiceModel.Web;
using System.Text;
using System.Data;
using PostgreSQLConnect.Dao_Classess;

namespace PostgreSQLConnect
{
    // NOTE: You can use the "Rename" command on the "Refactor" menu to change the class name "Service1" in code, svc and config file together.
    // NOTE: In order to launch WCF Test Client for testing this service, please select Service1.svc or Service1.svc.cs at the Solution Explorer and start debugging.
    public class PostgreSQLConnectService : IPostgreSQLConnectService
    {
        public string GetData(int value)
        {
            return string.Format("You entered: {0}", value);
        }

        public string GetHubs(string value)
        {
            string hubs = string.Empty;
            try
            {
                DbRequest request = new DbRequest();
                request.isSelectQuery = true;
                request.Query = "SELECT hub_code FROM OPN_HUBS";
                DbResponse response = Database_Access.Execute_Db_Query(request);
                if(null != response)
                {
                    if(null != response.dataTable)
                    {
                        response.dataTable.Rows.Cast<DataRow>().ToList().ForEach(row =>
                        {
                            hubs = hubs + "," + Convert.ToString(row[0]);
                        });
                    }
                }
            }
            catch(Exception ex)
            {
                hubs = "ERROR" + "," + ex.Message;
                hubs = hubs + "," + ex.StackTrace;
            }
            return hubs;
        }

        public CompositeType GetDataUsingDataContract(CompositeType composite)
        {
            if (composite == null)
            {
                throw new ArgumentNullException("composite");
            }
            if (composite.BoolValue)
            {
                composite.StringValue += "Suffix";
            }
            return composite;
        }
    }
}
