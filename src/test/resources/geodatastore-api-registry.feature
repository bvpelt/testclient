Feature: Geodatastore api registry
  The geodatastore api documentation https://test.geodatastore.pdok.nl/api/v1/docs
  shows the documentation of the registry services
  The basic operation is:
  - get a list of known registry services
  - for each known registry service call that registry service


  @Registries @Registry
  Scenario: Get list of known registry services
  This scenario implements get a list of known registry services
  There are 4 known registry services denominator, license, topicCategory and location

    Given There is a testclient
    When I ask for known registry services
    Then I get a http success status
    And I get a list of 4 known registrie services with names 'denominator, license, topicCategory, location'

  @Registrie @Registry
  Scenario Outline: Query registry services
  This scenario implements calling each of the known registry services denominator, license, topicCategory and location
  For each known registry service a call should al least return a http 200 status

    Given There is a testclient
    When I ask register service <Regserver> without filtering
    Then I get http status <Httpstatus>

    Examples:
      | Regserver     | Httpstatus |
      | denominator   | 200        |
      | license       | 200        |
      | topicCategory | 200        |
      | location      | 200        |

  @RegistrieParam @Registry
  Scenario Outline: Query registry services with parameters
    Given There is a testclient
    When I ask register service <Regserver> with parameter <Parameter>
    Then I get http status <Httpstatus>

    Examples:
      | Regserver     | Httpstatus | Parameter        |
      | denominator   | 200        | pageSize=1       |
      | denominator   | 200        | pageSize=1&q=10  |
      | license       | 200        | pageSize=1       |
      | license       | 200        | q=zero           |
      | topicCategory | 200        | pageSize=1       |
      | location      | 200        | pageSize=1       |
      | location      | 200        | pageSize=4       |
      | location      | 200        | pageSize=10&q=am |




