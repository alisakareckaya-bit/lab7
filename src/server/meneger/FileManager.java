package server.meneger;

import common.*;
import server.Server;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static server.Server.getCollectionManager;

/**
 * Класс, отвечающий за работу с файлами (чтение и запись коллекции).
 * Осуществляет хранение данных в формате CSV и обеспечивает их корректную
 * десериализацию в объекты {@link Movie}.
 *
 * @author Алиса
 * @version 1.0
 */
public class FileManager {

    /**
     * Считывает коллекцию фильмов из CSV-файла.
     * Проверяет существование файла и права на чтение. Автоматически обновляет
     * счетчик ID в {@link Generatic} на основе загруженных данных.
     *
     * @param filename путь к файлу для чтения
     */
    public void readCSV(String filename) {
        File file = new File(filename);

        if (!file.exists()) {
            Server.logger.error("Файл не найден или пуст. Будет создан новый файл при сохранении.\n");
            return;
        }
        if (!file.canRead()) {
            Server.logger.error("Ошибка доступа: Файл коллекции найден, но запрещен для чтения (проверьте chmod).\n");
            return;
        }
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            Server.logger.error("Ошибка при чтении файла: " + e.getMessage());
            return;
        }
        if (lines.isEmpty()) {
            Server.logger.error("Файл пуст. Данные не загружены.\n");
            return;
        }

        int startIndex = 0;
        if (lines.get(0).contains("id") && lines.get(0).contains("name")) {
            startIndex = 1;
        }

        int loadedCount = 0;
        for (int i = startIndex; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.trim().isEmpty()) continue;

            Movie movie = parseMovieFromCSV(line);
            if (movie != null) {
                getCollectionManager().add(movie);
                loadedCount++;
            }
        }

        int maxId = getCollectionManager().getMovie().stream()
                .mapToInt(Movie::getId)
                .max()
                .orElse(0);
        Generatic.setId(maxId + 1);

        Server.logger.info("Загружено фильмов: " + loadedCount + "\n");
    }

    /**
     * Сохраняет текущую коллекцию в CSV-файл.
     * Перед записью проверяет наличие прав на запись в файл.
     *
     * @param filename путь к файлу для сохранения
     */
    public void writeCSV(String filename) {
        File file = new File(filename);
        if (file.exists() && !file.canWrite()) {
            Server.logger.error("Ошибка доступа: Невозможно сохранить коллекцию. Файл защищен от записи.\n");
            return;
        }

        Stack<Movie> movies = Server.getCollectionManager().getMovie();
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("id;name;coordinates_x;coordinates_y;creationDate;oscarsCount;goldenPalmCount;length;genre;operator_name;operator_height;operator_passportId;operator_hairColor;operator_nationality");

            for (Movie movie : movies) {
                writer.println(movieToCSV(movie));
            }

        } catch (IOException e) {
            Server.logger.error("Ошибка при сохранении в файл: " + e.getMessage() + "\n");
        }
    }

    /**
     * Преобразует объект фильма в строку формата CSV.
     *
     * @param movie объект для конвертации
     * @return строка с данными фильма, разделенными точкой с запятой
     */
    private String movieToCSV(Movie movie) {
        StringBuilder sb = new StringBuilder();
        sb.append(movie.getId()).append(";");
        sb.append(escapeCSV(movie.getName())).append(";");
        sb.append(movie.getCoordinates().getX()).append(";");
        sb.append(movie.getCoordinates().getY()).append(";");
        sb.append(movie.getCreationDate()).append(";");
        sb.append(movie.getOscarsCount()).append(";");
        sb.append(movie.getGoldenPalmCount()).append(";");
        sb.append(movie.getLength()).append(";");
        sb.append(movie.getGenre()).append(";");

        Person operator = movie.getOperator();
        if (operator != null) {
            sb.append(escapeCSV(operator.getName())).append(";");
            sb.append(operator.getHeight()).append(";");
            sb.append(escapeCSV(operator.getPassportID())).append(";");
            sb.append(operator.getHairColor()).append(";");
            sb.append(operator.getNationality());
        } else {
            sb.append(";;;;");
        }
        return sb.toString();
    }

    /**
     * Парсит строку из CSV-файла и создает объект {@link Movie}.
     *
     * @param line строка данных
     * @return созданный объект фильма или {@code null}, если парсинг не удался
     */
    private Movie parseMovieFromCSV(String line) {
        try {
            String[] parts = line.split(";", -1);
            if (parts.length < 14) {
                Server.logger.error("Ошибка: недостаточно полей в строке.\n");
                return null;
            }

            int idx = 0;
            Integer id = Integer.parseInt(parts[idx++]);
            String name = unescapeCSV(parts[idx++]);
            long x = Long.parseLong(parts[idx++]);
            Float y = Float.parseFloat(parts[idx++]);
            LocalDate creationDate = LocalDate.parse(parts[idx++]);
            long oscarsCount = Long.parseLong(parts[idx++]);
            int goldenPalmCount = Integer.parseInt(parts[idx++]);
            Long length = Long.parseLong(parts[idx++]);
            MovieGenre genre = MovieGenre.valueOf(parts[idx++]);

            // Парсинг оператора
            String operatorName = unescapeCSV(parts[idx++]);
            String operatorHeightStr = parts[idx++];
            String operatorPassportId = unescapeCSV(parts[idx++]);
            String operatorHairColor = parts[idx++];
            String operatorNationality = parts[idx++];

            Person operator = null;
            if (!operatorName.isEmpty() && !operatorHeightStr.isEmpty()) {
                Integer height = Integer.parseInt(operatorHeightStr);
                String passportId = operatorPassportId.isEmpty() ? null : operatorPassportId;

                Color hairColor = operatorHairColor.isEmpty() ? null : Color.valueOf(operatorHairColor.toUpperCase());
                Country nationality = operatorNationality.isEmpty() ? null : Country.valueOf(operatorNationality.toUpperCase());

                operator = new Person(operatorName, height, passportId, hairColor, nationality);
            }

            return new Movie(id, name, new Coordinates(x, y), creationDate, oscarsCount,
                    goldenPalmCount, length, genre, operator);

        } catch (Exception e) {
            Server.logger.error("Ошибка при парсинге строки. Причина: " + e.getMessage() + "\n");
            return null;
        }
    }

    /**
     * Экранирует специальные символы CSV (точка с запятой, кавычки) в строке.
     */
    private String escapeCSV(String str) {
        if (str == null) return "";
        if (str.contains(";") || str.contains("\"") || str.contains("\n")) {
            return "\"" + str.replace("\"", "\"\"") + "\"";
        }
        return str;
    }

    /**
     * Снимает экранирование со строки CSV.
     */
    private String unescapeCSV(String str) {
        if (str == null || str.isEmpty()) return "";
        if (str.startsWith("\"") && str.endsWith("\"")) {
            str = str.substring(1, str.length() - 1);
            str = str.replace("\"\"", "\"");
        }
        return str;
    }
}
