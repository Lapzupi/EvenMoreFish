package com.oheers.fish.errors;

import com.google.errorprone.CompilationTestHelper;
import org.junit.jupiter.api.Test;

class AvoidImpreciseRoundingTest {
    private final CompilationTestHelper helper =
            CompilationTestHelper.newInstance(AvoidImpreciseRounding.class, getClass());

    @Test
    void testPositiveCase() {
        helper
                .addSourceLines("Test.java",
                        "public class Test {",
                        "    public void test() {",
                        "        // BUG: Diagnostic contains:", //this must be here.
                        "        Float.toString(Math.round(3.456f * 10f) / 10f);",
                        "    }",
                        "}")
                .doTest();
    }

    @Test
    void testNegativeCase() {
        helper.addSourceLines(
                "Test.java",
                "class Test {",
                "  void test() {",
                "    java.text.DecimalFormat df = new java.text.DecimalFormat(\"#.##\");",
                "    df.format(3.456);",
                "  }",
                "}"
        ).doTest();
    }
}
