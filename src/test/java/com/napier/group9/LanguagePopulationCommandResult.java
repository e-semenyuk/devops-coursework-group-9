package com.napier.group9;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

record LanguagePopulationCommandResult(int status, String output, String errors) {
    static LanguagePopulationCommandResult run(
            LanguagePopulationRepository repository, String... args) {

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ByteArrayOutputStream errors = new ByteArrayOutputStream();

        int status = LanguagePopulationCommand.run(
                args,
                repository,
                new PrintStream(output, true, StandardCharsets.UTF_8),
                new PrintStream(errors, true, StandardCharsets.UTF_8));

        return new LanguagePopulationCommandResult(
                status, text(output), text(errors));
    }

    private static String text(ByteArrayOutputStream stream) {
        return stream.toString(StandardCharsets.UTF_8)
                .replace("\r\n", "\n");
    }
}