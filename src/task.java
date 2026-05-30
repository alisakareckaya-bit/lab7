import java.util.List;


public class task {
    public static void main() {
        record User(String name, int age, String city) {}

        List<User> users = List.of(
                new User("Анна", 22, "Москва"),
                new User("Иван", 17, "Казань"),
                new User("Олег", 31, "Москва"),
                new User("Мария", 19, "СПб"),
                new User("Петр", 45, "Казань"),
                new User("Елена", 28, "СПб"),
                new User("Артем", 16, "Москва")
        );
       users.stream().map(User::city).distinct().forEach(System.out::println);



    }


}
