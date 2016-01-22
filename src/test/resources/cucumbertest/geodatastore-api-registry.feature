Feature: Geodatastore api registry

  Scenario: Get list of known registrie services
    Given There is a testclient
    When I ask for known registrie services
    Then I get a http success status
    And I get a list of 4 known registrie services with names 'denominator, license, topicCategory, location'

  Scenario Outline: Query registry services
    Given There is a testclient
    When I ask register service <Regserver> without filtering
    Then I get http status <Httpstatus>

    Examples:
     | Regserver      | Httpstatus |
     | denominator    | 200        |
     | license        | 200        |
     | topicCategory  | 200        |
     | location       | 200        |


  Scenario Outline: Query registry services with parameters
    Given There is a testclient
    When I ask register service <Regserver> with parameter <Parameter>
    Then I get http status <Httpstatus>

    Examples:
      | Regserver      | Httpstatus | Parameter         |
      | denominator    | 200        |  pageSize=1       |
      | denominator    | 200        |  pageSize=1&q=10  |
      | license        | 200        |  pageSize=1       |
      | license        | 200        |  q=zero           |
      | topicCategory  | 200        |  pageSize=1       |
      | location       | 200        |  pageSize=1       |
      | location       | 200        |  pageSize=4       |
      | location       | 200        |  pageSize=10&q=am |
