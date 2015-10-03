Feature: PDOK WFS Services

Scenario: Get Capabilities wfs secure
Given The following wfs services: bag, brpgewaspercelen, beschermdenatuurmonumenten
When I ask wfs get capabilities secure
Then I get correct answer

Scenario: Get Capabilities wfs not secure
Given The following wfs services: bag, brpgewaspercelen, beschermdenatuurmonumenten
When I ask wfs get capabilities not secure
Then I get correct answer
