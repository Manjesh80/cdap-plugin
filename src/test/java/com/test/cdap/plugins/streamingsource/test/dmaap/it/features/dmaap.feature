Feature: Test DMaaP plugin in a sample pipeline to check for basic functionality
  In order for the pipelines to function properly
  As a developer
  I want to specify a basic pipeline for DMaaP

  Scenario: Run a simple DMaaP pipeline
    Given dmaap pipeline is setup
    When sample message is sent to the DMaaP topic
    Then sample collection container should have sample messages