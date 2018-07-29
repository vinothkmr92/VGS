using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data;

namespace PostgreSQLConnect.Dao_Classess
{
    public interface DataBase
    {
        
           IDbConnection CreateConnection();
         IDbCommand CreateCommand();
          IDbConnection CreateOpenConnection();
          IDbCommand CreateCommand(string commandText, IDbConnection connection);
           IDbCommand CreateStoredProcCommand(string procName, IDbConnection connection);
          IDataParameter CreateParameter(string parameterName, object parameterValue);
          int ExecuteCommand(IDbCommand command);
         IDataAdapter ExecuteCommandReader(IDbCommand command);
    }
}
