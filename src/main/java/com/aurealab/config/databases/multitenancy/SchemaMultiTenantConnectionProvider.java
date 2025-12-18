package com.aurealab.config.databases.multitenancy;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Proveedor de conexiones que cambia el schema según el tenant.
 * IMPORTANTE: La interfaz es genérica MultiTenantConnectionProvider<String>
 */
@Component
public class SchemaMultiTenantConnectionProvider implements MultiTenantConnectionProvider<String> {

    private static final long serialVersionUID = 1L;
    private final DataSource dataSource;

    public SchemaMultiTenantConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        final Connection connection = getAnyConnection();
        try {
            // Opción 1: Usando setSchema (JDBC 4.1+)
            connection.setSchema(tenantIdentifier);

            // Opción 2: Usando SQL directo (más compatible con PostgreSQL)
            // connection.createStatement().execute("SET search_path TO " + tenantIdentifier);

        } catch (SQLException e) {
            throw new SQLException("No se pudo cambiar al schema: " + tenantIdentifier, e);
        }
        return connection;
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        try {
            // Resetear al schema por defecto
            connection.setSchema("public");
            // O con SQL: connection.createStatement().execute("SET search_path TO public");
        } catch (SQLException e) {
            // Log del error pero continuar con el cierre
        }
        connection.close();
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class<?> unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        return null;
    }
}