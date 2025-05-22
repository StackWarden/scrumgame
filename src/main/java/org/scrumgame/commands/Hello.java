package org.scrumgame.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class Hello {
    @ShellMethod(key = "hello", value = "Say hello to someone")
    public String sayHello(@ShellOption(help = "Name to greet") String name) {
        return "Hello, " + name + "!";
    }
}