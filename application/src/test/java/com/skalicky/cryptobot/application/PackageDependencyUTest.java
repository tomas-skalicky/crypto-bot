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

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import javax.annotation.Nonnull;

import static com.skalicky.cryptobot.application.TestConstants.BASE_PACKAGE;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideOutsideOfPackage;
import static com.tngtech.archunit.lang.conditions.ArchConditions.onlyDependOnClassesThat;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = BASE_PACKAGE)
public class PackageDependencyUTest {

    @ArchTest
    @Nonnull
    static final ArchRule test_dependencies_when_sharedPackage_then_dependsOnlyOn_shared_or_nonProject_packages =
            classes().that().resideInAPackage(BASE_PACKAGE + "..shared..")
                    .should(onlyDependOnClassesThat(
                            resideInAPackage(BASE_PACKAGE + "..shared..")
                                    .or(resideOutsideOfPackage(BASE_PACKAGE + ".."))));

    @ArchTest
    @Nonnull
    static final ArchRule test_dependencies_when_slackPackages_then_dependsOnlyOn_shared_or_slack_or_nonProject_packages =
            classes().that().resideInAPackage(BASE_PACKAGE + "..slack..")
                    .should(onlyDependOnClassesThat(
                            resideInAnyPackage(BASE_PACKAGE + "..shared..", BASE_PACKAGE + "..slack..")
                                    .or(resideOutsideOfPackage(BASE_PACKAGE + ".."))));

    @ArchTest
    @Nonnull
    static final ArchRule test_dependencies_when_tradingPlatformPackages_then_dependsOnlyOn_shared_or_tradingPlatform_or_nonProject_packages =
            classes().that().resideInAPackage(BASE_PACKAGE + "..tradingplatform..")
                    .should(onlyDependOnClassesThat(
                            resideInAnyPackage(BASE_PACKAGE + "..shared..",
                                    BASE_PACKAGE + "..tradingplatform..")
                                    .or(resideOutsideOfPackage(BASE_PACKAGE + ".."))));

    @ArchTest
    @Nonnull
    static final ArchRule test_dependencies_when_krakenPackages_then_dependsOnlyOn_shared_or_tradingPlatform_or_kraken_or_nonProject_packages =
            classes().that().resideInAPackage(BASE_PACKAGE + "..kraken..")
                    .should(onlyDependOnClassesThat(
                            resideInAnyPackage(BASE_PACKAGE + "..shared..",
                                    BASE_PACKAGE + "..tradingplatform..", BASE_PACKAGE + "..kraken..")
                                    .or(resideOutsideOfPackage(BASE_PACKAGE + ".."))));

    @ArchTest
    @Nonnull
    static final ArchRule test_dependencies_when_poloniexPackages_then_dependsOnlyOn_shared_or_tradingPlatform_or_poloniex_or_nonProject_packages =
            classes().that().resideInAPackage(BASE_PACKAGE + "..poloniex..")
                    .should(onlyDependOnClassesThat(
                            resideInAnyPackage(BASE_PACKAGE + "..shared..",
                                    BASE_PACKAGE + "..tradingplatform..", BASE_PACKAGE + "..poloniex..")
                                    .or(resideOutsideOfPackage(BASE_PACKAGE + ".."))));

}
