/*
 * A program to automatically trade cryptocurrencies.
 * Copyright (C) 2020 Tomas Skalicky
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.skalicky.cryptobot.application;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.skalicky.cryptobot.application.TestConstants.BASE_PACKAGE;
import static com.tngtech.archunit.lang.conditions.ArchConditions.beAnnotatedWith;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

/**
 * Note it would be better to implement this check with Checkstyle using a "raw" java code, not to use ArchUnit using
 * a bytecode.
 */
@AnalyzeClasses(packages = BASE_PACKAGE)
public class MandatoryAnnotationUTest {

    @Nonnull
    private static final DescribedPredicate<JavaClass> isPrimitive = JavaClass.Predicates.belongToAnyOf(byte.class,
            short.class, int.class, long.class, float.class, double.class, char.class, boolean.class);
    @Nonnull
    private static final DescribedPredicate<JavaClass> isPrimitiveOrVoid = JavaClass.Predicates.belongToAnyOf(byte.class,
            short.class, int.class, long.class, float.class, double.class, char.class, boolean.class, void.class);

    @ArchTest
    @Nonnull
    static final ArchRule test_fields_when_notDeclaredInEnums_and_haveNonPrimitiveType_and_haveNonGeneratedName_then_mustBeAnnotatedWithNullnessAnnotation =
            fields()

                    .that()
                    .areDeclaredInClassesThat(DescribedPredicate.not(JavaClass.Predicates.ENUMS))
                    .and().doNotHaveRawType(isPrimitive)
                    .and().haveNameNotMatching(".*\\$.*")

                    .should(beAnnotatedWith(Nullable.class).or(beAnnotatedWith(Nonnull.class)));

    @ArchTest
    @Nonnull
    static final ArchRule test_fields_when_declaredInEnums_and_haveNonPrimitiveType_and_haveNonGeneratedName_and_haveNonEnumValueType_then_mustBeAnnotatedWithNullnessAnnotation =
            fields()

                    .that()
                    .areDeclaredInClassesThat(JavaClass.Predicates.ENUMS)
                    .and().doNotHaveRawType(isPrimitive)
                    .and().haveNameNotMatching(".*\\$.*")
                    // This predicate is necessary due to enum values in enums. However, the predicate excludes too much
                    // (also ordinary enum fields).
                    .and().doNotHaveRawType(JavaClass.Predicates.ENUMS)

                    .should(beAnnotatedWith(Nullable.class).or(beAnnotatedWith(Nonnull.class)));

    @ArchTest
    @Nonnull
    static final ArchRule test_methods_when_notDeclaredInEnums_and_returnNonPrimitiveTypeAndNonVoid_and_haveNonGeneratedName_then_mustBeAnnotatedWithNullnessAnnotation =
            methods()

                    .that()
                    .areDeclaredInClassesThat(DescribedPredicate.not(JavaClass.Predicates.ENUMS))
                    .and().doNotHaveRawReturnType(isPrimitiveOrVoid)
                    .and().haveNameNotMatching(".*\\$.*")

                    .should(beAnnotatedWith(Nullable.class).or(beAnnotatedWith(Nonnull.class)));

    @ArchTest
    @Nonnull
    static final ArchRule test_methods_when_declaredInEnums_and_returnNonPrimitiveTypeAndNonVoid_and_haveNonGeneratedName_and_areNotAutomaticallyProvided_then_mustBeAnnotatedWithNullnessAnnotation =
            methods()

                    .that()
                    .areDeclaredInClassesThat(JavaClass.Predicates.ENUMS)
                    .and().doNotHaveRawReturnType(isPrimitiveOrVoid)
                    .and().haveNameNotMatching(".*\\$.*")
                    .and().doNotHaveName("valueOf")
                    .and().doNotHaveName("values")

                    .should(beAnnotatedWith(Nullable.class).or(beAnnotatedWith(Nonnull.class)));

}
