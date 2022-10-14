package dk.kb.cop3.backend.migrate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PerformMigrationTask {

    public void run(String[] args) {

        String task = "dk.kb.cop3.backend.migrate."+args[0];
        try {
            Method mainMethod = Class.forName(task).getDeclaredMethod("main",String[].class);
            mainMethod.invoke(null, new String[0]);
        } catch (ClassNotFoundException e) {
            System.out.println("Unknown task "+task);
        } catch (NoSuchMethodException e) {
            System.out.println(task+" has no main method");
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }



}
