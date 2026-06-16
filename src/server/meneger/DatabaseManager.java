package server.meneger;

import common.*;
import server.Server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.List;
import java.util.Properties;
import java.util.Stack;

public class DatabaseManager {
    private static DatabaseManager instance;
    private static Connection connection;

    private static String DB_HOST;
    private static String DB_PORT;
    private static String DB_NAME;
    private static String DB_USER;
    private static String DB_PASSWORD;
    /**
     * Менеджер для работы с базой данных PostgreSQL.
     * Синглтон, чтобы везде было только одно подключение.
     */
    public DatabaseManager() {
        try {
            loadConfigDB();
            connectDB();
            createTableDB();
        } catch (SQLException e) {
            Server.logger.error("База данных не инициализированна");
        } catch (RuntimeException e) {
            Server.logger.error("Ошибка инициализации БД {}", e.getMessage());
        }
    }
    /**
     * Подгружает настройки (хост, порт, пароль) из файла db.properties.
     * Если файла нет, ставит дефолтные значения, чтобы программа не упала.
     */
    private void loadConfigDB() {
        try {
            InputStream input = new FileInputStream("db.properties");

            Properties properties = new Properties();
            properties.load(input);

            DB_HOST = properties.getProperty("db.host", "localhost");
            DB_PORT = properties.getProperty("db.port", "5432");
            DB_NAME = properties.getProperty("db.name", "movie");
            DB_USER = properties.getProperty("db.user", "postgres");
            DB_PASSWORD = properties.getProperty("db.password", "10082007");

        } catch (IOException e) {
            Server.logger.error("Файл db.properties не найден, использую значения по умолчанию");
            DB_HOST = "localhost";
            DB_PORT = "5432";
            DB_NAME = "movie";
            DB_USER = "postgres";
            DB_PASSWORD = "10082007";
        }
    }
    /**
     * Возвращает единственный экземпляр этого менеджера.
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    /**
     * Подключается к самой базе данных.
     * Если базы на сервере вообще нет, сначала создаёт её с нуля, а потом заходит.
     */
    private void connectDB() throws SQLException {
        String url = String.format("jdbc:postgresql://%s:%s/%s", DB_HOST, DB_PORT, DB_NAME);
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            Server.logger.error("Не подключил библиотеку");
        }


