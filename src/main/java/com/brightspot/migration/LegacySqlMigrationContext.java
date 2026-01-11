package com.brightspot.migration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.psddev.cms.db.Site;
import com.psddev.cms.db.ToolUi;
import com.psddev.cms.db.ToolUser;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.StringUtils;
import com.psddev.migration.sql.SqlMigrationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Migration context for connecting to and querying legacy SQL databases.
 *
 * <p>This class provides configuration and connection management for migrating data from
 * legacy MySQL databases. It extends {@link SqlMigrationContext} to provide specific
 * functionality for connecting to legacy database systems.</p>
 *
 * <p>The context supports configuration of:</p>
 * <ul>
 *   <li>Database connection parameters (host, port, database name, credentials)</li>
 *   <li>Default site and publisher for migrated content</li>
 *   <li>Custom JDBC connection parameters for fine-tuning database connections</li>
 * </ul>
 *
 * <p>This class automatically initializes default JDBC parameters optimized for migration
 * operations, including connection timeouts, auto-reconnect, and compression settings.</p>
 *
 * @see SqlMigrationContext
 */
public class LegacySqlMigrationContext extends SqlMigrationContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(LegacySqlMigrationContext.class);

    public static final String TAB_ADVANCED = "Advanced";

    private static final String DEFAULT_PORT = "3306";

    /**
     * Constructs a new LegacySqlMigrationContext with default JDBC parameters initialized.
     */
    public LegacySqlMigrationContext() {
        this.initParametersList();
    }

    @Recordable.Required
    private String host;

    @Recordable.Required
    @ToolUi.Placeholder(DEFAULT_PORT)
    private Integer port;

    @Recordable.Required
    private String database;

    @Recordable.Required
    private String username;

    @ToolUi.Secret
    private String password;

    @ToolUi.Heading("Defaults")
    private Site site;

    private ToolUser publisher;

    @ToolUi.Tab(TAB_ADVANCED)
    @ToolUi.DisplayName("JDBC Parameters")
    private List<String> parameters;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return ObjectUtils.firstNonBlank(port, Integer.parseInt(DEFAULT_PORT));
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public ToolUser getPublisher() {
        return publisher;
    }

    public void setPublisher(ToolUser publisher) {
        this.publisher = publisher;
    }

    public List<String> getParameters() {
        if (parameters == null) {
            parameters = new ArrayList<>();
        }
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    /**
     * Establishes a connection to the legacy MySQL database.
     *
     * <p>This method loads the MySQL JDBC driver, constructs the connection URL with
     * configured parameters, and attempts to establish a connection. The connection URL
     * includes any additional JDBC parameters that have been configured.</p>
     *
     * @return a Connection to the legacy database
     * @throws SQLException if the driver cannot be loaded or the connection fails
     */
    @Override
    public Connection connect() throws SQLException {
        // Load MySQL driver (MySQL Connector/J 5.1.42 uses the old driver class)
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQL driver class not found", e);
        }

        String url = String.format("jdbc:mysql://%s:%d/%s", getHost(), getPort(), getDatabase());

        if (!getParameters().isEmpty()) {
            url += "?" + StringUtils.join(parameters, "&");
        }

        LOGGER.debug("Connecting to legacy database at {} with user {}", url, getUsername());

        try {
            Connection connection = DriverManager.getConnection(url, getUsername(), getPassword());
            LOGGER.info("Successfully connected to legacy database");
            return connection;
        } catch (Exception e) {
            throw new SQLException("Failed to connect to legacy database: " + e.getMessage(), e);
        }
    }

    /**
     * Initializes the default JDBC connection parameters optimized for migration operations.
     *
     * <p>These parameters include settings for handling zero dates, connection timeouts,
     * auto-reconnect, compression, and statement caching to improve migration performance
     * and reliability.</p>
     */
    private void initParametersList() {
        parameters = Lists.newArrayList();
        parameters.add("zeroDateTimeBehavior=convertToNull");
        parameters.add("removeAbandoned=false");
        parameters.add("enableQueryTimeouts=false");
        parameters.add("connectTimeout=600");
        parameters.add("socketTimeout=600");
        parameters.add("initialTimeout=30");
        parameters.add("autoReconnect=true");
        parameters.add("useCompression=true");
        parameters.add("cachePrepStmts=true");
        parameters.add("cacheCallableStmts=true");
        parameters.add("cacheServerConfiguration=true");
        parameters.add("useLocalSessionState=true");
        parameters.add("elideSetAutoCommits=true");
        parameters.add("alwaysSendSetIsolation=false");
    }
}
