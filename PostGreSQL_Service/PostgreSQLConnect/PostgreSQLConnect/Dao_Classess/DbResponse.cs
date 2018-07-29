using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Data;
using System.Data.SqlClient;
namespace PostgreSQLConnect.Dao_Classess
{
    public class DbResponse
    {
        public int RowAffected { get; set; }
        public DataTable dataTable { get; set; }
    }
}
