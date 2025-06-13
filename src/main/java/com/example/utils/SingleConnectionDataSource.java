package com.example.Utils;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class SingleConnectionDataSource implements DataSource {
    private final Connection connection;

    public SingleConnectionDataSource(String url, String user, String password) throws SQLException {
        this.connection = DriverManager.getConnection(url, user, password);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connection;
    }

    @Override public Connection getConnection(String username, String password) { throw new UnsupportedOperationException(); }
    @Override public PrintWriter getLogWriter() { throw new UnsupportedOperationException(); }
    @Override public void setLogWriter(PrintWriter out) { throw new UnsupportedOperationException(); }
    @Override public void setLoginTimeout(int seconds) { throw new UnsupportedOperationException(); }
    @Override public int getLoginTimeout() { throw new UnsupportedOperationException(); }
    @Override public Logger getParentLogger() { throw new UnsupportedOperationException(); }
    @Override public <T> T unwrap(Class<T> iface) { throw new UnsupportedOperationException(); }
    @Override public boolean isWrapperFor(Class<?> iface) { throw new UnsupportedOperationException(); }

}
