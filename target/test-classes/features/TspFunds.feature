Feature: TSP Funds Navigation

  Scenario: Uncheck a specific Lifecycle fund
    Given I navigate to the TSP Lifecycle funds page
    When I uncheck the "L 2035" fund checkbox
    Then the script ends successfully

  Scenario: Navigate to Annuity Calculator and start
    Given I navigate to the TSP homepage
    And I go to the Annuity Calculator page
    When I click the Start button
    And I enter "70" for the age
    And I enter "90000" for the account balance
    Then the calculator should be active