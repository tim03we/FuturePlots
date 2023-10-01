package com.intellectualcrafters.plot.object;

import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.commands.DebugExec;
import com.intellectualcrafters.plot.commands.MainCommand;

import javax.script.ScriptException;

public abstract class Expression<T> {
    public abstract T evaluate(T arg);

    public static <U> Expression<U> constant(final U value) {
        return new Expression<U>() {
            @Override
            public U evaluate(U arg) {
                return value;
            }
        };
    }

    public static Expression<Double> linearDouble(final Double value) {
        return new Expression<Double>() {
            @Override
            public Double evaluate(Double arg) {
                return (arg * value);
            }
        };
    }

    public static Expression<Double> doubleExpression(final String expression) {
        try {
            return constant(Double.parseDouble(expression));
        } catch (NumberFormatException ignore) {}
        if (expression.endsWith("*{arg}")) {
            try {
                return linearDouble(Double.parseDouble(expression.substring(0, expression.length() - 6)));
            } catch (NumberFormatException ignore) {}
        }
        return new Expression<Double>() {
            @Override
            public Double evaluate(Double arg) {
                DebugExec exec = (DebugExec) MainCommand.getInstance().getCommand(DebugExec.class);
                try {
                    return (Double) exec.getEngine().eval(expression.replace("{arg}", "" + arg));
                } catch (ScriptException e) {
                    PS.debug("Invalid Expression: " + expression);
                    e.printStackTrace();
                }
                return 0d;
            }
        };
    }
}
