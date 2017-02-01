# Created by mg153v at 1/31/2017
Feature: test dmaap plugin using hydratorbase
  In order to make sure DMaaP plugin can function independently
  As a plugin developer
  I want to specify the below plugin flow

  Background: start the dmaap test pipeline
    Given plugin is available
    When the hydrator-test-base is loaded
    Then the plugin should be loaded properly
    Then the pipeline is running

  Scenario: plugin works for sample data
    Given dmaap publisher is pushing the sample data
    When the plugin receives the sample data
    Then the plugin should send the data to sink

  Scenario: plugin works for CEF data
    Given dmaap publisher is pushing the CEF data
    When the plugin receives the CEF data
    Then the plugin should validate CEF sink
    Then the plugin should send the data to sink