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

import static com.skalicky.cryptobot.application.TestConstants.BASE_PACKAGE;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = BASE_PACKAGE)
public class LayeredArchitectureUTest {

    private static final String APPLICATION_LAYER_NAME = "Application";
    private static final String BUSINESS_LOGIC_API_LAYER_NAME = "BusinessLogicApi";
    private static final String BUSINESS_LOGIC_IMPL_LAYER_NAME = "BusinessLogicImpl";
    private static final String EXCHANGE_CONNECTOR_FACADE_API_LAYER_NAME = "ExchangeConnectorFacadeApi";
    private static final String EXCHANGE_CONNECTOR_FACADE_IMPL_LAYER_NAME = "ExchangeConnectorFacadeImpl";
    private static final String EXCHANGE_CONNECTOR_API_LAYER_NAME = "ExchangeConnectorApi";
    private static final String EXCHANGE_CONNECTOR_IMPL_LAYER_NAME = "ExchangeConnectorImpl";

    @ArchTest
    static final ArchRule test_layers_when_accessesRespectLayers_then_ok = layeredArchitecture()

            .layer(APPLICATION_LAYER_NAME).definedBy(BASE_PACKAGE + ".application..")
            .layer(BUSINESS_LOGIC_API_LAYER_NAME).definedBy(BASE_PACKAGE + ".businesslogic.api..")
            .layer(BUSINESS_LOGIC_IMPL_LAYER_NAME).definedBy(BASE_PACKAGE + ".businesslogic.impl..")
            .layer(EXCHANGE_CONNECTOR_FACADE_API_LAYER_NAME).definedBy(
                    BASE_PACKAGE + ".exchange.*.connectorfacade.api..")
            .layer(EXCHANGE_CONNECTOR_FACADE_IMPL_LAYER_NAME).definedBy(
                    BASE_PACKAGE + ".exchange.*.connectorfacade.impl..")
            .layer(EXCHANGE_CONNECTOR_API_LAYER_NAME).definedBy(
                    BASE_PACKAGE + ".exchange.*.connector.api..")
            .layer(EXCHANGE_CONNECTOR_IMPL_LAYER_NAME).definedBy(
                    BASE_PACKAGE + ".exchange.*.connector.impl..")

            .whereLayer(APPLICATION_LAYER_NAME).mayNotBeAccessedByAnyLayer()
            .whereLayer(BUSINESS_LOGIC_API_LAYER_NAME).mayOnlyBeAccessedByLayers(
                    APPLICATION_LAYER_NAME,
                    BUSINESS_LOGIC_IMPL_LAYER_NAME)
            .whereLayer(BUSINESS_LOGIC_IMPL_LAYER_NAME).mayOnlyBeAccessedByLayers(
                    APPLICATION_LAYER_NAME)
            .whereLayer(EXCHANGE_CONNECTOR_FACADE_API_LAYER_NAME).mayOnlyBeAccessedByLayers(
                    APPLICATION_LAYER_NAME,
                    BUSINESS_LOGIC_IMPL_LAYER_NAME,
                    EXCHANGE_CONNECTOR_FACADE_IMPL_LAYER_NAME)
            .whereLayer(EXCHANGE_CONNECTOR_FACADE_IMPL_LAYER_NAME).mayOnlyBeAccessedByLayers(
                    APPLICATION_LAYER_NAME)
            .whereLayer(EXCHANGE_CONNECTOR_API_LAYER_NAME).mayOnlyBeAccessedByLayers(
                    APPLICATION_LAYER_NAME,
                    EXCHANGE_CONNECTOR_FACADE_IMPL_LAYER_NAME,
                    EXCHANGE_CONNECTOR_IMPL_LAYER_NAME)
            .whereLayer(EXCHANGE_CONNECTOR_IMPL_LAYER_NAME).mayOnlyBeAccessedByLayers(
                    APPLICATION_LAYER_NAME);

}
