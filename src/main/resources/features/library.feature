Feature: Library fine calculation
  The system must calculate the fine based on book data and overdue days.

  Scenario Outline: Calculate fine for various overdue scenarios
    Given the library service is available
    And a book with ID <bookId> exists in the database
    When I request data for book <bookId>
    And I send a request to calculate the fine for the retrieved book
    Then the API returns status code <statusCode>
    And the total fine should be <expectedFine>

    Examples:
      | bookId | statusCode | expectedFine | comment              |
      | 2      | 200        | 0.0          | Zero overdue         |
      | 52     | 200        | 0.1          | Standard overdue     |
      | 53     | 404        | 0.0          | Invalid ID           |