using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data;
using Npgsql;

namespace PostgreSQLConnect.Dao_Classess
{
    public class PostgreDatabase : DataBase
    {
        public string connectionString { get; set; }
        public  IDbConnection CreateConnection()
        {
            return new NpgsqlConnection(connectionString);
        }
        public  IDbCommand CreateCommand()
        {
            return new NpgsqlCommand();
        }
        public  IDbConnection CreateOpenConnection()
        {
            NpgsqlConnection connection = (NpgsqlConnection)CreateConnection();
            connection.Open();
            return connection;
        }
        public  IDbCommand CreateCommand(string commandText, IDbConnection connection)
        {
            NpgsqlCommand command = (NpgsqlCommand)CreateCommand();
            command.CommandText = commandText;
            command.Connection = (NpgsqlConnection)connection;
            command.CommandType = CommandType.Text;
            return command;
        }
        public  IDbCommand CreateStoredProcCommand(string procName, IDbConnection connection)
        {
            NpgsqlCommand command = (NpgsqlCommand)CreateCommand();
            command.CommandText = procName;
            command.Connection = (NpgsqlConnection)connection;
            command.CommandType = CommandType.StoredProcedure;
            return command;
        }
        public  IDataParameter CreateParameter(string parameterName, object parameterValue)
        {
            return new NpgsqlParameter(parameterName, parameterValue);
        }
        public  int ExecuteCommand(IDbCommand command)
        {
            return command.ExecuteNonQuery();
        }
        public  IDataAdapter ExecuteCommandReader(IDbCommand command)
        {
            return new NpgsqlDataAdapter(command.CommandText, (NpgsqlConnection)command.Connection);
        }
        
    }
}
