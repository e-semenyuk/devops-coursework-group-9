package com.napier.group9;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

record PopulationTotalCommandResult(int status, String output, String errors) {
    static PopulationTotalCommandResult run(
            PopulationTotalRepository repository, String... args) {

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ByteArrayOutputStream errors = new ByteArrayOutputStream();

        int status = PopulationTotalCommand.run(
                args,
                repository,
                new PrintStream(output, true, StandardCharsets.UTF_8),
                new PrintStream(errors, true, StandardCharsets.UTF_8));

        return new PopulationTotalCommandResult(
                status, text(output), text(errors));
    }

    private static String text(ByteArrayOutputStream stream) {
        return stream.toString(StandardCharsets.UTF_8)
                .replace("\r\n", "\n");
    }
}