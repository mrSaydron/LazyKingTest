import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/*
 * В одной далекой стране правил крайне сумасбродный король, который больше всего на свете любил власть.
 * Ему подчинялось множество людей, но вот незадача, у его подчиненных тоже были свои слуги.
 * Король обезумел от мысли, что какой-нибудь дворянин или даже зажиточный холоп может иметь больше слуг, чем он сам.
 * И приказал всем людям на бумаге через запятую написать свое имя и имена своих прямых подчиненных.
 *
 * По результатам опроса король получил огромный список из имен (see "pollResults")
 *
 * У короля разболелась голова. Что с этими данными делать, король не знал и делегировал задачу невезучему слуге.

 * Помогите слуге правильно составить иерархию и подготовить  отчет для короля следующим образом:
 *
 * король
 *     дворянин Кузькин
 *         управляющий Семен Семеныч
 *             крестьянин Федя
 *             доярка Нюра
 *         жена Кузькина
 *         ...
 *     секретарь короля
 *         зажиточный холоп
 *         ...
 *     ...
 *
 * Помните:
 *  1. Те, кто у кого нет подчиненных, просто написали свое имя.
 *  2. Те, кого никто не указал как слугу, подчиняются напрямую королю (ну, пускай бедный король так думает).
 *  3. Итоговый список должен быть отсортирован в алфавитном порядке на каждом уровне иерархии.
 *
   * Ответ присылайте ссылкой на опубликованный приватный Gist.
 * */

public class LazyKing {
    private static List<String> pollResults = List.of(
            "служанка Аня",
            "управляющий Семен Семеныч: крестьянин Федя, доярка Нюра",
            "дворянин Кузькин: управляющий Семен Семеныч, жена Кузькина, экономка Лидия Федоровна",
            "экономка Лидия Федоровна: дворник Гена, служанка Аня",
            "доярка Нюра",
            "кот Василий: человеческая особь Катя",
            "дворник Гена: посыльный Тошка",
            "киллер Гена",
            "зажиточный холоп: крестьянка Таня",
            "секретарь короля: зажиточный холоп, шпион Т",
            "шпион Т: кучер Д",
            "посыльный Тошка: кот Василий",
            "аристократ Клаус",
            "просветленный Антон"
    );

    public static void main(String... args) {
        UnluckyVassal unluckyVassal = new UnluckyVassal();
        unluckyVassal.printReportForKing(pollResults);
    }

    public static class UnluckyVassal {
        public void printReportForKing(List<String> pollResults) {
            Map<String, Person> personByName = new HashMap<>();

            for (String record : pollResults) {
                parseRecord(record, personByName);
            }

            List<Person> kingServants = personByName.values().stream()
                    .filter(person -> person.getMaster() == null)
                    .sorted(Comparator.comparing(Person::getName))
                    .collect(Collectors.toList());

            if (kingServants.isEmpty() && !personByName.isEmpty()) throw new RuntimeException("Список слуг зациклен");

            Person king = new Person("король");
            king.setServants(kingServants);

            recursivePrint(king, 0, new HashSet<>());
        }

        /**
         * Рекурсивно обходит дерево, и печатает его
         **/
        private void recursivePrint(Person person, int level, Set<Person> printedPersons) {
            if (printedPersons.contains(person)) throw new RuntimeException("Список слуг зациклен");
            printedPersons.add(person);

            for (int i = 0; i < level; i++) {
                System.out.print('\t');
            }
            System.out.println(person.getName());
            if (person.getServants() != null) {
                for (Person servant : person.getServants()) {
                    recursivePrint(servant, level + 1, printedPersons);
                }
            }
        }

        //Шаблон для строки
        private static final Pattern RECORD = Pattern.compile("^(([а-яА-Я0-9 ]*): )?(([а-яА-Я0-9 ]*?), )*([а-яА-Я0-9 ]*)$");

        /**
         * Заполню personByName жителями из одной записи
         **/
        private void parseRecord(String record, Map<String, Person> personByName) {
            if (record == null || record.isBlank()) return;
            if (!RECORD.matcher(record).find())
                throw new RuntimeException("Строка \"" + record + "\" не соответствует шаблону");

            String masterName = getMaster(record);
            List<String> servants = getServants(record);

            //Создам слуг если таких еще нет
            List<Person> servantList = servants.stream()
                    .map(servant -> personByName.computeIfAbsent(servant, Person::new))
                    .sorted(Comparator.comparing(Person::getName))
                    .collect(Collectors.toList());

            //Заполню хозяина если он есть
            if (masterName != null) {
                Person master = personByName.computeIfAbsent(masterName, Person::new);
                master.setServants(servantList);
                for (Person servant : servantList) {
                    servant.setMaster(master);
                }
            }
        }

        /**
         * Возвращает хозяина
         */
        private String getMaster(String record) {
            String result = null;
            int index = record.indexOf(":");
            if (index >= 0) {
                result = record.substring(0, index);
            }
            return result;
        }

        /**
         * Возвращает слуг
         **/
        private List<String> getServants(String record) {
            int index = record.indexOf(":");
            if (index >= 0) {
                record = record.substring(index + 2);
            }
            return List.of(record.split(", "));
        }

        /**
         * Житель королевства
         **/
        private static class Person {
            private String name;
            private Person master;
            private List<Person> servants;

            public Person(String name) {
                this.name = name;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<Person> getServants() {
                return servants;
            }

            public void setServants(List<Person> servants) {
                this.servants = servants;
            }

            public Person getMaster() {
                return master;
            }

            public void setMaster(Person master) {
                this.master = master;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Person person = (Person) o;
                return Objects.equals(name, person.name);
            }

            @Override
            public int hashCode() {
                return Objects.hash(name);
            }
        }
    }
}
