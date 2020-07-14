import org.junit.*;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;

public class LazyKingTest {

    private PrintStream out;
    private ByteArrayOutputStream outContent;

    @Before
    public void before() {
        out = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void after() {
        System.setOut(out);
    }

    @Test
    public void positiveTest() {
        List<String> pollResults = List.of(
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
        String result =
                "король\r\n" +
                "\tаристократ Клаус\r\n" +
                "\tдворянин Кузькин\r\n" +
                "\t\tжена Кузькина\r\n" +
                "\t\tуправляющий Семен Семеныч\r\n" +
                "\t\t\tдоярка Нюра\r\n" +
                "\t\t\tкрестьянин Федя\r\n" +
                "\t\tэкономка Лидия Федоровна\r\n" +
                "\t\t\tдворник Гена\r\n" +
                "\t\t\t\tпосыльный Тошка\r\n" +
                "\t\t\t\t\tкот Василий\r\n" +
                "\t\t\t\t\t\tчеловеческая особь Катя\r\n" +
                "\t\t\tслужанка Аня\r\n" +
                "\tкиллер Гена\r\n" +
                "\tпросветленный Антон\r\n" +
                "\tсекретарь короля\r\n" +
                "\t\tзажиточный холоп\r\n" +
                "\t\t\tкрестьянка Таня\r\n" +
                "\t\tшпион Т\r\n" +
                "\t\t\tкучер Д\r\n";

        LazyKing.UnluckyVassal unluckyVassal = new LazyKing.UnluckyVassal();
        unluckyVassal.printReportForKing(pollResults);

        String resultString = new String(outContent.toByteArray());
        Assert.assertEquals(result, resultString);
    }

    @Test
    public void emptyListTest() {
        List<String> pollResults = Collections.emptyList();

        String result = "король\r\n";

        LazyKing.UnluckyVassal unluckyVassal = new LazyKing.UnluckyVassal();
        unluckyVassal.printReportForKing(pollResults);

        String resultString = new String(outContent.toByteArray());
        Assert.assertEquals(result, resultString);
    }

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void wrongRecordTest() {
        String record = "служанка: Аня:";
        List<String> pollResults = List.of(record);

        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("Строка \"" + record + "\" не соответствует шаблону");

        LazyKing.UnluckyVassal unluckyVassal = new LazyKing.UnluckyVassal();
        unluckyVassal.printReportForKing(pollResults);
    }

    @Test
    public void cycleOneTest() {
        List<String> pollResults = List.of("слуга1: слуга2", "слуга2: слуга3", "слуга3: слуга2");

        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("Список слуг зациклен");

        LazyKing.UnluckyVassal unluckyVassal = new LazyKing.UnluckyVassal();
        unluckyVassal.printReportForKing(pollResults);
    }

    @Test
    public void cycleTwoTest() {
        List<String> pollResults = List.of("слуга1: слуга2", "слуга2: слуга1");

        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("Список слуг зациклен");

        LazyKing.UnluckyVassal unluckyVassal = new LazyKing.UnluckyVassal();
        unluckyVassal.printReportForKing(pollResults);
    }
}