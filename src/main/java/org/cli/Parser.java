package org.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Разбирает строку ввода в команды и аргументы.
 */
public class Parser {
    private final Environment environment;
//    private final Executor executor;

    public Parser(Environment environment, Executor executor) {
        this.environment = environment;
//        this.executor = executor;
    }

    /**
     * Преобразует строку в список команд.
     */
    public List<Command> parse(String input) {
        List<Command> commands = new ArrayList<>();
        if (input.isBlank()) {
            return commands;
        }

        input = resolveVariables(input);
        String[] commandStrings = input.split("\\|");
        for (String commandString : commandStrings) {
            List<String> tokens = tokenize(commandString.trim());
            if (tokens.isEmpty()) {
                continue;
            }
            String commandName = tokens.get(0);
            List<String> args = tokens.subList(1, tokens.size());
            commands.add(new Command(commandName, args));
        }
        return commands;
    }

    /**
     * Разбивает строку на аргументы, учитывая кавычки.
     */
    private List<String> tokenize(String input) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = Pattern.compile("\"([^\"]*)\"|'([^']*)'|(\\S+)").matcher(input);
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                tokens.add(matcher.group(1));
            } else if (matcher.group(2) != null) {
                tokens.add(matcher.group(2));
            } else {
                tokens.add(matcher.group(3));
            }
        }
        return tokens;
    }

    /**
     * Заменяет переменные окружения вида $VAR на их значения.
     */
    private String resolveVariables(String arg) {
        Pattern pattern = Pattern.compile("\\$(\\w+)");
        Matcher matcher = pattern.matcher(arg);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String varName = matcher.group(1);
            String value = environment.getVariable(varName);
            matcher.appendReplacement(result, value);
        }
        matcher.appendTail(result);

        return result.toString();
    }
}
