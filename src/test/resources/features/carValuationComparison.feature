Feature: Car Comparison Validation

  Scenario: Car Comparison Validation from a webuyanycar.com site
    Given I go to the car valuation website
    When the vehicle registration numbers are extracted from 'car_input.txt' file
    And I search each extracted car number on the valuation website with random mileage
    Then I verify the results with 'car_output.txt' file