import java.util.Scanner;

public class Main {

    private static String input() {
        var in = new Scanner(System.in);
        var input = in.nextLine();
        in.close();

        return input;
    }

    public static void main(String[] args) {
        var example = input();
        var solver = new Solver(example);
        System.out.println(solver.result);
        //test();
    }

    private static void test() {
        var tests = new String[] {
                "10 * 10",//1
                "10 / 3",//2
                "4 - 10",//3
                "2 + 2",//4
                "III + X",//5
                "VIII - IV",//6
                "IX * VIII",//7
                "X / IV",//8
        };
        var tests_expect = new String[] {
                "100",//1
                "3",//2
                "-6",//3
                "4",//4
                "XIII",//5
                "IV",//6
                "LXXII",//7
                "II",//8
        };


        for (var i = 0; i < tests.length; i++) {
            var example = tests[i];
            var solver = new Solver(example);
            if (!tests_expect[i].equals(solver.result))
                System.out.println("WRONG TEST:\nexample: " + example + "\nresult: " + solver.result + " expected: " + tests_expect[i]);
        }

        System.out.println("complete");
    }
}

class Solver {

    public String result;

    Solver(String example) {
        var exampleSplit = example.split(" ");
        var checker = new InputCheck(exampleSplit);

        if (checker.numsNature.equals("ARABIC")) {
            var x = checker.num1.equals("10") ? 10 : checker.num1.codePointAt(0) - 48;
            var y = checker.num2.equals("10") ? 10 : checker.num2.codePointAt(0) - 48;
            this.result = "" + solve(checker.operator, x, y);
        }
        else {
            var x = toArabicNum(checker.num1);
            var y = toArabicNum(checker.num2);

            if (checker.operator.equals("-") && x <= y)
                throw new Error("ERROR: Roman nums less than 1 do not exist");

            this.result = toRomeNum(solve(checker.operator, x, y));
        }
    }

    private int solve(String operator, int x, int y) {
        return switch (operator) {
            case "+" -> x + y;
            case "-" -> x - y;
            case "*" -> x * y;
            case "/" -> x / y;
            default -> 0;
        };
    }

    private String toRomeNum(int arabicNum) {
        var tens = arabicNum / 10;
        var other = arabicNum % 10;
        var romeNum = "";

        romeNum = romeTransform(romeNum, tens, 'X', 'L', 'C');
        romeNum = romeTransform(romeNum, other, 'I', 'V', 'X');

        return romeNum;
    }

    private String romeTransform(String romeNum, int rankNum, char rankLow, char rankMiddle, char rankBig) {
        if (rankNum == 10)
            romeNum += rankBig;
        else if (rankNum == 9)
            romeNum += rankLow + "" + rankBig;
        else if (rankNum >= 5) {
            romeNum += rankMiddle;
            for (var i = 0; i < rankNum % 5; i++)
                romeNum += rankLow;
        }
        else if (rankNum == 4)
            romeNum += rankLow + "" + rankMiddle;
        else {
            for (var i = 0; i < rankNum; i++)
                romeNum += rankLow;
        }

        return romeNum;
    }

    private int toArabicNum(String romeNum) {
        char lastNum = '.';
        char currentNum;
        int arabicNum = 0;

        for (var i = 0; i < romeNum.length(); i++) {
            currentNum = romeNum.charAt(i);
            arabicNum += getRomeNumValues(currentNum);

            if (lastNum != '.' && getRomeNumValues(lastNum) < getRomeNumValues(currentNum))
                arabicNum -= getRomeNumValues(lastNum) * 2;

            lastNum = currentNum;
        }

        return arabicNum;
    }

    public int getRomeNumValues(char name) {
        return switch (name) {
            case 'I' -> 1;
            case 'V' -> 5;
            case 'X' -> 10;
            case 'L' -> 50;
            case 'C' -> 100;
            default -> 0;
        };
    }
}

class InputCheck {

    public String num1;
    public String operator;
    public String num2;
    public String numsNature;

    InputCheck(String[] input) {

        checkCorrectSpaces(input);

        this.num1 = input[0];
        this.operator = input[1];
        this.num2 = input[2];

        checkOperator();
        checkNums();
    }

    private void checkCorrectSpaces(String[] inputSplit) {
        if (inputSplit.length != 3)
            throw new Error("ERROR: Wrong input");
    }

    private void checkOperator() {
        if (!(this.operator.equals("+") || this.operator.equals("-") || this.operator.equals("*") || this.operator.equals("/")))
            throw new Error("ERROR: Wrong operator");
    }

    private void checkNums() {
        var num1Nature = checkNum(this.num1);
        var num2Nature = checkNum(this.num2);
        if (!num1Nature.equals(num2Nature))
            throw new Error("ERROR: Different types of nums");

        this.numsNature = num1Nature.equals("ROME")
                ? "ROME"
                : "ARABIC";
    }

    private String checkNum(String num) {
        if (num.length() == 0)
            throw new Error("ERROR: Wrong num");

        String nature;

        var numCharCode = num.codePointAt(0);
        if (numCharCode >= 49 && numCharCode <= 57) {
            if (num.length() > 1 && !num.equals("10"))
                throw new Error("ERROR: Wrong num (only 1-10)");
            nature = "ARABIC";
        }
        else {
            if (!"I II III IV V VI VII VIII IX X".contains(num))
                throw new Error("Wrong num (only I-X)");
            nature = "ROME";
        }

        return nature;
    }
}
