package com.vertexcache.core.command.argument;

import com.vertexcache.core.command.argument.ArgumentParser;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArgumentParserTest {

    @Test
    public void testPrimaryCommandNoArgs() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser("PING");
        assertEquals("PING",argumentParser.getPrimaryArgument().getName());
        assertEquals(0,argumentParser.getPrimaryArgument().getArgs().size());
    }

    @Test
    public void testPrimaryCommandOneArg() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser("GET my-key");
        assertEquals("GET",argumentParser.getPrimaryArgument().getName());
        assertEquals(1,argumentParser.getPrimaryArgument().getArgs().size());
    }

    @Test
    public void testPrimaryCommandTwoArgs() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser("SET my-key my-value");
        assertEquals("SET",argumentParser.getPrimaryArgument().getName());
        assertEquals(2,argumentParser.getPrimaryArgument().getArgs().size());
    }

    @Test
    public void testSubCommands() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser("   SET key \"my TEST value\" IDX1 abc IDX2 xyz     ");

        // Initial parse, no sub arg indicated everything is treated as an arg for SET, total of 6
        assertEquals("SET",argumentParser.getPrimaryArgument().getName());
        assertEquals(6,argumentParser.getPrimaryArgument().getArgs().size());

        ArrayList<String> subArguments = new ArrayList<>();
        subArguments.add("IDX1");
        subArguments.add("IDX2");
        argumentParser.setSubArguments(subArguments);

        // Re-parsed occurred after setting sub arguments, then the primary then only has 2 args
        assertEquals("SET",argumentParser.getPrimaryArgument().getName());
        assertEquals(2,argumentParser.getPrimaryArgument().getArgs().size());
        assertEquals("key",argumentParser.getPrimaryArgument().getArgs().get(0));
        assertEquals("my TEST value",argumentParser.getPrimaryArgument().getArgs().get(1));

        if(argumentParser.subArgumentExists("IdX1")) {
            assertEquals("abc",argumentParser.getSubArgumentByName("IDX1").getArgs().getFirst());
            assertEquals(1,argumentParser.getSubArgumentByName("IDX1").getArgs().size());
        }

        if(argumentParser.subArgumentExists("iDX2")) {
            assertEquals("xyz",argumentParser.getSubArgumentByName("IDX2").getArgs().getFirst());
            assertEquals(1,argumentParser.getSubArgumentByName("IDX2").getArgs().size());
        }
    }

}
