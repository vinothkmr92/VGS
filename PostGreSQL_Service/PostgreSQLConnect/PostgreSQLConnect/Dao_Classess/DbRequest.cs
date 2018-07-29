using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PostgreSQLConnect.Dao_Classess
{
    public class DbRequest
    {
        public string Query { get; set; }
        public bool isSelectQuery { get; set; }
    }
}
