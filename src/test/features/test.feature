Feature: test Daily video call between two users
  Scenario Outline: two users connect successfully
    Given host opens the app
    And host paste in the demo room link
    And host clicks "join"
    Given guest opens the app
    And guest paste in the demo room link
    And guest clicks "join"
    And guest wait until host camera on
    And verify host side video call started when reached minimum similarity threshold "<minThresholdValue>" within "<timeout>" ms
    And verify guest side video call started when reached minimum similarity threshold "<minThresholdValue>" within "<timeout>" ms

  Examples:
    | minThresholdValue | timeout |
    | 0.90              | 5000    |


  Scenario Outline: one user click join, the other does not, video won't be connected
    Given host opens the app
    And host paste in the demo room link
    Given guest opens the app
    And guest paste in the demo room link
    And guest clicks "join"
    And verify host side video call started when reached minimum similarity threshold "<minThresholdValue>" within "<timeout>" ms
    And verify guest side video call started when reached minimum similarity threshold "<minThresholdValue>" within "<timeout>" ms

    Examples:
      | minThresholdValue | timeout |
      | 0.90              | 5000    |