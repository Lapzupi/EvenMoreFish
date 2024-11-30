package com.oheers.fish.errors;


import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.matchers.Matchers;
import com.sun.source.tree.MethodInvocationTree;

import static com.google.errorprone.BugPattern.SeverityLevel.SUGGESTION;
import static com.google.errorprone.BugPattern.SeverityLevel.WARNING;

/**
 * Custom Error Prone check to detect patterns involving Math.round and conversion to String.
 */
@BugPattern(
        name = "AvoidImpreciseRounding",
        summary = "Avoid using Math.round followed by division for formatting; use DecimalFormat or String.format instead.",
        link = "https://github.com/Oheers/EvenMoreFish/wiki/CustomChecks#AvoidImpreciseRounding",
        linkType = BugPattern.LinkType.CUSTOM,
        severity = SUGGESTION
)
public class AvoidImpreciseRounding extends BugChecker implements BugChecker.MethodInvocationTreeMatcher {

    @Override
    public Description matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
        // Match Math.round(value * scale) / scale
        if (Matchers.methodInvocation(
                        Matchers.staticMethod()
                                .onClass("java.lang.Math")
                                .named("round"))
                .matches(tree, state)) {

            // Extract the method call as a string
            String methodCallString = tree.toString();

            // Check if it is followed by division (e.g., Math.round(value * 10) / 10)
            if (methodCallString.matches(".*Math\\.round\\(.*\\) / .*")) {
                return buildDescription(tree)
                        .setMessage("Avoid using Math.round followed by division for formatting; use DecimalFormat or String.format instead.")
                        .build();
            }
        }

        // Match toString() methods converting the result
        if (Matchers.methodInvocation(
                        Matchers.anyOf(
                                Matchers.staticMethod().onClass("java.lang.Float").named("toString"),
                                Matchers.staticMethod().onClass("java.lang.Double").named("toString"),
                                Matchers.staticMethod().onClass("java.lang.String").named("valueOf")
                        ))
                .matches(tree, state)) {

            // Check if the method involves Math.round in its argument
            String methodCallString = tree.toString();
            if (methodCallString.matches(".*Math\\.round\\(.*\\).*")) {
                return buildDescription(tree)
                        .setMessage("Avoid converting a rounded value to String directly; use DecimalFormat or String.format instead.")
                        .build();
            }
        }

        return Description.NO_MATCH;
    }
}