        try {
            connection = DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
            Server.logger.info("Подключение к БД установллено");
        } catch (SQLException e) {
            if (e.getSQLState().equals("3D000")) {
                Server.logger.info("Базы данных не существует");
                createDB();
                Server.logger.info("Создаем базу данных");
                connection = DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
                Server.logger.info("Подключение к БД установллено");

            } else {
                throw e;
            }
        }
    }
    /**
     * Создает таблицы users и Movie, если их еще нет в базе.
     */
    private void createDB() throws SQLException {

        String url = String.format("jdbc:postgresql://%s:%s/", DB_HOST, DB_PORT);

        try {
            Connection conn = DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
            Statement s = conn.createStatement();

            s.execute("CREATE DATABASE " + DB_NAME);
            Server.logger.info("База данных {} создана", DB_NAME);
        } catch (Exception e) {
            Server.logger.error("Произошла ошибка при создании БД");
        }
    };
    private void createTableDB() throws SQLException {
        String createUsersTable = """
                create table if not exists users (
                    id serial primary key,
                    login varchar(50) unique not null,
                    password varchar(300) not null,
                    salt varchar(64) not null,
                    date_created timestamp default current_timestamp
                );
                """;

        Statement s = connection.createStatement();
        s.execute(createUsersTable);

        Server.logger.info("Таблица users создана");


        String createMovieTable = """
                create table if not exists Movie (
                   id serial primary key,
                   name text not null,
                   x bigint,
                   y double precision not null,
                   creationDate timestamp not null default current_timestamp,
                   oscarCount bigint check (oscarCount > 0),
                   goldenPalmCount integer check (goldenPalmCount > 0),
                   length bigint check (length > 0) not null,
                   genre text not null,
                   namePerson text not null,
                   height bigint check (height > 0) not null,
                   passportID varchar(35),
                   hairColor text not null,
                   nationality text not null,
                   author varchar(50) not null
                );
                """;
        s.execute(createMovieTable);
        Server.logger.info("Таблица Movie была создана");

    };
    /**
     * Безопасно добавляет новый фильм в БД.
     * Защищает от SQL-инъекций и возвращает сгенерированный базой ID.
     */
    public synchronized Integer add(Movie movie, String author){
        checkConnection();
        checkAndCreateTables();
        try{
            String addMovie = """
                    insert into Movie (name,x,y, oscarCount, goldenPalmCount, length, genre, namePerson, height, passportID, hairColor, nationality, author)
                    values (?,?,?,?,?,?,?,?,?,?,?,?,?)
                    returning id;
                    """;
            PreparedStatement ps = connection.prepareStatement(addMovie);

            ps.setString(1, movie.getName());
            ps.setLong(2, movie.getCoordinates().getX());
            ps.setFloat(3, movie.getCoordinates().getY());
            ps.setLong(4, movie.getOscarsCount());
            ps.setInt(5, movie.getGoldenPalmCount());
            ps.setLong(6, movie.getLength());
            ps.setString(7,movie.getGenre().toString());
            ps.setString(8, movie.getOperator().getName());
            ps.setInt(9, movie.getOperator().getHeight());
            ps.setString(10,movie.getOperator().getPassportID());
            ps.setString(11,movie.getOperator().getHairColor().toString());
            ps.setString(12,movie.getOperator().getNationality().toString());
            ps.setString(13, author);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Integer id = rs.getInt("id");
                Server.logger.info("Элемент с ID {} успешно добавлен в БД ", id);
                return id;
            }
            return null;

        } catch (SQLException e) {
            Server.logger.error("Ошибка при sql запросе: {}", e.getMessage());
            return null;

        }

    }

    /**
     * Обновляет данные фильма, но только если этот фильм принадлежит тебе.
     */
    public synchronized boolean update(Movie movie, String author){
        checkConnection();
        checkAndCreateTables();
        String updateMovie = """
                update Movie set
                name=?,
                x=?,
                y=?,
                oscarCount=?,
                goldenPalmCount=?,
                length=?,
                genre=?,
                namePerson=?,
                height=?,
                passportID=?,
                hairColor=?,
                nationality=?
                where author=? and id=? ;
                """;
        try{
            PreparedStatement ps = connection.prepareStatement(updateMovie);

            ps.setString(1, movie.getName());
            ps.setLong(2, movie.getCoordinates().getX());
            ps.setFloat(3, movie.getCoordinates().getY());
            ps.setLong(4, movie.getOscarsCount());
            ps.setInt(5, movie.getGoldenPalmCount());
            ps.setLong(6, movie.getLength());
            ps.setString(7,movie.getGenre().toString());
            ps.setString(8, movie.getOperator().getName());
            ps.setInt(9, movie.getOperator().getHeight());
            ps.setString(10,movie.getOperator().getPassportID());
            ps.setString(11,movie.getOperator().getHairColor().toString());
            ps.setString(12,movie.getOperator().getNationality().toString());
            ps.setString(13, author);
            ps.setInt(14, movie.getId());
            int rows = ps.executeUpdate();

            if (rows > 0) {
                Server.logger.info("Фильм {} обновлён ", movie.getId());
                return true;
            } else {
                Server.logger.debug("Фильм {} не найден ", movie.getId());
                return false;
            }

        } catch (SQLException e) {
            Server.logger.error("Ошибка при sql запросе: {}", e.getMessage());
            return false;
        }

    }
    /**
     * Удаляет фильм по ID, если ты его автор.
     * Возвращает true, если строка реально стерлась из базы.
     */
    public synchronized boolean delete(int id, String author){
        checkConnection();
        checkAndCreateTables();
        String deleteMovie = """
                delete from Movie where id=? and author = ?;
                """;
        try {
            PreparedStatement ps = connection.prepareStatement(deleteMovie);
            ps.setInt(1,id);
            ps.setString(2, author);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                Server.logger.info("Фильм {} удалён из БД", id);
                return true;
            } else {
                Server.logger.info("Фильм {} не найден в БД", id);
                return false;
            }
        } catch (SQLException e) {
            Server.logger.error("Ошибка удаления: {}",e.getMessage());
            return false;
        }
    }
    /**
     * Сносит вообще все фильмы из базы, которые создал текущий автор.
     */
    public synchronized void clearDB(String author){
        checkConnection();
        checkAndCreateTables();
        String clearMovie = "delete from Movie where author=?;";
        try{
            PreparedStatement ps = connection.prepareStatement(clearMovie);
            ps.setString(1,author);
            int rows = ps.executeUpdate();
            Server.logger.info("Удалено {} Фильмов", rows);
        } catch (SQLException e) {
            Server.logger.error("Ошибка очищения коллекции");
        }
    }
    /**
     * Достает один конкретный фильм из базы по его ID и автору.
     */
    public Movie getMV(int id, String author){
        checkConnection();
        checkAndCreateTables();
        String getMovie = """
                select * from Movie where id=? and author=?;
                """;
        Movie movie = null;
        try {
            PreparedStatement ps = connection.prepareStatement(getMovie);
            ps.setInt(1,id);
            ps.setString(2, author);
            ResultSet rs = ps.executeQuery();

            if (rs.next()){
                Coordinates coordinates = new Coordinates(
                        rs.getLong("x"),
                        rs.getFloat("y")
                );
                Person operator = new Person(
                        rs.getString("namePerson"),
                        rs.getInt("height"),
                        rs.getString("passportID"),
                        Color.valueOf(rs.getString("hairColor")),
                        Country.valueOf(rs.getString("nationality"))
                );
                movie = new Movie(
                        rs.getInt("id"),
                        rs.getString("name"),
                        coordinates,
                        rs.getTimestamp("creationDate").toLocalDateTime().toLocalDate(),
                        rs.getLong("oscarCount"),
                        rs.getInt("goldenPalmCount"),
                        rs.getLong("length"),
                        MovieGenre.valueOf(rs.getString("genre")),
                        operator,
                        author
                );
                Server.logger.info("Фильмов {} получен", id);
                return movie;
            } else{
            Server.logger.error("Фильмов {} не найден", id);
            return null;
        }

        } catch (SQLException e) {
            Server.logger.error("Ошибка чтения фильма из бд ");
            return null;
        }
    }
    /**
     * Вытаскивает все фильмы из базы при старте сервера и складывает их в Stack.
     */
    public Stack<Movie> getDB(){
        if (connection==null){
            Server.logger.error("База данных не подключена");
            return new Stack<>();
        }

        String getMovie = """
                select * from Movie order by id;
                """;
        Stack<Movie> movieList = new Stack<>();
        try {
            PreparedStatement ps = connection.prepareStatement(getMovie);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Coordinates coordinates = new Coordinates(
                        rs.getLong("x"),
                        rs.getFloat("y")
                );

                Person operator = new Person(
                        rs.getString("namePerson"),
                        rs.getInt("height"),
                        rs.getString("passportID"),
                        Color.valueOf(rs.getString("hairColor")),
                        Country.valueOf(rs.getString("nationality"))
                );

                Movie movie = new Movie(
                        rs.getInt("id"),
                        rs.getString("name"),
                        coordinates,
                        rs.getTimestamp("creationDate").toLocalDateTime().toLocalDate(),
                        rs.getLong("oscarCount"),
                        rs.getInt("goldenPalmCount"),
                        rs.getLong("length"),
                        MovieGenre.valueOf(rs.getString("genre")),
                        operator,
                        rs.getString("author")
                );
                movie.setLogin(rs.getString("author"));
                movieList.add(movie);
            }
            Server.logger.info("Успешно загружено {} фильмов из БД", movieList.size());

        } catch (SQLException e) {
            Server.logger.error("Ошибка чтения лабороторной из бд ");
        }
        return movieList;
    }


    private void checkConnection(){
        if (connection==null){
            Server.logger.error("База данных не подключена");
            repeatConnect();
        }
    }
    /**
     * Пингует базу. Если соединение отвалилось или зависло — переподключается.
     */
    public synchronized boolean repeatConnect() {
        try {
            if (connection == null || connection.isClosed()) {
                connectDB();
                checkAndCreateTables();
                return true;
            }
            if (!connection.isValid(3)) {
                Server.logger.debug("Соединение невалидно, переподключаюсь...");
                connectDB();
                checkAndCreateTables();
                return true;
            }
            return true;
        } catch (SQLException e) {
            Server.logger.error("Не удалось проверить/восстановить соединение: {}", e.getMessage());
            try {
                connectDB();
                checkAndCreateTables();
                return true;
            } catch (SQLException ex) {
                Server.logger.error("БД недоступна после попытки переподключения");
                return false;
            }
        }
    }
    public Connection getConnection() {
        if (!repeatConnect()) {
            Server.logger.debug("БД недоступна Подключение getConnection");
            return null;
        }
        return connection;
    }
    /**
     * Проверяет пароль при логине.
     * Достает соль пользователя, хэширует пароль и сверяет результат с базой.
     */
    public synchronized boolean checkUserPassword(String login, String password) {
        if (!repeatConnect()) {
            Server.logger.error("БД недоступна. Проверка пароля пропущена.");
            return false;
        }

        try {
            String getSalt = "SELECT salt FROM users WHERE login = ?";

            PreparedStatement saltStmt = connection.prepareStatement(getSalt);
            saltStmt.setString(1, login);

            ResultSet saltRs = saltStmt.executeQuery();

            if (!saltRs.next()) {
                return false;
            }

            String salt = saltRs.getString("salt");

            String inputHash = PasswdManager.doSecret(password, salt);

            String checkPassword = "select 1 from users where login = ? and password = ?";
            PreparedStatement pstmt = connection.prepareStatement(checkPassword);

            pstmt.setString(1, login);
            pstmt.setString(2, inputHash);

            ResultSet rs = pstmt.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.startsWith("08")) {
                Server.logger.debug("Соединение с БД потеряно");
                repeatConnect();
            } else {
                Server.logger.error("Ошибка проверки пароля [{}]: {}", sqlState, e.getMessage());
            }
            return false;
        }
    }
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                Server.logger.info("Соединение с БД закрыто");
            }
        } catch (SQLException e) {
            Server.logger.info("Ошибка закрытия БД: {}", e.getMessage());
        }
    }
    /**
     * Проверяет, на месте ли таблицы. Если их кто-то стёр, создаёт заново
     * и заливает туда фильмы, которые сейчас лежат в памяти сервера.
     */
    public synchronized boolean checkAndCreateTables() {

        if (connection == null) {
            Server.logger.error("connection == null");
            return false;
        }

        try {
            try (Statement stmt = connection.createStatement()) {
                stmt.executeQuery("SELECT 1 FROM Movie LIMIT 1");
                stmt.executeQuery("select 1 from users limit 1");
                return true;
            }
        } catch (SQLException e) {

            if ("42P01".equals(e.getSQLState())) {
                try {
                    createTableDB();
                    restoreDataFromCollection();
                    Server.logger.info("Таблицы созданы");
                    return true;
                } catch (SQLException ex) {
                    Server.logger.error("Ошибка создания: {}", ex.getMessage());
                    ex.printStackTrace();
                    return false;
                }
            } else {
                Server.logger.error( e.getSQLState());
                return false;
            }
        }
    }
    /**
     * Экстренно перекидывает все фильмы из коллекции (из ОЗУ) обратно в таблицы БД.
     */
    private synchronized void restoreDataFromCollection() {
        List<Movie> collection = Server.getCollectionManager().getMovie();

        if (collection == null || collection.isEmpty()) {
            Server.logger.info("Коллекция пуста, нечего восстанавливать");
            return;
        }

        Server.logger.info("Восстановление {} объектов из коллекции в БД", collection.size());

        int restoredCount = 0;
        for (Movie movie : collection) {
            if (movie == null) {
                Server.logger.warn("Null объект в коллекции, пропускаю");
                continue;
            }

            String author = movie.getLogin() != null ? movie.getLogin() : "unknown";

            try {
                String insertSql = """
                insert into Movie (name,x,y, oscarCount, goldenPalmCount, length, genre, namePerson, height, passportID, hairColor, nationality, author)
                    values (?,?,?,?,?,?,?,?,?,?,?,?,?)
                """;

                PreparedStatement ps = connection.prepareStatement(insertSql);
                ps.setString(1, movie.getName());
                ps.setLong(2, movie.getCoordinates().getX());
                ps.setFloat(3, movie.getCoordinates().getY());
                ps.setLong(4, movie.getOscarsCount());
                ps.setInt(5, movie.getGoldenPalmCount());
                ps.setLong(6, movie.getLength());
                ps.setString(7,movie.getGenre().toString());
                ps.setString(8, movie.getOperator().getName());
                ps.setInt(9, movie.getOperator().getHeight());
                ps.setString(10,movie.getOperator().getPassportID());
                ps.setString(11,movie.getOperator().getHairColor().toString());
                ps.setString(12,movie.getOperator().getNationality().toString());
                ps.setString(13, author);

                ps.executeUpdate();
                restoredCount++;

            } catch (SQLException e) {
                Server.logger.error("Ошибка восстановления фильма '{}': {}", movie.getName(), e.getMessage());
            }
        }

        Server.logger.info("Восстановлено {} объектов в БД", restoredCount);
    }
}
