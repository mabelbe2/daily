Feature: test Daily video call between two users
  Scenario: get sample screenshots
    Given host opens the app
    And host paste in the demo room link
    And host clicks "join"
    When guest opens the app
    And guest paste in the demo room link
    And guest clicks "join"
    Then guest wait until host camera on
    And screenshot host after 5 second wait
    And screenshot guest after 5 second wait
    And host leaves video call room
    And guest leaves video call room

  Scenario Outline: Postivie Test case: two users connect successfully
    Given host opens the app
    And host paste in the demo room link
    And host clicks "join"
    When guest opens the app
    And guest paste in the demo room link
    And guest clicks "join"
    Then guest wait until host camera on
    And verify host side video call started when reached minimum similarity threshold "<minVideoSimilarityThreshold>" within "<videoTimeout>" seconds
    And verify guest side video call started when reached minimum similarity threshold "<minVideoSimilarityThreshold>" within "<videoTimeout>" seconds
    And verify host should be able to hear guest audio input within "<audioTimeout>" seconds
    And host leaves video call room
    And guest leaves video call room
  Examples:
    | minVideoSimilarityThreshold | videoTimeout |  audioTimeout |
    | 0.90                        | 15           |   10          |


  Scenario Outline: Negative test case: host clicks join, guest does not click join, host won't be able to see guest
    Given host opens the app
    And host paste in the demo room link
    When guest opens the app
    And guest paste in the demo room link
    And guest clicks "join"
    #    next step should fail
    Then verify host side video call started when reached minimum similarity threshold "<minVideoSimilarityThreshold>" within "<videoTimeout>" seconds
    And verify guest side video call started when reached minimum similarity threshold "<minVideoSimilarityThreshold>" within "<videoTimeout>" seconds
    And host leaves video call room
    And guest leaves video call room
    Examples:
      | minVideoSimilarityThreshold | videoTimeout |
      | 0.90                        | 10           |

  Scenario Outline: Negative test case: when call connected, if guest mute own mic, host would fail to hear guest input
    Given host opens the app
    And host paste in the demo room link
    And host clicks "join"
    When guest opens the app
    And guest paste in the demo room link
    And guest clicks "join"
    Then verify host side video call started when reached minimum similarity threshold "<minVideoSimilarityThreshold>" within "<videoTimeout>" seconds
    And verify guest side video call started when reached minimum similarity threshold "<minVideoSimilarityThreshold>" within "<videoTimeout>" seconds
    And guest mutes own mic
#    next step should fail
    And verify host should be able to hear guest audio input within "<audioTimeout>" seconds
    And host leaves video call room
    And guest leaves video call room
    Examples:
      | minVideoSimilarityThreshold | videoTimeout |  audioTimeout |
      | 0.90                        | 10           |  10           |